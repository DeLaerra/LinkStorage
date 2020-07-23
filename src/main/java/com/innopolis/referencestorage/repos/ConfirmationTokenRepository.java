package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.ConfirmationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * ConfirmationTokenRepository.
 *
 * @author Roman Khokhlov
 */
@Repository
public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, String> {
    ConfirmationToken findByConfirmationToken(String confirmationToken);
}

