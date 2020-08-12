package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.FriendshipRequest;
import com.innopolis.referencestorage.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * FriendshipRequestRepo.
 *
 * @author Roman Khokhlov
 */

public interface FriendshipRequestRepo extends JpaRepository<FriendshipRequest, Long> {
    FriendshipRequest findByUid(Long uid);

    List<FriendshipRequest> findBySender(User sender, Pageable page);

    List<FriendshipRequest> findByRecipient(User recipient, Pageable page);

    List<FriendshipRequest> findByRecipient(User recipient);

    boolean existsBySenderAndRecipientAndAcceptionStatusEquals(User sender, User recipient,
                                                               Integer acceptionStatusUid);
}
