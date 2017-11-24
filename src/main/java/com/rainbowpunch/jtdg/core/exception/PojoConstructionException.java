package com.rainbowpunch.jtdg.core.exception;

public class PojoConstructionException extends RuntimeException {
    public PojoConstructionException() {
        super();
    }

    public PojoConstructionException(String message) {
        super(message);
    }

    public PojoConstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PojoConstructionException(Throwable cause) {
        super(cause);
    }

    protected PojoConstructionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
