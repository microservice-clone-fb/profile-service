package com.tam.profile.configuration;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class UtilBean {

    public boolean isUUID(String value) {
        try {
            UUID.fromString(value);
            return true; // hợp lệ
        } catch (IllegalArgumentException ex) {
            return false; // không phải UUID
        }
    }
}
