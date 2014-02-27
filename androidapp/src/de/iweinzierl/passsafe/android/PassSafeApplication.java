package de.iweinzierl.passsafe.android;

import android.app.Application;

import de.iweinzierl.passsafe.android.data.SQLiteRepository;
import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.secure.AesPasswordHandler;
import de.iweinzierl.passsafe.android.secure.PasswordHandler;
import de.iweinzierl.passsafe.android.util.FileUtils;

public class PassSafeApplication extends Application {

    private static final Logger LOGGER = new Logger("PassSafeApplication");

    private PasswordHandler passwordHandler;
    private SQLiteRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeSingletons();
    }

    private void initializeSingletons() {
        LOGGER.info("initializeSingletons()");
        repository = new SQLiteRepository(this, FileUtils.getDatabaseFile(this));
    }

    public void setPassword(final String password) {
        passwordHandler = new AesPasswordHandler(password);
    }

    public PasswordHandler getPasswordHandler() {
        return passwordHandler;
    }

    public SQLiteRepository getRepository() {
        return repository;
    }
}
