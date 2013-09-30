package de.iweinzierl.passsafe.gui.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.iweinzierl.passsafe.shared.data.DataSourceChangedListener;
import de.iweinzierl.passsafe.shared.data.PassSafeDataSource;
import de.iweinzierl.passsafe.shared.data.SQLiteDatabaseCreator;
import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;
import de.iweinzierl.passsafe.shared.exception.PassSafeSqlException;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
import java.util.Collection;
import java.util.List;


public class SqliteDataSource implements PassSafeDataSource {

    public static final String SQL_LOAD_CATEGORIES = "SELECT id, title FROM category ORDER BY title";

    public static final String SQL_LOAD_ENTRIES = "SELECT id, category_id, title, username, password FROM entry";

    public static final String SQL_INSERT_ENTRY = "INSERT INTO entry (category_id, title, username, " +
            "" + "password) VALUES (?, ?, ?, ?)";

    public static final String SQL_REMOVE_ENTRY = "DELETE FROM entry WHERE id = ?";

    public static final String SQL_FIND_ENTRY_ID = "SELECT id FROM entry WHERE title = ?";

    public static final String SQL_INSERT_CATEGORY = "INSERT INTO category (title) VALUES (?)";

    public static final String SQL_REMOVE_CATEGORY = "DELETE FROM category WHERE id = ?";

    public static final String SQL_REMOVE_CATEGORY_ENTRIES = "DELETE FROM entry WHERE category_id = ?";

    public static final String SQL_UPDATE_CATEGORY_OF_ENTRY = "UPDATE entry SET category_id = ? WHERE id = ?";

    private static final Logger LOGGER = LoggerFactory.getLogger(SqliteDataSource.class);

    private String dbfile;
    private DataSourceChangedListener dataSourceChangedListener;
    private Connection conn;

    private List<EntryCategory> categories;
    private Multimap<EntryCategory, Entry> entryMap;


    public SqliteDataSource(
            String dbfile) throws SQLException, ClassNotFoundException, IOException, PassSafeSqlException {
        Class.forName("org.sqlite.JDBC");

        this.dbfile = dbfile;
        this.categories = new ArrayList<>();
        this.entryMap = ArrayListMultimap.create();

        initialize(dbfile);
        preLoad();
    }


    private void initialize(
            String dbfile) throws PassSafeSqlException, SQLException, ClassNotFoundException, IOException {

        File db = new File(dbfile);
        boolean isNew = !db.exists();

        conn = DriverManager.getConnection("jdbc:sqlite:" + dbfile);
        conn.setAutoCommit(true);

        if (isNew) {
            LOGGER.info("SQLite database is new. Initialize now...");

            try {
                new SQLiteDatabaseCreator(new SQLiteDatabaseCreator.SQLiteCommandExecutor() {
                    @Override
                    public boolean execute(String sql) throws PassSafeSqlException {
                        try {
                            PreparedStatement preparedStatement = conn.prepareStatement(sql);
                            preparedStatement.execute();

                            return true;

                        } catch (SQLException e) {
                            throw new PassSafeSqlException(e.getMessage(), e.getCause());
                        }
                    }
                }).setup();
            } catch (PassSafeSqlException e) {
                db.delete();
                throw e;
            }
        }
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
                Collection<Entry> entries = entryMap.get(category);

                if (entries == null) {
                    entries = new ArrayList<>();
                    entryMap.put(category, (Entry) entries);
                }

                entries.add(new SqliteEntry(category, id, title, username, password));
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
        Collection<Entry> entries = entryMap.get(category);

        return entries == null || entries.isEmpty() ? 0 : entries.size();
    }


    @Override
    public List<EntryCategory> getCategories() {
        return categories;
    }


    @Override
    public List<Entry> getAllEntries(EntryCategory category) {
        return (List<Entry>) entryMap.get(category);
    }


    @Override
    public Entry getEntry(EntryCategory category, int index) {
        List<Entry> entries = (List<Entry>) entryMap.get(category);
        if (entries != null && !entries.isEmpty() && entries.size() > index) {
            return entries.get(index);
        }

        return null;
    }


    @Override
    public Entry addEntry(EntryCategory category, Entry entry) {
        SqliteEntryCategory sqliteCategory;

        if (!(category instanceof SqliteEntryCategory)) {
            sqliteCategory = findCategory(category);
        } else {
            sqliteCategory = (SqliteEntryCategory) category;
        }

        if (sqliteCategory == null) {
            LOGGER.error("Did not find sqlite category '{}'", category);
            return null;
        }

        try {
            if (conn.isReadOnly()) {
                LOGGER.error("Database is read-only!");
                return null;
            }

            PreparedStatement statement = conn.prepareStatement(SQL_INSERT_ENTRY);
            statement.setInt(1, sqliteCategory.getId());
            statement.setString(2, entry.getTitle());
            statement.setString(3, entry.getUsername());
            statement.setString(4, entry.getPassword());

            statement.executeUpdate();
            int id = findId(entry);

            if (id <= 0) {
                LOGGER.error("Storage of entry '{}' was not successful", entry.toString());
                return null;
            }

            SqliteEntry added = new SqliteEntry(entry, id);

            entryMap.put(category, added);

            if (dataSourceChangedListener != null) {
                dataSourceChangedListener.onEntryAdded(category, added);
            }

            return added;
        } catch (SQLException e) {
            LOGGER.error("Unable to create new entry", e);
        }

        return null;
    }


