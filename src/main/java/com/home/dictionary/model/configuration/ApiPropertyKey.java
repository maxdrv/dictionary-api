package com.home.dictionary.model.configuration;

public enum ApiPropertyKey implements CharSequence {

    REGISTRATION_ALLOWED;

    @Override
    public int length() {
        return this.name().length();
    }

    @Override
    public char charAt(int index) {
        return this.name().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return this.name().subSequence(start, end);
    }
}
