package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendsRepo extends JpaRepository<Friends, Long> {
    List<Friends> findAllByFriend(long friend);
    List<Friends> findAllByOwner(long owner);
}
