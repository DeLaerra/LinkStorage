package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.domain.FriendshipRequest;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.enums.AcceptionStatus;
import com.innopolis.referencestorage.repos.FriendshipRequestRepo;
import com.innopolis.referencestorage.repos.UserRepo;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * FriendshipRequestService.
 *
 * @author Roman Khokhlov
 */
@Service
@Slf4j
@NoArgsConstructor
public class FriendshipRequestService {
    private FriendshipRequestRepo friendshipRequestRepo;
    private UserRepo userRepo;
    private FriendsService friendsService;

    @Autowired
    public FriendshipRequestService(FriendshipRequestRepo friendshipRequestRepo,
                                    UserRepo userRepo, FriendsService friendsService) {
        this.friendshipRequestRepo = friendshipRequestRepo;
        this.userRepo = userRepo;
        this.friendsService = friendsService;
    }

    public List<FriendshipRequest> getInbox(User recipient, Pageable pageable) {
        return friendshipRequestRepo.findByRecipient(recipient, pageable);
    }

    public List<FriendshipRequest> getSent(User sender, Pageable pageable) {
        return friendshipRequestRepo.findBySender(sender, pageable);
    }

    public boolean isEmptyInbox(User recipient) {
        List<FriendshipRequest> frreq = friendshipRequestRepo.findByRecipient(recipient);
        for (FriendshipRequest fr : frreq) {
            if (fr != null && fr.getAcceptionStatus() == 0) {
                return false;
            }
        }
        return true;
    }

    public void sendFriendshipRequestToUser(FriendshipRequest friendshipRequest,
                                                         User user, Long recipientUid, String text, Model model) {
        log.info("Получен запрос на добавление в друзья: \n username - {}, \n recipientUsername - {} ", user.getUsername(), userRepo.findByUid(recipientUid).getUsername());
        User friend = userRepo.findByUid(recipientUid);

        if (friendshipRequestRepo.existsBySenderAndRecipientAndAcceptionStatusEquals(userRepo.findByUid(recipientUid),
                user, 1)) {
            model.addAttribute("requestAcceptionAllow", "Принять заявку");
            ArrayList<FriendshipRequest> requests1 = friendshipRequestRepo.findBySenderAndRecipientAndAcceptionStatusEquals(userRepo.findByUid(recipientUid),
                    user, 1);
            FriendshipRequest request = requests1.get(requests1.size()-1);
            acceptRequestFromMessage(request.getUid(), user, model);
            log.error("Заявка принята");
            model.addAttribute("requestAccepted", "Заявка принята!");
        } else
        if (friendshipRequestRepo.existsBySenderAndRecipientAndAcceptionStatusEquals(userRepo.findByUid(recipientUid),
                user, 0)) {
            model.addAttribute("requestAcceptionAllow", "Принять заявку");
            ArrayList<FriendshipRequest> requests0 = friendshipRequestRepo.findBySenderAndRecipientAndAcceptionStatusEquals(userRepo.findByUid(recipientUid),
                    user, 0);
            FriendshipRequest request = requests0.get(requests0.size()-1);
            acceptRequestFromMessage(request.getUid(), user, model);
            log.error("Заявка принята");
            model.addAttribute("requestAccepted", "Заявка принята!");
        } else
        if (friendshipRequestRepo.existsBySenderAndRecipientAndAcceptionStatusEquals(userRepo.findByUid(recipientUid),
                user, 3)) {
            model.addAttribute("requestAcceptionAllow", "Принять заявку");
            ArrayList<FriendshipRequest> requests3 = friendshipRequestRepo.findBySenderAndRecipientAndAcceptionStatusEquals(userRepo.findByUid(recipientUid),
                    user, 3);
            FriendshipRequest request = requests3.get(requests3.size()-1);
            acceptRequestFromMessage(request.getUid(), user, model);
            log.error("Заявка принята");
            model.addAttribute("requestAccepted", "Заявка принята!");

        } else {
        friendshipRequest.setText(text);
        friendshipRequest.setSender(user);
        friendshipRequest.setRecipient(friend);
        friendshipRequest.setSendingTime(LocalDateTime.now());
        friendshipRequestRepo.save(friendshipRequest); }
    }

    public FriendshipRequest acceptRequestFromMessage(Long frUid, User user, Model model) {
        FriendshipRequest friendshipRequest = friendshipRequestRepo.findByUid(frUid);
        friendshipRequest.setAcceptionStatus(AcceptionStatus.ACCEPTED.getStatusUid());
        friendshipRequestRepo.saveAndFlush(friendshipRequest);
        log.info("Сообщению с uid {} присвоен статус Одобрено", frUid);
        User sender = friendshipRequest.getSender();
        friendsService.addFriends(user, sender);
        log.info("Пользователь добавлен в друзья, frUid- {}", frUid);
        return friendshipRequest;
    }

    public FriendshipRequest rejectRequestFromMessage(Long frUid) {
        log.info("Отклонение заявки в друзья, frId- {}", frUid);
        FriendshipRequest friendshipRequest = friendshipRequestRepo.findByUid(frUid);
        friendshipRequest.setAcceptionStatus(AcceptionStatus.REJECTED.getStatusUid());
        friendshipRequestRepo.saveAndFlush(friendshipRequest);
        log.info("Заявке с uid {} присвоен статус Отклонено", frUid);
        return friendshipRequest;
    }

    public void deleteMessageByUid(Long frUid) {
        FriendshipRequest friendshipRequest = friendshipRequestRepo.findByUid(frUid);
        friendshipRequestRepo.delete(friendshipRequest);
        log.info("Удаление заявки, frId- {}", frUid);
    }


    private boolean isDuplicateRequest(User sender, User recipient, Model model) {
        if (friendshipRequestRepo.existsBySenderAndRecipientAndAcceptionStatusEquals(sender,
                recipient, 0) || friendshipRequestRepo.existsBySenderAndRecipientAndAcceptionStatusEquals(sender,
                recipient, 3)) {
            log.error("Повторная отправка заявки тому же адресату");
            model.addAttribute("pmDuplicateError", "Повторная отправка заявки тому же адресату!");
        }
        return true;
    }
}
