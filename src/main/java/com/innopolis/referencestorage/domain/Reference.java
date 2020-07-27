package com.innopolis.referencestorage.domain;

import lombok.*;

import javax.persistence.*;

@ToString
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refs")
public class Reference {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long uid;

    @Getter
    @Setter
    private String url;

    @Getter
    @Setter
    private Integer rating;
}
