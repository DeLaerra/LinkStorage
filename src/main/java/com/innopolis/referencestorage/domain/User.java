package com.innopolis.referencestorage.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * User.
 *
 * @author Roman Khokhlov
 */

@Entity
@Table(name = "usercreds")
@ToString
@EqualsAndHashCode
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uid;
    private String username;
    private String password;

    @Transient
    private String passwordConfirmation;
    private boolean active;
    private String email;

    @Column(name = "date_registration")
    private LocalDate registrationDate;
    private int roleUid;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToOne(optional = false, mappedBy = "user", cascade = CascadeType.ALL)
    private UserInfo userInfo;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "friends",
            joinColumns = @JoinColumn(name = "user_friend"),
            inverseJoinColumns = @JoinColumn(name = "user_owner")
    )
    private Set<User> friends;


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