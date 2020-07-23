package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.domain.UserInfo;
import com.innopolis.referencestorage.repos.UserInfoRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Base64;

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
        try {
            userInfo.setAvatar(Files.readAllBytes(Paths.get("src\\resources\\otherFiles\\defaultAvatar.png")));
        } catch (IOException e) {
            log.error("Нет файла defaultAvatar.png");
        }
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

    public void checkAndAddData(User user, UserInfo userInfo, String birthDate, MultipartFile file) {
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
        if (!(file == null)) {
            if ((!file.getOriginalFilename().toUpperCase().endsWith(".JPG"))
                    && (!file.getOriginalFilename().toUpperCase().endsWith(".PNG"))
                    && (!file.getOriginalFilename().toUpperCase().endsWith(".BMP"))) {
                log.info("Был загружен файл, который не является картинкой, не меняется.");
            } else {
                log.info("Попытка преобразовать в байты и сохранить в файл в базе данных");
                try {
                    byte[] bytes = file.getBytes();
                    userInfo.setAvatar(bytes);
                } catch (IOException e) {
                    log.error("Ошибка ввода-вывода при попытке преобразования файла в байты");
                }
            }
        }
    }

    public String getFileFromUserInfo(byte[] avatarBytes) {
        if (avatarBytes != null) {
            byte[] encoded = Base64.getEncoder().encode(avatarBytes);
            String imgDataAsBase64 = new String(encoded);
            String imgAsBase64 = "data:image/png;base64," + imgDataAsBase64;
            return imgAsBase64;
        }
        return "";
    }
}