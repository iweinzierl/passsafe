package de.iweinzierl.passsafe.android.exception;

public class PassSafeSecurityException extends Exception {

    public PassSafeSecurityException(String detailMessage) {
        super(detailMessage);
    }

    public PassSafeSecurityException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
