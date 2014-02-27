package de.iweinzierl.passsafe.android.util;

import java.io.File;

import android.content.Context;

public final class FileUtils {

    private static final String TEMP_DIR = "temp";
    private static final String DATA_DIR = "data";
    private static final String TEMP_DB_FILE = "temp.sqlite";
    private static final String DB_FILE = "passsafe.sqlite";

    private FileUtils() { }

    public static File getDataDir(final Context context) {
        File filesDir = context.getFilesDir();
        File dataDir = new File(filesDir, DATA_DIR);

        if (!dataDir.exists()) {
            dataDir.mkdir();
        }

        return dataDir;
    }

    public static File getDatabaseFile(final Context context) {
        File dbFile = context.getDatabasePath(DB_FILE);
        File dir = dbFile.getParentFile();

        if (!dir.exists()) {
            dir.mkdir();
        }

        return dbFile;
    }

    public static File getTemporaryDatabaseFile(final Context context) {
        File dbFile = context.getDatabasePath(TEMP_DB_FILE);
        File dir = dbFile.getParentFile();

        if (!dir.exists()) {
            dir.mkdir();
        }

        return dbFile;
    }
}
