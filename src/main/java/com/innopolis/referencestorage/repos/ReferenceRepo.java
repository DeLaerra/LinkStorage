package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.Reference;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * ReferenceRepo.
 *
 * @author Roman Khokhlov
 */
public interface ReferenceRepo extends CrudRepository<Reference, Long> {

    List<Reference> findByTag(String tag);

}