package com.innopolis.referencestorage.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.NumericField;

import javax.persistence.*;
import java.time.LocalDate;

@ToString
@Entity
@Table(name = "ref_description")
@Indexed
public class ReferenceDescription {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uid;
    @Getter
    @Setter
    @Field
    @NumericField
    private Long uidUser;
    @Getter
    @Setter
    @IndexedEmbedded(depth = 1)
    @ManyToOne
    @JoinColumn (name="uid_reference", nullable = false)
    private Reference reference;
    @Getter
    @Setter
    @Field
    private String name;
    @Getter
    @Setter
    @Field
    private String description;
    @Getter
    @Setter
    private Short uidReferenceType;
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
    private Short uidAdditionMethod;
    @Getter
    @Setter
    @Field
    @NumericField
    private int uidAccessLevel;
    @Getter
    @Setter
    private Long uidParentRef;
}
