package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.PrivateMessage;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivateMessageRepo extends JpaRepository<PrivateMessage, Long> {

    PrivateMessage findByUid(Long uid);

    List<PrivateMessage> findBySender(User sender, Pageable page);

    List<PrivateMessage> findByRecipient(User recipient);

    List<PrivateMessage> findByRecipient(User recipient, Pageable page);

    boolean existsBySenderAndRecipientAndReferenceDescriptionAndAcceptionStatusEquals (User sender, User recipient,
                                                                                         ReferenceDescription referenceDescription,
                                                                                         Integer acceptionStatusUid);
}
