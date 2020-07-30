package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UserRepo.
 *
 * @author Roman Khokhlov
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUid(Long uid);

    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findByUsernameLike(String username);
}