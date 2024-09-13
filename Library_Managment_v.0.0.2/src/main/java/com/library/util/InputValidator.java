package com.library.util;

import java.util.regex.Pattern;

public class InputValidator {

    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    public static boolean isValidUUID(String uuid) {
        return UUID_PATTERN.matcher(uuid).matches();
    }

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    public static boolean isValidTitle(String title) {
        return title != null && !title.trim().isEmpty();
    }
}