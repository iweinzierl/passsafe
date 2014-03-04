package de.iweinzierl.passsafe.android.data;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntry;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntryCategory;
import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;
import de.iweinzierl.passsafe.shared.utils.DateUtils;

public class SQLiteRepository {

    private static final Logger LOGGER = new Logger("SQLiteRepository");

    private static final String TABLE_PASSSAFE_METADATA = "passsafe_metadata";
    private static final String TABLE_ENTRY = "entry";
    private static final String TABLE_CATEGORY = "category";

    private static final String[] TABLE_PASSSAFE_METADATA_COLUMNS = new String[] {"_id", "meta_key", "value"};

    private static final String[] TABLE_ENTRY_COLUMNS = new String[] {
        "_id", "category_id", "title", "url", "username", "password", "comment", "last_modified"
    };

    private static final String[] TABLE_CATEGORY_COLUMNS = new String[] {"_id", "title"};

    private Context context;

    private File databaseFile;
    private SQLiteDatabase database;

    public SQLiteRepository(final Context context, final File databaseFile) {
        this.context = context;
        this.databaseFile = databaseFile;
    }

    public void updateSynchronizationDate() {
        openDatabaseIfNecessary();

        ContentValues values = new ContentValues();
        values.put("value", DateUtils.formatDatabaseDate(new Date()));

        int updated = database.update(TABLE_PASSSAFE_METADATA, values, "meta_key = ?", new String[] {"sync.timestamp"});

        if (updated <= 0) {
            LOGGER.error("Update of synchronization date failed");
        }
    }

    public Date getSynchronizationDate() {
        openDatabaseIfNecessary();

        Cursor cursor = database.query(TABLE_PASSSAFE_METADATA, TABLE_PASSSAFE_METADATA_COLUMNS, "meta_key = ?",
                new String[] {"sync.timestamp"}, null, null, null);

        if (!cursor.moveToFirst()) {
            LOGGER.warn("Last sync timestamp not found in database");
            return new Date();
        } else {
            return DateUtils.parseDatabaseDate(cursor.getString(2));
        }
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

        Collections.sort(entries, new Comparator<Entry>() {
                @Override
                public int compare(final Entry a, final Entry b) {
                    return a.getTitle().toLowerCase().compareTo(b.getTitle().toLowerCase());
                }
            });

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

        Collections.sort(categories, new Comparator<EntryCategory>() {
                @Override
                public int compare(final EntryCategory a, final EntryCategory b) {
                    return a.getTitle().toLowerCase().compareTo(b.getTitle().toLowerCase());
                }
            });

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

    public Entry save(final Entry entry) {
        openDatabaseIfNecessary();

        ContentValues values = new ContentValues();
        values.put("category_id", ((DatabaseEntryCategory) entry.getCategory()).getId());
        values.put("title", entry.getTitle());
        values.put("url", entry.getUrl());
        values.put("username", entry.getUsername());
        values.put("password", entry.getPassword());
        values.put("comment", entry.getComment());
        values.put("last_modified", DateUtils.formatDatabaseDate(new Date()));

        long id = database.insert(TABLE_ENTRY, null, values);

        if (id > 0) {
            return entry;
        } else {
            return null;
        }
    }

    public Entry update(final Entry entry) {
        openDatabaseIfNecessary();

        int id = ((DatabaseEntry) entry).getId();

        ContentValues values = new ContentValues();
        values.put("category_id", ((DatabaseEntryCategory) entry.getCategory()).getId());
        values.put("title", entry.getTitle());
        values.put("url", entry.getUrl());
        values.put("username", entry.getUsername());
        values.put("password", entry.getPassword());
        values.put("comment", entry.getComment());
        values.put("last_modified", DateUtils.formatDatabaseDate(new Date()));

        int update = database.update(TABLE_ENTRY, values, "_id = ?", new String[] {String.valueOf(id)});

        return update > 0 ? entry : null;
    }

    public boolean delete(final Entry entry) {
        openDatabaseIfNecessary();

        DatabaseEntry dbEntry = (DatabaseEntry) entry;
        int delete = database.delete(TABLE_ENTRY, "_id = ?", new String[] {String.valueOf(dbEntry.getId())});

        return delete > 0;
    }

    private void openDatabaseIfNecessary() {
        if (database == null || !database.isOpen()) {
            connect();
        }
    }

    private void connect() {
        database = SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
        LOGGER.info("Connection to database ");
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
                .withComment(cursor.getString(6))
                .withLastModified(DateUtils.parseDatabaseDate(cursor.getString(7)))
                .build();
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
