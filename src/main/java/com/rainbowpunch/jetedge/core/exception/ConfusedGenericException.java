package com.rainbowpunch.jetedge.core.exception;

/**
 * An exception that is thrown when the generics of a class cannot be inferred.
 */
public class ConfusedGenericException extends RuntimeException {
    public ConfusedGenericException(String message) {
        super("Unable to determine the generics for class {"
                + message
                + "}. Verify that you've provided proper hinting.");
    }
}
