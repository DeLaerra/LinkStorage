package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.domain.Reference;
import com.innopolis.referencestorage.domain.User;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.util.List;

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
            e.printStackTrace();
        }
    }

    @Transactional
    public Page<Reference> fullTextSearch(String searchTerm, Pageable pageable) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Reference.class).get();
        Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1)
                .onFields("name")
                .andField("url")
                .andField("description")
                .andField("tag")
                .matching(searchTerm).createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Reference.class);
        return executeJpaQuery(jpaQuery, pageable);
    }

    public Page<Reference> fullTextSearchByUserUid(String searchTerm, User user, Pageable pageable) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Reference.class).get();

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
                        .andField("url")
                        .andField("description")
                        .andField("tag")
                        .matching(searchTerm).createQuery())
                .createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Reference.class);
        return executeJpaQuery(jpaQuery, pageable);
    }

    public Page<Reference> fullTextSearchPublicOnly(String searchTerm, Pageable pageable) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(centityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Reference.class).get();
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
                        .andField("url")
                        .andField("description")
                        .andField("tag")
                        .matching(searchTerm).createQuery())
                .createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Reference.class);
        return executeJpaQuery(jpaQuery, pageable);
    }

    private Page<Reference> executeJpaQuery(javax.persistence.Query jpaQuery, Pageable pageable) {
        List<Reference> refs = null;
        try {
            refs = jpaQuery.getResultList();
        } catch (NoResultException nre) {
            nre.printStackTrace();
        }
        return new PageImpl<>(refs, pageable, refs.size());
    }
}
