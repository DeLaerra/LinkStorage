package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.domain.PrivateMessage;
import com.innopolis.referencestorage.domain.ReferenceDescription;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.enums.AcceptionStatus;
import com.innopolis.referencestorage.repos.PrivateMessageRepo;
import com.innopolis.referencestorage.repos.ReferenceDescriptionRepo;
import com.innopolis.referencestorage.repos.UserRepo;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@Service
@Slf4j
@NoArgsConstructor
public class PrivateMessageService {
    private PrivateMessageRepo privateMessageRepo;
    private ReferenceDescriptionRepo referenceDescriptionRepo;
    private UserRepo userRepo;
    private ReferenceService referenceService;

    @Autowired
    public PrivateMessageService(PrivateMessageRepo privateMessageRepo, ReferenceDescriptionRepo referenceDescriptionRepo,
                                 UserRepo userRepo, ReferenceService referenceService) {
        this.privateMessageRepo = privateMessageRepo;
        this.referenceDescriptionRepo = referenceDescriptionRepo;
        this.userRepo = userRepo;
        this.referenceService = referenceService;
    }

    public List<PrivateMessage> getInbox(User recipient, Pageable pageable) {
        return privateMessageRepo.findByRecipient(recipient, pageable);
    }

    public List<PrivateMessage> getSent(User sender, Pageable pageable) {
        return privateMessageRepo.findBySender(sender, pageable);
    }

    public PrivateMessage sendReferenceToFriend(PrivateMessage privateMessage, Long refId,
                                                User user, String friendUsername, String text, Model model) {

        log.info("Получен запрос на отправку записи ссылки: " +
                "\n username - {}, " +
                "\n refDescriptionUid - {}, " +
                "\n friendUsername - {} ", user.getUsername(), refId, friendUsername);

        User friend = userRepo.findByUsername(friendUsername);
        ReferenceDescription sourceRef = checkDuplicateMessage(refId, user, friend, model);

        if (model.getAttribute("pmDuplicateError") != null) return null;

        privateMessage = PrivateMessage.builder()
                .text(text)
                .referenceDescription(sourceRef)
                .sender(user)
                .recipient(friend)
                .sendingTime(LocalDateTime.now())
                .addingMethodUid(sourceRef.getUidAdditionMethod())
                .acceptionStatus(AcceptionStatus.NOT_DEFINED.getStatusUid()).build();

        if (referenceDescriptionRepo.findAnyByUidUserAndReference(friend.getUid(), sourceRef.getReference()) != null) {
            log.info("Описание для ссылки уже существует в Home пользователя с uid " + friend.getUid());
            privateMessage.setAcceptionStatus(AcceptionStatus.DUPLICATE.getStatusUid());
            log.info("Сообщению с uid {} присвоен статус Дубликат", privateMessage.getUid());
        }
        return privateMessageRepo.save(privateMessage);
    }

    public void sendReferenceToFriendFromTelegram (Long refId, User user, String friendUsername) {
        log.info("Получен запрос на отправку записи ссылки из Telegram: " +
                "\n username - {}, " +
                "\n refDescriptionUid - {}, " +
                "\n friendUsername - {} ", user.getUsername(), refId, friendUsername);

        User friend = userRepo.findByUsername(friendUsername);
        ReferenceDescription sourceRef = checkDuplicateMessage(refId, user, friend, null);

        PrivateMessage privateMessage = PrivateMessage.builder()
                .referenceDescription(sourceRef)
                .sender(user)
                .recipient(friend)
                .sendingTime(LocalDateTime.now())
                .addingMethodUid(sourceRef.getUidAdditionMethod())
                .acceptionStatus(AcceptionStatus.NOT_DEFINED.getStatusUid()).build();

        if (referenceDescriptionRepo.findAnyByUidUserAndReference(friend.getUid(), sourceRef.getReference()) != null) {
            log.info("Описание для ссылки уже существует в Home пользователя с uid " + friend.getUid());
            privateMessage.setAcceptionStatus(AcceptionStatus.DUPLICATE.getStatusUid());
            log.info("Сообщению с uid {} присвоен статус Дубликат", privateMessage.getUid());
        }

        privateMessageRepo.save(privateMessage);
    }


    public PrivateMessage acceptRequestFromMessage(Long pmUid, User user, ReferenceDescription referenceDescription, Model model) {
        PrivateMessage privateMessage = privateMessageRepo.findByUid(pmUid);
        privateMessage.setAcceptionStatus(AcceptionStatus.ACCEPTED.getStatusUid());

        privateMessageRepo.saveAndFlush(privateMessage);
        log.info("Сообщению с uid {} присвоен статус Одобрено", pmUid);

        referenceService.copyReference(privateMessage.getReferenceDescription().getUid(), user, referenceDescription, model);
        log.info("Ссылка из приватного сообщения добавлена, pmId- {}", pmUid);

        return privateMessage;
    }

    public PrivateMessage rejectRequestFromMessage(Long pmUid) {
        log.info("Отказ добавления ссылки из приватного сообщения, pmId- {}", pmUid);

        PrivateMessage privateMessage = privateMessageRepo.findByUid(pmUid);
        privateMessage.setAcceptionStatus(AcceptionStatus.REJECTED.getStatusUid());

        privateMessageRepo.saveAndFlush(privateMessage);
        log.info("Сообщению с uid {} присвоен статус Отклонено", pmUid);

        return privateMessage;
    }

    public void deleteMessageByUid(Long pmUid) {
        PrivateMessage privateMessage = privateMessageRepo.findByUid(pmUid);
        privateMessageRepo.delete(privateMessage);
        log.info("Удаление приватного сообщения, pmId- {}", pmUid);
    }

    private ReferenceDescription checkDuplicateMessage(Long refId, User sender, User recipient, Model model) {
        ReferenceDescription data = referenceDescriptionRepo.findByUid(refId);
        assertNotNull(data, String.format("Указана несуществующая ссылка, refId - %s", refId));

        if (privateMessageRepo.existsBySenderAndRecipientAndReferenceDescriptionAndAcceptionStatusEquals(sender,
                recipient, data, AcceptionStatus.NOT_DEFINED.getStatusUid())
                || privateMessageRepo.existsBySenderAndRecipientAndReferenceDescriptionAndAcceptionStatusEquals(sender,
                recipient, data, AcceptionStatus.DUPLICATE.getStatusUid())) {
            log.error("Отправка сообщения c повторной ссылкой тому же адресату");
            Optional.ofNullable(model).ifPresent(m -> m.addAttribute("pmDuplicateError", "Отправка сообщения c повторной ссылкой тому же адресату!"));
        }
        return data;
    }
}