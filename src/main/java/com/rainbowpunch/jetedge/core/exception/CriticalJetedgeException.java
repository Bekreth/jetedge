package com.rainbowpunch.jetedge.core.exception;

/**
 * This exception acts as a catchall for any error that is completely unexpected.
 */
public class CriticalJetedgeException extends RuntimeException {
    public CriticalJetedgeException(Throwable cause) {
        super("ERROR ERROR : Something has gone SERIOUSLY wrong.  You should never see this exception.  "
                + "If you do, please file a ticket with information on however it was generated.", cause);
    }
}
