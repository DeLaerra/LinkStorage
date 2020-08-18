package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.Tags;
import com.innopolis.referencestorage.domain.TagsRefs;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.repos.ReferenceDescriptionRepo;
import com.innopolis.referencestorage.repos.TagsRefsRepo;
import com.innopolis.referencestorage.repos.TagsRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ReferenceSearchService {
    private final EntityManager centityManager;
    private TagsRepo tagsRepo;
    private TagsRefsRepo tagsRefsRepo;
    private ReferenceDescriptionRepo referenceDescriptionRepo;

    @Autowired
    public ReferenceSearchService(final EntityManagerFactory entityManagerFactory, ReferenceDescriptionRepo referenceDescriptionRepo,
                                  TagsRepo tagsRepo, TagsRefsRepo tagsRefsRepo) {
        super();
        this.centityManager = entityManagerFactory.createEntityManager();
        this.referenceDescriptionRepo = referenceDescriptionRepo;
        this.tagsRepo = tagsRepo;
        this.tagsRefsRepo = tagsRefsRepo;
    }

    @PostConstruct
    public void initializeHibernateSearch() {
        try {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            log.error("Ошибка при старте fullTextEntityManager", e);
        }
    }

    @Transactional
    public List<ReferenceDescription> fullTextSearchAllReferences(String searchTerm, User currentUser) {
        List<ReferenceDescription> searchResults = new ArrayList<>();
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(ReferenceDescription.class).get();
        Query luceneQuery = null;
        try {
            luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1)
                    .onFields("name")
                    .andField("description")
                    .andField("source")
                    .andField("reference.url")
                    //                .andField("tag")
                    .matching(searchTerm).createQuery();
        } catch (Exception e) {
            log.error("Ошибка при попытке создания поискового запроса по терминам \"{}\"", searchTerm, e);
            return searchResults;
        }

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, ReferenceDescription.class);
        return executeJpaQuery(jpaQuery, currentUser);
    }

    @Transactional
    public List<ReferenceDescription> fullTextSearchReferencesByUserUid(String searchTerm, User user) {
        List<ReferenceDescription> searchResults = new ArrayList<>();
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(ReferenceDescription.class).get();


        QueryParser parser = new QueryParser("", new StandardAnalyzer());
        Query luceneQuery;
        try {
            luceneQuery = parser.parse(searchTerm);
        } catch (ParseException e) {
            log.error("Невозможно распарсить запрос " + searchTerm, e);
        }

        try {
            luceneQuery = qb
                    .bool()
                    .must(qb
                            .keyword()
                            .onField("uidUser")
                            .matching(user.getUid())
                            .createQuery())
                    .must(qb
                            .keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1)
                            .onFields("name")
                            .andField("reference.url")
                            .andField("description")
                            .andField("source")
                            //                        .andField("tag")
                            .matching(searchTerm).createQuery())
                    .createQuery();
        } catch (Exception e) {
            log.error("Ошибка при попытке создания поискового запроса по терминам \"{}\"", searchTerm, e);
            return searchResults;
        }

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, ReferenceDescription.class);
        log.info("Запрос {} от пользователя с uid {}", jpaQuery, user.getUid());
        return executeJpaQuery(jpaQuery, user);
    }

    @Transactional
    public List<ReferenceDescription> fullTextSearchPublicReferencesOnly(String searchTerm, User user) {
        List<ReferenceDescription> searchResults = new ArrayList<>();
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(ReferenceDescription.class).get();
        Query luceneQuery;
        try {
            luceneQuery = qb
                    .bool()
                    .must(qb
                            .keyword()
                            .onField("uidAccessLevel")
                            .matching(0)
                            .createQuery())
                    .must(qb
                            .keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1)
                            .onFields("name").boostedTo(2)
                            .andField("reference.url")
                            .andField("description")
                            .andField("source")
                            //                        .andField("tag")
                            .matching(searchTerm).createQuery())
                    .createQuery();
        } catch (Exception e) {
            log.error("Ошибка при попытке создания поискового запроса по терминам \"{}\"", searchTerm, e);
            return searchResults;
        }

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, ReferenceDescription.class);
        log.info("Запрос {} от пользователя с uid {}", jpaQuery, user.getUid());
        return executeJpaQuery(jpaQuery, user);
    }

    private List<ReferenceDescription> executeJpaQuery(javax.persistence.Query jpaQuery, User user) {
        List<ReferenceDescription> refs = new ArrayList<>();
        try {
            refs = jpaQuery.getResultList();
        } catch (NoResultException nre) {
            log.error("Ничего не найдено по запросу " + jpaQuery, nre);
        }
        try {
            refs = filterDuplicateReferenceDescriptions(refs, user);
        } catch (NullPointerException e) {
            log.error("Ничего не найдено.", e);
        }

        return refs;
    }

    private List<ReferenceDescription> filterDuplicateReferenceDescriptions(List<ReferenceDescription> sourceRefs, User user) {
        return sourceRefs.stream()
                .collect(Collectors
                        .groupingBy((ReferenceDescription referenceDescription) -> referenceDescription.getReference().getUid(),
                                Collectors.minBy(Comparator
                                                .comparing(ReferenceDescription::getAdditionDate))))
                .values()
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<ReferenceDescription> fullTagSearchPublicReferencesOnly(String q, User user) {
        Set<ReferenceDescription> result = new HashSet<>();
        List<Tags> foundTags = tagsRepo.findAllByName(q.replaceFirst("#", ""));
        List<TagsRefs> foundTagsRefs = new ArrayList<>();
        if (!foundTags.isEmpty()) {
            for (Tags tags : foundTags) {
                foundTagsRefs.add(tagsRefsRepo.findByUidTag(tags.getUid()));
            }
            while (foundTagsRefs.remove(null));
            if (!foundTagsRefs.isEmpty()) {
                for (TagsRefs tagsRefs : foundTagsRefs) {
                    result.add(referenceDescriptionRepo.findByUid(tagsRefs.getUidRefDescription()));
                }
                result.removeIf(referenceDescription -> referenceDescription.getUidAccessLevel() == 1);
            }
        }
        return new ArrayList<>(result);
    }

    public List<ReferenceDescription> fullTagSearchReferencesByUserUid(String q, User user) {
        Set<ReferenceDescription> result = new HashSet<>();
        List<Tags> foundTags = tagsRepo.findAllByName(q.replaceFirst("#", ""));
        List<TagsRefs> foundTagsRefs = new ArrayList<>();
        for (Tags tags : foundTags) {
            foundTagsRefs.add(tagsRefsRepo.findByUidTag(tags.getUid()));
        }
        for (TagsRefs tagsRefs : foundTagsRefs) {
            result.add(referenceDescriptionRepo.findByUidAndUidUser(tagsRefs.getUidRefDescription(), user.getUid()));
        }
        return new ArrayList<>(result);
    }
}
