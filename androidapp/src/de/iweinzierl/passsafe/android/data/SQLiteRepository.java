package de.iweinzierl.passsafe.android.data;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.util.FileUtils;
import de.iweinzierl.passsafe.shared.domain.Entry;

public class SQLiteRepository {

    private static final Logger LOGGER = new Logger("SQLiteRepository");

    private static final String TABLE_ENTRY = "entry";

    private static final String[] TABLE_ENTRY_COLUMNS = new String[] {
        "id", "category_id", "title", "url", "username", "password", "comment"
    };

    private Context context;
    private SQLiteDatabase database;

    public SQLiteRepository(final Context context) {
        this.context = context;
    }

    public List<Entry> listEntries() {
        if (database == null || !database.isOpen()) {
            connect();
        }

        List<Entry> entries = new ArrayList<Entry>();

        Cursor cursor = database.query(TABLE_ENTRY, TABLE_ENTRY_COLUMNS, null, null, null, null, "title");
        do {
            Entry entry = newEntryFromCursor(cursor);
            if (entry != null) {
                entries.add(entry);
            }
        } while (cursor.moveToNext());

        return entries;
    }

    private void connect() {
        database = SQLiteDatabase.openDatabase(getDatabaseFilePath(), null, SQLiteDatabase.OPEN_READWRITE);
        LOGGER.info("Connection to database ");
    }

    private String getDatabaseFilePath() {
        File databaseFile = FileUtils.getDatabaseFile(context);
        return databaseFile.getAbsolutePath();
    }

    private Entry newEntryFromCursor(final Cursor cursor) {

        // TODO implement a Builder for entries
        return new DatabaseEntry(null, cursor.getString(2), cursor.getString(4), cursor.getString(5));
    }
}
