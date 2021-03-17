package com.innopolis.referencestorage.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "friends")
@NoArgsConstructor
@Data
public class Friends {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "user_owner")
    private long owner;

    @Column(name = "user_friend")
    private long friend;

    public Friends(long owner, long friend) {
        this.owner = owner;
        this.friend = friend;
    }
}
