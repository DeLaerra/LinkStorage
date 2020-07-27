package com.innopolis.referencestorage.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.NumericField;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Reference.
 *
 * @author Roman Khokhlov
 */

@ToString
@Entity
@Table(name = "ref_description")
@Indexed
public class RefDescription {
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
    @Field
    @NumericField
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uid_reference")
    private Reference reference;
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
    private Short uidReferenceType;
//    @Getter
//    @Setter
////    @Field
//    private String tag;
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

    public Integer getRating(){
        return reference.getRating();
    }
}