package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * UserService.
 *
 * @author Roman Khokhlov
 */
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

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException{
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
        userRepo.save(user);
        return true;
    }

    private String hashPassword(String password) {
        String salt = BCrypt.gensalt(12);
        return (BCrypt.hashpw(password, salt));
    }

}