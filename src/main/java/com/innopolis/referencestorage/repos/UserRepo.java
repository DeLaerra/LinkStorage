package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * UserRepo.
 *
 * @author Roman Khokhlov
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByEmail(String email);
}