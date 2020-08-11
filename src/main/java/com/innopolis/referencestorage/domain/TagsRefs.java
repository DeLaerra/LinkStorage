package com.innopolis.referencestorage.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;

@ToString
@Entity
@Table(name = "tags_refs")
@Indexed
public class TagsRefs {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uid;
    @Getter
    @Setter
    @Column(name = "uid_ref_description")
    private Long uidRefDescription;
    @Getter
    @Setter
    @Column(name = "uid_tag")
    private Long uidTag;
}