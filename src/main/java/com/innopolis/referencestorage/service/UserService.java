package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.domain.UserInfo;
import com.innopolis.referencestorage.repos.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

/**
 * UserService.
 *
 * @author Roman Khokhlov
 */
@Slf4j
@Service
public class UserService implements UserDetailsService {
    private UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден!");
        }
        return user;
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден!");
        }
        return user;
    }

    public boolean saveUser(User user) {
        if (userRepo.findByEmail(user.getEmail()) != null || userRepo.findByUsername(user.getUsername()) != null) {
            return false;
        }
        user.setRoleUid(0);
        user.setPassword(hashPassword(user.getPassword()));
        user.setUsername(user.getUsername());
        user.setRegistrationDate(LocalDate.now());
        user.setActive(true);
        UserInfo userInfo = new UserInfo();
        userInfo.setUser(user);
        userInfo.setName("Name");
        userInfo.setSurname("Surname");
        userInfo.setAge(0);
        userInfo.setSex(0);
        userInfo.setBirthDate(LocalDate.now());
        user.setUserInfo(userInfo);
        try {
            userInfo.setAvatar(Files.readAllBytes(Paths.get("src\\resources\\otherFiles\\defaultAvatar.png")));
        } catch (IOException e) {
            log.error("Нет файла defaultAvatar.png");
        }
        userRepo.save(user);
        return true;
    }

    public List<User> findUsers(String searchUsername) {
        return userRepo.findByUsernameLike("%" + searchUsername + "%");
    }

    private String hashPassword(String password) {
        String salt = BCrypt.gensalt(12);
        return (BCrypt.hashpw(password, salt));
    }

    public User findUserByUid(Long uid) {
        return userRepo.findByUid(uid);
    }
}