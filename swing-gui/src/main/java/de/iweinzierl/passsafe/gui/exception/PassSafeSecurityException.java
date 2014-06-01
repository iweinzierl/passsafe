package de.iweinzierl.passsafe.gui.exception;

public class PassSafeSecurityException extends PassSafeException {

    public PassSafeSecurityException(String message) {
        super(message);
    }

    public PassSafeSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
