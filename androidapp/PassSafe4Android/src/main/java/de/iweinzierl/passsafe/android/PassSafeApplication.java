package de.iweinzierl.passsafe.android;

import android.app.Application;

import de.iweinzierl.passsafe.android.logging.Logger;

public class PassSafeApplication extends Application {

    private static final Logger LOGGER = new Logger("PassSafeApplication");

    @Override
    public void onCreate() {
        super.onCreate();
        initializeSingletons();
    }

    private void initializeSingletons() {
        LOGGER.info("initializeSingletons()");
    }
}