    public Integer findId(Entry entry) {
        try {
            PreparedStatement find = conn.prepareStatement(SQL_FIND_ENTRY_ID);
            find.setString(1, entry.getTitle());

            ResultSet resultSet = find.executeQuery();
            if (resultSet != null) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot find id for entry '{}'", entry, e);
        }

        return null;
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
    public void removeEntry(Entry entry) {
        LOGGER.debug("Go an remove entry '{}'", entry);

        if (!(entry instanceof SqliteEntry)) {
            LOGGER.warn("Cannot remove entry from type '{}'", entry.getClass());
            return;
        }

        try {
            PreparedStatement remove = conn.prepareStatement(SQL_REMOVE_ENTRY);
            remove.setInt(1, ((SqliteEntry) entry).getId());
            int affected = remove.executeUpdate();

            if (affected <= 0) {
                LOGGER.error("Deletion of entry was not successful.");
                return;
            }

            entryMap.remove(entry.getCategory(), entry);
            LOGGER.info("Successfully deleted entry '{}'", entry);

        } catch (SQLException e) {
            LOGGER.error("Unable to remove entry '{}'", entry, e);
        }
    }

    @Override
    public EntryCategory addCategory(EntryCategory category) {
        LOGGER.debug("Go and insert category '{}'", category);

        try {
            PreparedStatement addCategory = conn.prepareStatement(SQL_INSERT_CATEGORY);
            addCategory.setString(1, category.getTitle());
            addCategory.executeUpdate();

            ResultSet generatedKeys = addCategory.getGeneratedKeys();
            int id = generatedKeys.getInt(1);

            if (id > 0) {
                EntryCategory newCategory = new SqliteEntryCategory(id, category.getTitle());
                LOGGER.info("Successfully inserted category '{}'", newCategory);

                categories.add(newCategory);

                return newCategory;
            }
        } catch (SQLException e) {
            // do nothing
        }

        LOGGER.error("Unable to add category '{}'", category);
        return null;
    }

    @Override
    public void removeCategory(EntryCategory category) {
        LOGGER.debug("Go an remove category '{}'", category);

        if (!(category instanceof SqliteEntryCategory)) {
            LOGGER.warn("Cannot remove category from type '{}'", category.getClass());
            return;
        }

        try {
            PreparedStatement removeEntries = conn.prepareStatement(SQL_REMOVE_CATEGORY_ENTRIES);
            removeEntries.setInt(1, ((SqliteEntryCategory) category).getId());
            removeEntries.execute();

            PreparedStatement remove = conn.prepareStatement(SQL_REMOVE_CATEGORY);
            remove.setInt(1, ((SqliteEntryCategory) category).getId());
            int affected = remove.executeUpdate();

            if (affected <= 0) {
                LOGGER.error("Deletion of category was not successful.");
                return;
            }

            categories.remove(category);
            LOGGER.info("Successfully deleted category '{}'", category);

        } catch (SQLException e) {
            LOGGER.error("Unable to remove category '{}'", category);
        }
    }

    @Override
    public void updateEntryCategory(Entry entry, EntryCategory category) {
        LOGGER.debug("Go and update category to '{}' of entry {}", category.getTitle(), entry.getTitle());
        final EntryCategory oldCategory = entry.getCategory();

        if (!(category instanceof SqliteEntryCategory)) {
            LOGGER.warn("Cannot update entry with category from type '{}'", category.getClass());
            return;
        }

        if (!(entry instanceof SqliteEntry)) {
            LOGGER.warn("Cannot update entry of type '{}'", entry.getClass());
            return;
        }

        try {
            PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_CATEGORY_OF_ENTRY);
            stmt.setInt(1, ((SqliteEntryCategory) category).getId());
            stmt.setInt(2, ((SqliteEntry) entry).getId());

            int affected = stmt.executeUpdate();
            if (affected <= 0) {
                LOGGER.warn("No entry updated!");
            } else {
                LOGGER.info("Successfully updated entry '{}' with category '{}'", entry.getTitle(),
                        category.getTitle());

                entryMap.remove(oldCategory, entry);
                entryMap.put(category, entry);
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to update category '{}' of entry '{}'", category.getTitle(), entry.getTitle());
        }
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

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("id", id).append("title", getTitle()).toString();
        }
    }


    @Override
    public void setDataSourceChangedListener(DataSourceChangedListener listener) {
        this.dataSourceChangedListener = listener;
    }


    private static class SqliteEntry extends Entry {

        private int id;

        public SqliteEntry(Entry entry, int id) {
            this(entry.getCategory(), id, entry.getTitle(), entry.getUsername(), entry.getPassword());
        }


        public SqliteEntry(EntryCategory category, String title, String username, String password) {
            super(category, title, username, password);
        }


        public SqliteEntry(EntryCategory category, int id, String title, String username, String password) {
            this(category, title, username, password);
            this.id = id;
        }


        protected int getId() {
            return id;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).appendSuper(super.toString()).append("id", id).toString();
        }
    }
}
