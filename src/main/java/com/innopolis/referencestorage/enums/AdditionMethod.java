package com.innopolis.referencestorage.enums;

import lombok.Getter;

public enum AdditionMethod {
    SITE(Short.parseShort("0")),
    MAIL(Short.parseShort("1")),
    TELEGRAM (Short.parseShort("2"));

    @Getter
    private final Short additionMethodUid;

    AdditionMethod(final Short additionMethodUid) {
        this.additionMethodUid = additionMethodUid;
    }
}
