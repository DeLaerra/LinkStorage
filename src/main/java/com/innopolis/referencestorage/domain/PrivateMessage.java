package com.innopolis.referencestorage.domain;

import com.innopolis.referencestorage.enums.AcceptionStatus;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "private_messages")
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class PrivateMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long uid;

    private String text;

    @ManyToOne
    @JoinColumn (name="ref_description_uid")
    private ReferenceDescription referenceDescription;

    @ManyToOne
    @JoinColumn (name="sender_uid", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn (name="recipient_uid", nullable = false)
    private User recipient;

    @Timestamp
    @NotNull
    private LocalDateTime sendingTime;

    @NotNull
    private Short addingMethodUid;

    @NotNull
    private Integer acceptionStatus = AcceptionStatus.NOT_DEFINED.getStatusUid();
}
