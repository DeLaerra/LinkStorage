package com.innopolis.referencestorage.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;

@ToString
@Entity
@Table(name = "tags")
@Indexed
public class Tags {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uid;
    @Getter
    @Setter
    private String name;

    @OneToOne
    @JoinTable(name="tags_refs",
            joinColumns={@JoinColumn(name="uid_tag", referencedColumnName="uid")},
            inverseJoinColumns={@JoinColumn(name="uid_ref_description", referencedColumnName="uid")})
    private ReferenceDescription referenceDescription;
}