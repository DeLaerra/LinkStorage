package com.innopolis.referencestorage.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Base64;

@ToString
@Entity
@Table(name = "user_info")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    @ToString.Exclude
    private Long uid;

    @Getter
    @Setter
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "uid_user")
    @ToString.Exclude
    private User user;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String surname;

    @Getter
    @Setter
    private int age;

    @Getter
    @Setter
    private int sex;

    @Column(name = "birth_date")
    @Getter
    @Setter
    @ToString.Exclude
    private LocalDate birthDate;
    @Getter
    @Setter
    @ToString.Exclude
    private byte[] avatar;

    public String getAvatarBase64() {
        String result = "";
        if (avatar != null) {
            byte[] encoded = Base64.getEncoder().encode(avatar);
            result = String.format("data:image/png;base64,%s", new String(encoded));
        }
        return result;
    }
}