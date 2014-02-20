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
    private static final String TABLE_CATEGORY = "category";

    private static final String[] TABLE_ENTRY_COLUMNS = new String[] {
        "_id", "category_id", "title", "url", "username", "password", "comment"
    };

    private static final String QUERY_ENTRIES_BY_CATEGORY =
        "SELECT _id, category_id, title, url, username, password, comment WHERE category_id = %s";

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

        List<EntryCategory> categories = new ArrayList<EntryCategory>();

        Cursor cursor = database.query(TABLE_CATEGORY, TABLE_CATEGORY_COLUMNS, null, null, null, null, "title");
        if (!cursor.moveToFirst()) {
            return categories;
        }

        do {
            EntryCategory category = newEntryCategoryFromCursor(cursor);
            if (category != null) {
                categories.add(category);
            }
        } while (cursor.moveToNext());

        return categories;
    }

    public Entry findEntry(final int id) {
        openDatabaseIfNecessary();

        Cursor cursor = database.query(TABLE_ENTRY, TABLE_ENTRY_COLUMNS, "_id = " + id, null, null, null, null);

        if (cursor.moveToFirst()) {
            return newEntryFromCursor(cursor);
        }

        return null;
    }

    public EntryCategory findCategory(final int id) {
        openDatabaseIfNecessary();

        Cursor cursor = database.query(TABLE_CATEGORY, TABLE_CATEGORY_COLUMNS, "_id = " + id, null, null, null, null);

        if (cursor.moveToFirst()) {
            return newEntryCategoryFromCursor(cursor);
        }

        return null;
    }

    public List<Entry> findEntries(final int categoryId) {
        openDatabaseIfNecessary();

        Cursor cursor = database.query(TABLE_ENTRY, TABLE_ENTRY_COLUMNS, "category_id = " + categoryId, null, null,
                null, "title");

        List<Entry> entries = new ArrayList<Entry>();

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
                .withId(cursor.getInt(0))
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
                .withId(cursor.getInt(0))
                .withTitle(cursor.getString(1)).build();
        //J+

    }
}
