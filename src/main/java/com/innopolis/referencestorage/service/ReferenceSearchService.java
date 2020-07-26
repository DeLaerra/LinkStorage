package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.util.List;

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
    public List<ReferenceDescription> fullTextSearchAllReferences(String searchTerm, Pageable pageable) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(ReferenceDescription.class).get();
        Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1)
                .onFields("name")
//                .andField("reference")
                .andField("description")
//                .andField("tag")
                .matching(searchTerm).createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, ReferenceDescription.class);
        return executeJpaQuery(jpaQuery, pageable);
    }

    public List<ReferenceDescription> fullTextSearchReferencesByUserUid(String searchTerm, User user, Pageable pageable) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(ReferenceDescription.class).get();

        Query luceneQuery = qb
                .bool()
                .must(qb
                        .keyword()
                        .onField("uidUser")
                        .matching(user.getUid())
                        .createQuery())
                .must(qb
                        .keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1)
                        .onFields("name")
//                        .andField("reference")
                        .andField("description")
//                        .andField("tag")
                        .matching(searchTerm).createQuery())
                .createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, ReferenceDescription.class);
        log.info("Запрос {} от пользователя с uid {}", jpaQuery, user.getUid());
        return executeJpaQuery(jpaQuery, pageable);
    }

    public List<ReferenceDescription> fullTextSearchPublicReferencesOnly(String searchTerm, Pageable pageable, User user) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(ReferenceDescription.class).get();
        Query luceneQuery = qb
                .bool()
                .must(qb
                        .keyword()
                        .onField("uidAccessLevel")
                        .matching(0)
                        .createQuery())
                .must(qb
                        .keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1)
                        .onFields("name")
//                        .andField("reference")
                        .andField("description")
//                        .andField("tag")
                        .matching(searchTerm).createQuery())
                .createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, ReferenceDescription.class);
        log.info("Запрос {} от пользователя с uid {}", jpaQuery, user.getUid());
        return executeJpaQuery(jpaQuery, pageable);
    }

    private List<ReferenceDescription> executeJpaQuery(javax.persistence.Query jpaQuery, Pageable pageable) {
        List<ReferenceDescription> refs = null;
        try {
            refs = jpaQuery.getResultList();
        } catch (NoResultException nre) {
            log.error("Ничего не найдено по запросу " + jpaQuery, nre);
        }
        return refs;
    }
}
