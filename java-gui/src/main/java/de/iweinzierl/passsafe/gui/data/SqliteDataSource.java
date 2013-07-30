package de.iweinzierl.passsafe.gui.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SqliteDataSource implements EntryDataSource {

    public static final String SQL_CREATE_CATEGORY = "CREATE TABLE category (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "  title TEXT NOT NULL" +
            ");";

    public static final String SQL_CREATE_ENTRY = "CREATE TABLE entry ( " +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "  category_id INTEGER NOT NULL, " +
            "  title TEXT NOT NULL, " +
            "  username TEXT, " +
            "  password TEXT NOT NULL " +
            ");";

    public static final String SQL_LOAD_CATEGORIES = "SELECT id, title FROM category ORDER BY title";

    public static final String SQL_LOAD_ENTRIES = "SELECT id, category_id, title, username, password FROM entry";

    public static final String SQL_INSERT_ENTRY = "INSERT INTO entry (category_id, title, username, " +
            "" + "password) VALUES (?, ?, ?, ?)";

    private static final Logger LOGGER = LoggerFactory.getLogger(SqliteDataSource.class);

    private String dbfile;
    private DataSourceChangedListener dataSourceChangedListener;
    private Connection conn;

    private List<EntryCategory> categories;
    private Map<EntryCategory, List<Entry>> entryMap;


    public SqliteDataSource(String dbfile) throws SQLException, ClassNotFoundException, IOException {
        Class.forName("org.sqlite.JDBC");

        this.dbfile = dbfile;
        this.categories = new ArrayList<>();
        this.entryMap = new HashMap<>();

        initialize(dbfile);
        preLoad();
    }


    private void initialize(String dbfile) throws SQLException, ClassNotFoundException, IOException {

        File db = new File(dbfile);
        boolean isNew = !db.exists();

        conn = DriverManager.getConnection("jdbc:sqlite:" + dbfile);
        conn.setAutoCommit(true);

        if (isNew) {
            LOGGER.info("SQLite database is new. Initialize now...");

            try {
                initializeDBLayout();
            } catch (SQLException e) {
                db.delete();
                throw e;
            }
        }
    }


    private void initializeDBLayout() throws SQLException {
        PreparedStatement prepareCategory = conn.prepareStatement(SQL_CREATE_CATEGORY);
        prepareCategory.execute();
        LOGGER.info("Successfully created table 'category'");

        PreparedStatement prepareEntry = conn.prepareStatement(SQL_CREATE_ENTRY);
        prepareEntry.execute();
        LOGGER.info("Successfully created table 'entry'");
    }


    private void preLoad() throws SQLException {
        preLoadCategories();
        preLoadEntries();
    }


    private void preLoadCategories() throws SQLException {
        PreparedStatement loadCategories = conn.prepareStatement(SQL_LOAD_CATEGORIES);
        ResultSet resultSet = loadCategories.executeQuery();

        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            String title = resultSet.getString(2);

            categories.add(new SqliteEntryCategory(id, title));
        }

        LOGGER.debug("PreLoaded {} categories", categories.size());
    }


    private void preLoadEntries() throws SQLException {
        PreparedStatement loadEntries = conn.prepareStatement(SQL_LOAD_ENTRIES);
        ResultSet resultSet = loadEntries.executeQuery();

        int loaded = 0;

        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            int categoryId = resultSet.getInt(2);
            String title = resultSet.getString(3);
            String username = resultSet.getString(4);
            String password = resultSet.getString(5);

            EntryCategory category = getCategoryById(categoryId);

            if (category != null) {
                List<Entry> entries = entryMap.get(category);

                if (entries == null) {
                    entries = new ArrayList<>();
                    entryMap.put(category, entries);
                }

                entries.add(new SqliteEntry(id, title, username, password));
                loaded++;
            }
        }

        LOGGER.debug("PreLoaded {} entries", loaded);
    }


    private EntryCategory getCategoryById(int id) {
        for (EntryCategory category : categories) {
            if (((SqliteEntryCategory) category).getId() == id) {
                return category;
            }
        }

        return null;
    }


    @Override
    public int getItemCount(EntryCategory category) {
        List<Entry> entries = entryMap.get(category);

        return entries == null || entries.isEmpty() ? 0 : entries.size();
    }


    @Override
    public List<EntryCategory> getCategories() {
        return categories;
    }


    @Override
    public List<Entry> getAllEntries(EntryCategory category) {
        return entryMap.get(category);
    }


    @Override
    public Entry getEntry(EntryCategory category, int index) {
        List<Entry> entries = entryMap.get(category);
        if (entries != null && !entries.isEmpty() && entries.size() > index) {
            return entries.get(index);
        }

        return null;
    }


    @Override
    public void addEntry(EntryCategory category, Entry entry) {
        SqliteEntryCategory sqliteCategory;

        if (!(category instanceof SqliteEntryCategory)) {
            sqliteCategory = findCategory(category);
        } else {
            sqliteCategory = (SqliteEntryCategory) category;
        }

        if (sqliteCategory == null) {
            LOGGER.error("Did not find sqlite category '{}'", category);
            return;
        }

        try {
            if (conn.isReadOnly()) {
                LOGGER.error("Database is read-only!");
                return;
            }

            PreparedStatement statement = conn.prepareStatement(SQL_INSERT_ENTRY);
            statement.setInt(1, sqliteCategory.getId());
            statement.setString(2, entry.getTitle());
            statement.setString(3, entry.getUsername());
            statement.setString(4, entry.getPassword());

            int result = statement.executeUpdate();
            if (result <= 0) {
                LOGGER.error("Storage of entry '{}' was not successful", entry.toString());
                return;
            }

            if (dataSourceChangedListener != null) {
                dataSourceChangedListener.onEntryAdded(category, entry);
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to create new entry", e);
        }
    }


    public SqliteEntryCategory findCategory(EntryCategory category) {
        for (EntryCategory tmp : categories) {
            if (tmp.getTitle().equals(category.getTitle())) {
                return (SqliteEntryCategory) category;
            }
        }

        return null;
    }

    @Override
    public void close() {
        if (conn != null) {

            try {

                LOGGER.info("Close connection to SQLite database '{}'", dbfile);
                conn.close();

            } catch (SQLException e) {
                LOGGER.error("Error while closing connection.", e);
            }
        }
    }

    private static class SqliteEntryCategory extends EntryCategory {

        private int id;


        public SqliteEntryCategory(String title) {
            super(title);
        }


        public SqliteEntryCategory(int id, String title) {
            this(title);
            this.id = id;
        }


        public int getId() {
            return id;
        }
    }


    @Override
    public void setDataSourceChangedListener(DataSourceChangedListener listener) {
        this.dataSourceChangedListener = listener;
    }


    private static class SqliteEntry extends Entry {

        private int id;


        public SqliteEntry(String title, String username, String password) {
            super(title, username, password);
        }


        public SqliteEntry(int id, String title, String username, String password) {
            this(title, username, password);
            this.id = id;
        }


        private int getId() {
            return id;
        }
    }
}
