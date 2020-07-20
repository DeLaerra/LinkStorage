package com.innopolis.referencestorage.repos;

import com.innopolis.referencestorage.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepo extends JpaRepository<UserInfo, Long> {
    UserInfo findByUidUser(Long uid_user);
}