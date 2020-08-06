package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepo extends JpaRepository<UserInfo, Long> {
    UserInfo findByUser(User uidUser);

    UserInfo findByChatId(Long chatId);
}