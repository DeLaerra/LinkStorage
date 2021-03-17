package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TagsRepo extends JpaRepository<Tags, Long> {
    @Transactional
    void deleteByUid(Long uid);

    Tags findByUid(Long uid);

    List<Tags> findAllByName(String name);
}