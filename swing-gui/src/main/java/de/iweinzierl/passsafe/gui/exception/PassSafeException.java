package de.iweinzierl.passsafe.gui.exception;

public class PassSafeException extends Exception {

    public PassSafeException(String message) {
        super(message);
    }

    public PassSafeException(String message, Throwable cause) {
        super(message, cause);
    }
}
