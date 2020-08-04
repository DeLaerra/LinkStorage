package com.innopolis.referencestorage.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "friends")
public class Friends {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private long id;

    @Column(name = "user_owner")
    @Getter
    @Setter
    private long owner;

    @Column(name = "user_friend")
    @Getter
    @Setter
    private long friend;

    public Friends(long owner, long friend) {
        this.owner = owner;
        this.friend = friend;
    }

    public Friends() {

    }
}
