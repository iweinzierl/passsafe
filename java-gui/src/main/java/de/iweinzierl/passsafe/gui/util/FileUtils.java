package de.iweinzierl.passsafe.gui.util;

import java.io.File;

import de.iweinzierl.passsafe.gui.configuration.Configuration;

public final class FileUtils {

    public static final String DATABASE_FILENAME = "passsafe.sqlite";
    public static final String DATABASE_TEMP_FILENAME = "temp.sqlite";

    private FileUtils() { }

    public static File getLocalDatabaseFile(final Configuration configuration) {
        String baseFolder = configuration.getBaseFolder();

        File baseDir = new File(baseFolder);
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }

        return new File(baseDir, DATABASE_FILENAME);
    }

    public static File getTempDatabaseFile(final Configuration configuration) {
        String baseFolder = configuration.getBaseFolder();

        File baseDir = new File(baseFolder);
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }

        return new File(baseDir, DATABASE_TEMP_FILENAME);
    }
}
