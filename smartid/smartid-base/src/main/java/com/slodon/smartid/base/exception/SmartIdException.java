package com.slodon.smartid.base.exception;

/**
 * @author smartId
 */
public class SmartIdException extends RuntimeException {

    public SmartIdException() {
        super();
    }

    public SmartIdException(String message) {
        super(message);
    }

    public SmartIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public SmartIdException(Throwable cause) {
        super(cause);
    }
}
