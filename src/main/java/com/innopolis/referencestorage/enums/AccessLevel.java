package com.innopolis.referencestorage.enums;

import lombok.Getter;

public enum AccessLevel {
    PUBLIC(0),
    PRIVATE(1);

    @Getter
    private final int accessLevelUid;

    AccessLevel(int accessLevelUid) {
        this.accessLevelUid = accessLevelUid;
    }
}
