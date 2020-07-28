package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.domain.UserInfo;
import com.innopolis.referencestorage.repos.UserInfoRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
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

    public void checkAndAddData(User user, UserInfo userInfo, String birthDate, MultipartFile file, Model model) {
        log.info("Получен запрос на проверку и добавление данных к сущности UserInfo от пользователя с uid {}: \n userInfo - {}, \n birthDate - {} ", user.getUid(), userInfo, birthDate);
        UserInfo originalUserInfo = getUserInfoWithUserUID(user);
        userInfo.setUid(originalUserInfo.getUid());
        userInfo.setUidUser(originalUserInfo.getUidUser());
        String newName = userInfo.getName();
        if (newName.matches(".*\\d.*")) {
            log.info("В введенных данных имени есть цифры, присваивается имеющееся имя");
            userInfo.setName(originalUserInfo.getName());
            model.addAttribute("userInfoNameError", "В введенных вами данных были цифры");
        } else {
            userInfo.setName(newName.substring(0, 1).toUpperCase() + newName.substring(1));
        }
        String newSurname = userInfo.getSurname();
        if (newSurname.matches(".*\\d.*")) {
            log.info("В введенных данных фамилии есть цифры, присваивается имеющаяся фамилия");
            userInfo.setSurname(originalUserInfo.getSurname());
            model.addAttribute("userInfoSurnameError", "В введенных вами данных были цифры");
        } else {
            userInfo.setSurname(newSurname.substring(0, 1).toUpperCase() + newSurname.substring(1));
        }
        int newAge = userInfo.getAge();
        if (newAge > 150) {
            log.info("Больше максимального возраста");
            userInfo.setAge(originalUserInfo.getAge());
            model.addAttribute("userInfoAgeError", "Максимальный возраст (150) превышен");
        } else if (newAge < 0) {
            log.info("Меньше минимального возраста");
            userInfo.setAge(originalUserInfo.getAge());
            model.addAttribute("userInfoAgeError", "Попытка поставить возраст меньше нуля");
        }
        try {
            log.info("Попытка пропарсить birthDate {}", birthDate);
            LocalDate newDate = LocalDate.parse(birthDate);
            userInfo.setBirthDate(newDate);
        } catch (DateTimeParseException e) {
            log.info("Значение birthDate {} не парсится под LocalDate формат, присваивается оригинальное значение", birthDate);
            userInfo.setBirthDate(originalUserInfo.getBirthDate());
            model.addAttribute("userInfoBirthDateError", "Неправильная дата дня рождения!");
        }
        userInfo.setAvatar(originalUserInfo.getAvatar());
        if (!(file.getOriginalFilename().equals(""))) {
            if ((!file.getOriginalFilename().toUpperCase().endsWith(".JPG"))
                    && (!file.getOriginalFilename().toUpperCase().endsWith(".PNG"))
                    && (!file.getOriginalFilename().toUpperCase().endsWith(".BMP"))) {
                log.info("Был загружен файл, который не является картинкой, не меняется.");
                model.addAttribute("userInfoAvatarError", "Неправильный формат, нужно JPG, PNG или BMP");
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
            return "data:image/png;base64," + imgDataAsBase64;
        }
        return "";
    }
}