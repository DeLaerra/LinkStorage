package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserRepo.
 *
 * @author Roman Khokhlov
 */
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
}