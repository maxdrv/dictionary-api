package com.home.dictionary.exception;

public class ThisShouldNeverHappen extends RuntimeException {

    public ThisShouldNeverHappen(String message) {
        super(message);
    }

    public ThisShouldNeverHappen(String message, Throwable cause) {
        super(message, cause);
    }

}
