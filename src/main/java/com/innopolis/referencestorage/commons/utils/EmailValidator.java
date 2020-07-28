package com.innopolis.referencestorage.commons.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EmailValidator.
 *
 * @author Roman Khokhlov
 */
public class EmailValidator {

    private static final String EMAIL__PATTERN =
            "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    public static boolean isValid(String email) {
        Matcher matcher;
        Pattern pattern;
        pattern = Pattern.compile(EMAIL__PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

}