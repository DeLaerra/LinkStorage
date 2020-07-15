package com.innopolis.referencestorage.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Reference.
 *
 * @author Roman Khokhlov
 */

@Entity
@Table(name = "refs")
@Indexed
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
    @Field
    private String name;
    @Getter
    @Setter
    @Field
    private String url;
    @Getter
    @Setter
    @Field
    private String description;
    @Getter
    @Setter
    private byte uidReferenceType;
    @Getter
    @Setter
    @Field
    private String tag;
    @Getter
    @Setter
    @Column(name = "adding_date")
    private LocalDate additionDate;
    @Getter
    @Setter
    @Field
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