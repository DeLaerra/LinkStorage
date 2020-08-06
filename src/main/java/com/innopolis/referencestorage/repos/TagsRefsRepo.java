package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.TagsRefs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
public interface TagsRefsRepo extends JpaRepository<TagsRefs, Long> {
    TagsRefs findFirstByUidRefDescriptionAndUidTag(Long uidRefDescription, Long uidTag);

    Set<TagsRefs> findAllByUidRefDescription(Long uidRefDescription);

    @Transactional
    void deleteAllByUidRefDescription(Long uidRefDescription);

    @Transactional
    void deleteByUid(Long uid);
}