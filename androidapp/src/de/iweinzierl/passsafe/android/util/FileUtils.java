package de.iweinzierl.passsafe.android.util;

import java.io.File;

import android.content.Context;

public final class FileUtils {

    private static final String TEMP_DIR = "temp";
    private static final String DATA_DIR = "data";
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
        File dataDir = getDataDir(context);
        return new File(dataDir, DB_FILE);
    }
}
