package com.home.dictionary.util;

public record Header(String name, String value) {

    public static Header auth(String value) {
        return new Header("Authorization", value);
    }

}
