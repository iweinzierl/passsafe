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
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class SQLiteRepository {

    private static final Logger LOGGER = new Logger("SQLiteRepository");

    private static final String TABLE_ENTRY = "entry";
    private static final String TABLE_CATEGORY = "entry";

    private static final String[] TABLE_ENTRY_COLUMNS = new String[] {
        "_id", "category_id", "title", "url", "username", "password", "comment"
    };

    private static final String[] TABLE_CATEGORY_COLUMNS = new String[] {"_id", "title"};

    private Context context;
    private SQLiteDatabase database;

    public SQLiteRepository(final Context context) {
        this.context = context;
    }

    public List<Entry> listEntries() {
        openDatabaseIfNecessary();

        List<Entry> entries = new ArrayList<Entry>();

        Cursor cursor = database.query(TABLE_ENTRY, TABLE_ENTRY_COLUMNS, null, null, null, null, "title");
        if (!cursor.moveToFirst()) {
            return entries;
        }

        do {
            Entry entry = newEntryFromCursor(cursor);
            if (entry != null) {
                entries.add(entry);
            }
        } while (cursor.moveToNext());

        return entries;
    }

    public List<EntryCategory> listCategories() {
        openDatabaseIfNecessary();

        List<EntryCategory> entries = new ArrayList<EntryCategory>();

        Cursor cursor = database.query(TABLE_CATEGORY, TABLE_CATEGORY_COLUMNS, null, null, null, null, "title");
        if (!cursor.moveToFirst()) {
            return entries;
        }

        do {
            EntryCategory entry = newEntryCategoryFromCursor(cursor);
            if (entry != null) {
                entries.add(entry);
            }
        } while (cursor.moveToNext());

        return entries;
    }

    public EntryCategory findCategory(final int id) {
        openDatabaseIfNecessary();

        Cursor cursor = database.query(TABLE_CATEGORY, TABLE_CATEGORY_COLUMNS, "WHERE \"_id\" = " + id, null, null,
                null, "title");

        if (!cursor.moveToFirst()) {
            return null;
        }

        return newEntryCategoryFromCursor(cursor);
    }

    private void openDatabaseIfNecessary() {
        if (database == null || !database.isOpen()) {
            connect();
        }
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
        //J-
        return new DatabaseEntry.Builder()
                .withCategory((DatabaseEntryCategory) findCategory(cursor.getInt(1)))
                .withId(cursor.getInt(1))
                .withTitle(cursor.getString(2))
                .withUrl(cursor.getString(3))
                .withUsername(cursor.getString(4))
                .withPassword(cursor.getString(5))
                .withComment(cursor.getString(6)).build();
        //J+
    }

    private EntryCategory newEntryCategoryFromCursor(final Cursor cursor) {
        //J-
        return new DatabaseEntryCategory.Builder()
                .withId(cursor.getInt(1))
                .withTitle(cursor.getString(2)).build();
        //J+
    }
}
