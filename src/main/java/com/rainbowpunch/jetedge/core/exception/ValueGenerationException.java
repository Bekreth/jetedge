package com.rainbowpunch.jetedge.core.exception;

public class ValueGenerationException extends RuntimeException {
    public ValueGenerationException() {
        super();
    }

    public ValueGenerationException(String message) {
        super(message);
    }

    public ValueGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValueGenerationException(Throwable cause) {
        super(cause);
    }

    protected ValueGenerationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
