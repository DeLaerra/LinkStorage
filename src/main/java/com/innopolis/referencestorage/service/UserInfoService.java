package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.domain.UserInfo;
import com.innopolis.referencestorage.repos.UserInfoRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
public class UserInfoService {
    private UserInfoRepo userInfoRepo;

    @Autowired
    public UserInfoService(UserInfoRepo userInfoRepo) {
        this.userInfoRepo = userInfoRepo;
    }

    public void createUserDetails(Long userUId) {
        log.info("Получен запрос на создание новой сущности UserInfo userUId - {}", userUId);
        UserInfo userInfo = new UserInfo();
        userInfo.setUidUser(userUId);
        userInfo.setName("Name");
        userInfo.setSurname("Surname");
        userInfo.setAge(0);
        userInfo.setSex(0);
        userInfo.setBirthDate(LocalDate.now());
        userInfo.setAvatar(null);
        // TODO set avatar на аватар с вопросительным знаком
        userInfoRepo.save(userInfo);
    }

    public UserInfo getUserInfoWithUserUID(User user) {
        log.info("Получен запрос на получение данных пользователя с userUId - {}", user.getUid());
        long uid = user.getUid();
        UserInfo userInfo = userInfoRepo.findByUidUser(uid);
        return userInfo;
    }

    public void saveUserInfo(UserInfo userInfo) {
        log.info("Получен запрос на сохранение данных пользователя с userUId {} \n userUId - {}", userInfo.getUidUser(), userInfo);
        userInfoRepo.save(userInfo);
    }

    public void checkAndAddData(User user, UserInfo userInfo, String birthDate) {
        log.info("Получен запрос на проверку и добавление данных к сущности UserInfo от пользователя с uid {}: \n userInfo - {}, \n birthDate - {} ", user.getUid(), userInfo, birthDate);
        UserInfo originalUserInfo = getUserInfoWithUserUID(user);
        userInfo.setUid(originalUserInfo.getUid());
        userInfo.setUidUser(originalUserInfo.getUidUser());
        String newName = userInfo.getName();
        newName = newName.replaceAll("\\d", "");
        if (newName.equals("")) {
            log.info("Пустое имя, присваивается значение Name");
            userInfo.setName("Name");
        } else {
            userInfo.setName(newName.substring(0, 1).toUpperCase() + newName.substring(1));
        }
        String newSurname = userInfo.getSurname();
        newSurname = newSurname.replaceAll("\\d", "");
        if (newSurname.equals("")) {
            log.info("Пустая фамилия, присваивается значение Surname");
            userInfo.setSurname("Surname");
        } else {
            userInfo.setSurname(newSurname.substring(0, 1).toUpperCase() + newSurname.substring(1));
        }
        try {
            log.info("Попытка пропарсить birthDate {}", birthDate);
            LocalDate newDate = LocalDate.parse(birthDate);
            userInfo.setBirthDate(newDate);
        } catch (DateTimeParseException e) {
            log.info("Значение birthDate {} не парсится под LocalDate формат, присваивается оригинальное значение", birthDate);
            userInfo.setBirthDate(originalUserInfo.getBirthDate());
        }
        userInfo.setAvatar(originalUserInfo.getAvatar());
    }
}