package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.RefDescription;
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
public interface RefDescriptionRepo extends JpaRepository<RefDescription, Long> {
    RefDescription findByUid(Long uid);

    List<RefDescription> findByUidUser(Long uid);

    Page<RefDescription> findByUidUser(Long uid, Pageable page);
}