package com.home.dictionary.util;

public record Header(String name, String value) {

    public static Header auth(String value) {
        return new Header("Authorization", value);
    }

    public static Header cookie(String name, String value) {
        return new Header("Cookie", name + "=" + value);
    }

}
