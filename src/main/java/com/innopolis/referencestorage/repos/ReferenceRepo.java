package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.Reference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ReferenceRepo.
 *
 * @author Roman Khokhlov
 */
@Repository
public interface ReferenceRepo extends JpaRepository<Reference, Long> {
    List<Reference> findByTag(String tag);

    List<Reference> findByUidUser(Long uid);

    Page<Reference> findByUidUser(Long uid, Pageable page);
}