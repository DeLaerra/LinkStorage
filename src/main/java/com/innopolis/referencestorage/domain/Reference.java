package com.innopolis.referencestorage.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Reference.
 *
 * @author Roman Khokhlov
 */

@Entity
@Table(name = "refs")
public class Reference {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uid;
    @Getter
    @Setter
    private Long uidUser;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String url;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private byte uidReferenceType;
    @Getter
    @Setter
    private String tag;
    @Getter
    @Setter
    @Column(name = "adding_date")
    private LocalDate additionDate;
    @Getter
    @Setter
    private String source;
    @Getter
    @Setter
    @Column(name = "uid_adding_method")
    private byte uidAdditionMethod;
    @Getter
    @Setter
    private int rating;
    @Getter
    @Setter
    private int uidAccessLevel;
    @Getter
    @Setter
    private Long uidParentRef;
}