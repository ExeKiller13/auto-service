package com.alokhin.autoservice.util;

import java.util.UUID;

public class RegistrationUtil {

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
