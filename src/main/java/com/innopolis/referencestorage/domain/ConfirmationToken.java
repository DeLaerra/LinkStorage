package com.innopolis.referencestorage.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * ConfirmationToken.
 *
 * @author Roman Khokhlov
 */
@Entity
@Table(name = "confirmation_token")
@NoArgsConstructor
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="token_id")
    @Getter
    @Setter
    private long tokenid;

    @Column(name="confirmation_token")
    @Getter
    private String confirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    @Setter
    private Date createdDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "uid")
    @Getter
    @Setter
    private User user;

    public ConfirmationToken(User user) {
        this.user = user;
        createdDate = new Date();
        confirmationToken = UUID.randomUUID().toString();
    }
}

