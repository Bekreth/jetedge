package com.rainbowpunch.jetedge.core.exception;

/**
 * This exception is thrown when a Limiter is improperly constructed.
 */
public class LimiterConstructionException extends RuntimeException {
    public LimiterConstructionException() {
        super();
    }

    public LimiterConstructionException(String message) {
        super(message);
    }

    public LimiterConstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public LimiterConstructionException(Throwable cause) {
        super(cause);
    }

    protected LimiterConstructionException(String message, Throwable cause, boolean enableSuppression,
                                           boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
