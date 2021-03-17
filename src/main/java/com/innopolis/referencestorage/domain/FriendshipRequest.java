package com.innopolis.referencestorage.domain;

import com.innopolis.referencestorage.enums.AcceptionStatus;
import jdk.jfr.Timestamp;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * FriendshipRequest.
 *
 * @author Roman Khokhlov
 */
@Entity
@Table(name = "friendship_request")
public class FriendshipRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private long uid;

    @ManyToOne
    @JoinColumn(name = "sender_uid", nullable = false)
    @Getter
    @Setter
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_uid", nullable = false)
    @Getter
    @Setter
    private User recipient;

    @Column(name = "text")
    @Getter
    @Setter
    private String text;

    @Column(name = "sending_time")
    @Timestamp
    @NotNull
    @Getter
    @Setter
    private LocalDateTime sendingTime;

    @NotNull
    @Getter
    @Setter
    private Integer acceptionStatus = AcceptionStatus.NOT_DEFINED.getStatusUid();

}
