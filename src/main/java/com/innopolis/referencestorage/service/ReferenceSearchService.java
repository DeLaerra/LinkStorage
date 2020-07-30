package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ReferenceSearchService {
    private final EntityManager centityManager;

    @Autowired
    public ReferenceSearchService(final EntityManagerFactory entityManagerFactory) {
        super();
        this.centityManager = entityManagerFactory.createEntityManager();
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
                    .andField("reference.url")
                    .andField("description")
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

        Query luceneQuery;
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
                            .onFields("name")
                            .andField("reference.url")
                            .andField("description")
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
}
