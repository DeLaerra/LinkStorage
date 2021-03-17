package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.domain.UserInfo;
import com.innopolis.referencestorage.repos.UserInfoRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Base64;

@Slf4j
@Service
public class UserInfoService {
    private UserInfoRepo userInfoRepo;
    private final static String NAME_SURNAME_WARNING = "В введенных данных имени есть цифры или символы, остаётся прежнее имя/фамилия";
    private final static String ONLY_LETTERS = "Пожалуйста, используйте только буквы";

    @Autowired
    public UserInfoService(UserInfoRepo userInfoRepo) {
        this.userInfoRepo = userInfoRepo;
    }

    public UserInfo getUserInfoWithUserUID(User user) {
        log.info("Получен запрос на получение данных пользователя с userUId - {}", user.getUid());
        long uid = user.getUid();
        UserInfo userInfo = userInfoRepo.findByUser(user);
        return userInfo;
    }

    public void saveUserInfo(UserInfo userInfo) {
        log.info("Получен запрос на сохранение данных пользователя с userUId {} \n userUId - {}", userInfo.getUser().getUid(), userInfo);
        userInfoRepo.save(userInfo);
    }

    public void checkAndAddData(User user, UserInfo userInfo, String birthDate, MultipartFile file, Model model) {
        log.info("Получен запрос на проверку и добавление данных к сущности UserInfo от пользователя с uid {}: \n userInfo - {}, \n birthDate - {} ", user.getUid(), userInfo, birthDate);
        UserInfo originalUserInfo = getUserInfoWithUserUID(user);
        userInfo.setUid(originalUserInfo.getUid());
        userInfo.setUser(originalUserInfo.getUser());

        String newName = userInfo.getName();
        if (!isText(newName)) {
            log.info(NAME_SURNAME_WARNING);
            userInfo.setName(originalUserInfo.getName());
            model.addAttribute("userInfoNameError", ONLY_LETTERS);
        } else {
            userInfo.setName(newName.substring(0, 1).toUpperCase() + newName.substring(1));
        }
        String newSurname = userInfo.getSurname();
        if (!isText(newSurname)) {
            log.info(NAME_SURNAME_WARNING);
            userInfo.setSurname(originalUserInfo.getSurname());
            model.addAttribute("userInfoSurnameError", ONLY_LETTERS);
        } else {
            userInfo.setSurname(newSurname.substring(0, 1).toUpperCase() + newSurname.substring(1));
        }
        try {
            log.info("Попытка пропарсить birthDate {}", birthDate);
            LocalDate newBirthdayDate = LocalDate.parse(birthDate);
            LocalDate currentDate = LocalDate.now();
            Period interval = Period.between(newBirthdayDate, currentDate);
            int newAge = interval.getYears();
            if (newAge > 0 && newAge < 100) {
                log.info("С новой датой рождения возраст равен {}", newAge);
                userInfo.setBirthDate(newBirthdayDate);
                userInfo.setAge(newAge);
            } else {
                log.info("С новой датой рождения возраст равен {}, невозможное значение, присваиваются оригинальные дата рождения и возраста", birthDate);
                userInfo.setAge(originalUserInfo.getAge());
                userInfo.setBirthDate(originalUserInfo.getBirthDate());
                model.addAttribute("userInfoBirthDateError", "При такой дате невозможный возраст!");
                model.addAttribute("userInfoAgeError", "Невозможный возраст!");
            }
        } catch (DateTimeParseException e) {
            log.info("Значение birthDate {} не парсится под LocalDate формат, присваивается оригинальное значение", birthDate);
            userInfo.setBirthDate(originalUserInfo.getBirthDate());
            userInfo.setAge(originalUserInfo.getAge());
            model.addAttribute("userInfoBirthDateError", "Неправильная дата дня рождения!");
        }
        userInfo.setAvatar(originalUserInfo.getAvatar());
        if (!(file.getOriginalFilename().equals(""))) {
            if ((!file.getOriginalFilename().toUpperCase().endsWith(".JPG"))
                    && (!file.getOriginalFilename().toUpperCase().endsWith(".PNG"))
                    && (!file.getOriginalFilename().toUpperCase().endsWith(".JPEG"))
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

    public Long getUserUidByChatId(Long chatId) {
        UserInfo userInfo = userInfoRepo.findByChatId(chatId);
        return userInfo.getUser().getUid();
    }

    private boolean isText(String string) {
        return string.matches("[a-zA-Zа-яА-Я]+");
    }
}