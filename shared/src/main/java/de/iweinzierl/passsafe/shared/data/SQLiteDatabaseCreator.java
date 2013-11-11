package de.iweinzierl.passsafe.shared.data;

import de.iweinzierl.passsafe.shared.exception.PassSafeSqlException;

public class SQLiteDatabaseCreator {

    public static interface SQLiteCommandExecutor {
        boolean execute(String sql) throws PassSafeSqlException;
    }

    public static final String SQL_CREATE_CATEGORY = "CREATE TABLE category ("
            + "  id INTEGER PRIMARY KEY AUTOINCREMENT, " + "  title TEXT NOT NULL" + ");";

    public static final String SQL_CREATE_ENTRY = "CREATE TABLE entry ( " + "  id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "  category_id INTEGER NOT NULL, " + "  title TEXT NOT NULL, " + "  url TEXT, " + "  username TEXT, "
            + "  password TEXT NOT NULL, " + "  comment  TEXT, " + "  FOREIGN KEY (category_id) REFERENCES category(id)"
            + ");";

    private SQLiteCommandExecutor commandExecutor;

    public SQLiteDatabaseCreator(final SQLiteCommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public void setup() throws PassSafeSqlException {
        commandExecutor.execute(SQL_CREATE_CATEGORY);
        commandExecutor.execute(SQL_CREATE_ENTRY);
    }
}
