package de.iweinzierl.passsafe.gui.resources;

import java.util.ResourceBundle;

public class Errors {

    public static final String ERROR_PROPERTIES = "Errors";

    public static final ResourceBundle ERROR_BUNDLE = ResourceBundle.getBundle(ERROR_PROPERTIES);

    public static final String SYNC_FAILED = "error.sync.failed";
    public static final String PASSWORD_VERIFICATION_FAILED = "error.passwordverification.failed";

    public static String getError(final String key) {
        return ERROR_BUNDLE.getString(key);
    }
}
