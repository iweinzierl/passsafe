package de.iweinzierl.passsafe.android;

import android.app.Application;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.widget.Toast;

import de.iweinzierl.passsafe.android.data.SQLiteRepository;
import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.preferences.ApplicationPreferences;
import de.iweinzierl.passsafe.android.secure.AesPasswordHandler;
import de.iweinzierl.passsafe.android.secure.PasswordHandler;
import de.iweinzierl.passsafe.android.util.FileUtils;
import de.iweinzierl.passsafe.shared.data.SQLiteCommandExecutor;
import de.iweinzierl.passsafe.shared.data.SQLiteDatabaseCreator;
import de.iweinzierl.passsafe.shared.exception.PassSafeSqlException;

public class PassSafeApplication extends Application {

    private static final Logger LOGGER = new Logger("PassSafeApplication");

    private PasswordHandler passwordHandler;
    private SQLiteRepository repository;
    private ApplicationPreferences applicationPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeSingletons();
    }

    private void initializeSingletons() {
        LOGGER.info("initializeSingletons()");
        repository = new SQLiteRepository(this, FileUtils.getDatabaseFile(this));
        applicationPreferences = new ApplicationPreferences(this);
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

    public ApplicationPreferences getApplicationPreferences() {
        return applicationPreferences;
    }

    public void createNewDatabase() {
        SQLiteOpenHelper dbHelper = new SQLiteOpenHelper(this, "passsafe.sqlite", null, 1) {
            @Override
            public void onCreate(final SQLiteDatabase db) {

                try {
                    final SQLiteDatabaseCreator creator = new SQLiteDatabaseCreator(new SQLiteCommandExecutor() {
                                @Override
                                public boolean execute(final String sql) throws PassSafeSqlException {
                                    db.execSQL(sql);
                                    return true;
                                }
                            }, SQLiteDatabaseCreator.OS.ANDROID);
                    creator.setup();

                } catch (PassSafeSqlException e) {
                    LOGGER.error("Unable to create passsafe database", e);
                    Toast.makeText(getApplicationContext(), "Unable to create passsafe database", Toast.LENGTH_SHORT)
                         .show();
                }

            }

            @Override
            public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) { }
        };

        dbHelper.getWritableDatabase();
        dbHelper.close();
    }
}
