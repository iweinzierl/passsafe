package de.iweinzierl.passsafe.android.exception;

public class PassSafeSyncException extends Exception {

    public PassSafeSyncException(final String message) {
        super(message);
    }

    public PassSafeSyncException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
