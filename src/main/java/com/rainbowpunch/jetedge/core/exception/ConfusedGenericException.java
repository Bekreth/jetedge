package com.rainbowpunch.jetedge.core.exception;

public class ConfusedGenericException extends RuntimeException {
    public ConfusedGenericException(String message) {
        super("Unable to determine the generics for class {"
                + message
                + "}. Verify that you've provided proper hinting.");
    }
}
