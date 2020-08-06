package com.innopolis.referencestorage.enums;

import lombok.Getter;

public enum ReferenceType {
    TEXT(Short.parseShort("0")),
    VIDEO(Short.parseShort("1")),
    FILE(Short.parseShort("2")),
    PIC(Short.parseShort("3"));

    @Getter
    private final Short refTypeUid;

    ReferenceType(final Short refTypeUid) {
        this.refTypeUid = refTypeUid;
    }
}
