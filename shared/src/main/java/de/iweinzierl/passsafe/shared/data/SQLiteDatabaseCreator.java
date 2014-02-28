package de.iweinzierl.passsafe.shared.data;

import de.iweinzierl.passsafe.shared.exception.PassSafeSqlException;

public class SQLiteDatabaseCreator {

    public enum OS {
        DESKTOP,
        ANDROID
    }

    public static final String SQL_CREATE_CATEGORY = "CREATE TABLE category ("
            + "  _id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, last_modified TEXT NOT NULL);";

    public static final String SQL_CREATE_ENTRY = "CREATE TABLE entry (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "  category_id INTEGER NOT NULL, " + "  title TEXT NOT NULL, " + "  url TEXT, " + "  username TEXT, "
            + "  password TEXT NOT NULL, " + "  comment  TEXT, last_modified TEXT NOT NULL, "
            + "  FOREIGN KEY (category_id) REFERENCES category(_id));";

    public static final String SQL_CREATE_ANDROID_METADATA = "CREATE TABLE android_metadata (locale text);";

    public static final String SQL_INSERT_ANDROID_METADATA = "INSERT INTO android_metadata (locale) VALUES ('en_US');";

    public static final String SQL_CREATE_PASSSAFE_METADATA =
        "CREATE TABLE passsafe_metadata (_id INTEGER PRIMARY KEY AUTOINCREMENT, meta_key TEXT NOT NULL, value TEXT NOT NULL);";

    public static final String SQL_INSERT_PASSSAFE_METADATA =
        "INSERT INTO passsafe_metadata (meta_key, value) VALUES ('sync.timestamp', '2000-01-01 00:00:00');";

    private final SQLiteCommandExecutor commandExecutor;
    private final OS os;

    public SQLiteDatabaseCreator(final SQLiteCommandExecutor commandExecutor, final OS os) {
        this.commandExecutor = commandExecutor;
        this.os = os;
    }

    public void setup() throws PassSafeSqlException {
        commandExecutor.execute(SQL_CREATE_CATEGORY);
        commandExecutor.execute(SQL_CREATE_ENTRY);
        commandExecutor.execute(SQL_CREATE_PASSSAFE_METADATA);
        commandExecutor.execute(SQL_INSERT_PASSSAFE_METADATA);

        if (os != OS.ANDROID) {
            commandExecutor.execute(SQL_CREATE_ANDROID_METADATA);
            commandExecutor.execute(SQL_INSERT_ANDROID_METADATA);
        }
    }
}
