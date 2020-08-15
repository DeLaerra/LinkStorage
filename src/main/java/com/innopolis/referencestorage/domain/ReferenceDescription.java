package com.innopolis.referencestorage.domain;

import lombok.*;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.NumericField;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "ref_description")
@Data
@Indexed
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ReferenceDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uid;

    @Field
    @NumericField
    private Long uidUser;

    @Field
    private String name;

    @Field
    private String description;

    private Short uidReferenceType;

    @Column(name = "adding_date")
    private LocalDate additionDate;

    @Field
    private String source;

    @Column(name = "uid_adding_method")
    private Short uidAdditionMethod;

    @Field
    @NumericField
    private int uidAccessLevel;

    private Long uidParentRef;

    @IndexedEmbedded(depth = 1)
    @ManyToOne
    @JoinColumn (name="uid_reference", nullable = false)
    private Reference reference;

    @OneToMany(mappedBy="referenceDescription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PrivateMessage> privateMessages = new ArrayList<>();

    @Getter
    @OneToMany(cascade=CascadeType.ALL)
    @JoinTable(name="tags_refs",
            joinColumns={@JoinColumn(name="uid_ref_description", referencedColumnName="uid")},
            inverseJoinColumns={@JoinColumn(name="uid_tag", referencedColumnName="uid")})
    private Set<Tags> tag;

    @Setter
    @Transient
    @Getter
    private String tags = getTagsFromSet(this.tag);

    @Setter
    @Transient
    @Getter
    private int isExistAtFriend = 1;

    private String getTagsFromSet(Set<Tags> tag) {
        String result = "";
        if (!(tag == null)) {
            for (Tags tags : tag) {
                result = result + tags.getName() + " ";
            }
        }
        return result;
    }

    public void setTags() {
        this.tags = getTagsFromSet(this.tag);
    }
}
