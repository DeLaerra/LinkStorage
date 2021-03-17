package com.innopolis.referencestorage.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.time.LocalDate;
import java.util.Base64;

@ToString
@Getter
@Setter
@Entity
@Table(name = "user_info")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Exclude
    private Long uid;

    @OneToOne(optional = false)
    @JoinColumn(name = "uid_user")
    @ToString.Exclude
    private User user;

    private String name;

    private String surname;

    private int age;

    private int sex;

    @Digits(integer = 14, fraction = 0)
    private Long chatId;

    @Column(name = "birth_date")
    @ToString.Exclude
    private LocalDate birthDate;

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