package de.iweinzierl.passsafe.gui.resources;

import java.util.ResourceBundle;

public class Errors {

    public static final String ERROR_PROPERTIES = "Errors";

    public static final ResourceBundle ERROR_BUNDLE = ResourceBundle.getBundle(ERROR_PROPERTIES);

    public static final String SYNC_FAILED = "error.sync.failed";

    public static String getError(String key) {
        return ERROR_BUNDLE.getString(key);
    }
}
