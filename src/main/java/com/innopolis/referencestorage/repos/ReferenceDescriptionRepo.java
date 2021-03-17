package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.Reference;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

/**
 * ReferenceRepo.
 *
 * @author Roman Khokhlov
 */
@Repository
public interface ReferenceDescriptionRepo extends JpaRepository<ReferenceDescription, Long> {

    ReferenceDescription findByUid(Long uid);

    ArrayList<ReferenceDescription> findByUidUser(Long uid);

    Page<ReferenceDescription> findByUidUser(Long uid, Pageable page);

    ReferenceDescription findAnyByUidUserAndReference(Long uid, Reference reference);

    ReferenceDescription findByUidAndUidUser(Long uid, Long uidUser);

    boolean existsByReferenceUidAndUidUser(Long refUid, Long uidUser);

}