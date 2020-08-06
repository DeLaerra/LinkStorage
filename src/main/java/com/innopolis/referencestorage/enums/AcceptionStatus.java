package com.innopolis.referencestorage.enums;

import lombok.Getter;

public enum AcceptionStatus {
    NOT_DEFINED(0),
    ACCEPTED(1),
    REJECTED(2),
    DUPLICATE(3);

    @Getter
    private final int statusUid;

    AcceptionStatus(final int statusUid) {
        this.statusUid = statusUid;
    }
}
