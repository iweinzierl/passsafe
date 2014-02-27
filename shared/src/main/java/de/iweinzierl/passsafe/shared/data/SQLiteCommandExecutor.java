package de.iweinzierl.passsafe.shared.data;

import de.iweinzierl.passsafe.shared.exception.PassSafeSqlException;

public interface SQLiteCommandExecutor {

    boolean execute(String sql) throws PassSafeSqlException;
}
