package com.innopolis.referencestorage.domain;

import org.springframework.security.core.GrantedAuthority;

/**
 * Role.
 *
 * @author Roman Khokhlov
 */
public enum Role implements GrantedAuthority {
    ROLE_USER, ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}