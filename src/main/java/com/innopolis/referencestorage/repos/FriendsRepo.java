package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendsRepo extends JpaRepository<Friends, Long> {
}
