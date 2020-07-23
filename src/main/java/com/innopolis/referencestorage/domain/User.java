package com.innopolis.referencestorage.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

/**
 * User.
 *
 * @author Roman Khokhlov
 */

@Entity
@Table(name = "usercreds")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long uid;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    @Transient
    private String passwordConfirmation;
    @Getter
    @Setter
    private boolean active;
    @Getter
    @Setter
    private String email;
    @Column(name = "date_registration")
    @Getter
    @Setter
    private LocalDate registrationDate;
    @Getter
    @Setter
    private int roleUid;

    public boolean isAdmin() {
        return roleUid == 1;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roleUid == 1) {
            return Collections.singleton(Role.ROLE_ADMIN);
        }
        return Collections.singleton(Role.ROLE_USER);
    }
}