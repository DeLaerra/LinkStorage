package com.innopolis.referencestorage.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@ToString
@Entity
@Table(name = "user_info")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    @ToString.Exclude private Long uid;
    @Column(name = "uid_user")
    @Getter
    @Setter
    @ToString.Exclude private Long uidUser;
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
    @ToString.Exclude private LocalDate birthDate;
    @Getter
    @Setter
    @ToString.Exclude private byte[] avatar;
}