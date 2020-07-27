package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.Reference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefRepo extends JpaRepository<Reference, Long> {
        Reference findByUrl(String url);
}
