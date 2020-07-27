package com.innopolis.referencestorage.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Reference.
 *
 * @author Roman Khokhlov
 */

@ToString
@Entity
@Table(name = "refs")
@Indexed
@NoArgsConstructor
public class Reference {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uid;
    @Getter
    @Setter
    @Field
    private String url;
    @Getter
    @Setter
    private int rating;
    @Getter
    @Setter
    @ContainedIn
    @IndexedEmbedded
    @OneToMany(mappedBy="reference", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ReferenceDescription> referenceDescriptions = new HashSet<>();

    public Reference(String url, int rating) {
        this.url = url;
        this.rating = rating;
    }
}