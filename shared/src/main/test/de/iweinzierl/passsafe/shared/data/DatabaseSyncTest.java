package de.iweinzierl.passsafe.shared.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.iweinzierl.passsafe.shared.domain.DatabaseEntry;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntryCategory;

public class DatabaseSyncTest {

    @Test
    public void testIdenticalDatabases() throws Exception {
        DateTime created = new DateTime(2014, 2, 27, 5, 0);
        DateTime lastSync = new DateTime(2014, 2, 27, 5, 30);

        Date creationDate = created.toDate();

        DatabaseEntry a = new DatabaseEntry.Builder().withTitle("A").withId(1).withLastModified(creationDate).build();
        DatabaseEntry b = new DatabaseEntry.Builder().withTitle("B").withId(2).withLastModified(creationDate).build();
        DatabaseEntry c = new DatabaseEntry.Builder().withTitle("C").withId(3).withLastModified(creationDate).build();

        DatabaseEntry a2 = new DatabaseEntry.Builder().withTitle("A").withId(1).withLastModified(creationDate).build();
        DatabaseEntry b2 = new DatabaseEntry.Builder().withTitle("B").withId(2).withLastModified(creationDate).build();
        DatabaseEntry c2 = new DatabaseEntry.Builder().withTitle("C").withId(3).withLastModified(creationDate).build();

        List<DatabaseEntry> localList = Lists.newArrayList(a, b, c);
        List<DatabaseEntry> onlineList = Lists.newArrayList(a2, b2, c2);

        DatabaseSyncHelper databaseSyncHelper = new DatabaseSyncHelper(new DatabaseData(lastSync.toDate(), localList,
                    new ArrayList<DatabaseEntryCategory>()),
                new DatabaseData(lastSync.toDate(), onlineList, new ArrayList<DatabaseEntryCategory>()));

        List<DatabaseEntry> requiredUpdates = databaseSyncHelper.getEntriesWithRequiredUpdate();
        List<DatabaseEntry> newEntries = databaseSyncHelper.getNewEntries();
        List<DatabaseEntry> removedEntries = databaseSyncHelper.getRemovedEntries();

        Assert.assertNotNull(requiredUpdates);
        Assert.assertTrue(requiredUpdates.isEmpty());

        Assert.assertNotNull(newEntries);
        Assert.assertTrue(newEntries.isEmpty());

        Assert.assertNotNull(removedEntries);
        Assert.assertTrue(removedEntries.isEmpty());
    }

    @Test
    public void testToFindOneNew() throws Exception {
        DateTime created = new DateTime(2014, 2, 27, 6, 0);
        DateTime lastSync = new DateTime(2014, 2, 27, 5, 30);

        Date creationDate = created.toDate();

        DatabaseEntry a = new DatabaseEntry.Builder().withTitle("A").withId(1).withLastModified(creationDate).build();
        DatabaseEntry c = new DatabaseEntry.Builder().withTitle("C").withId(3).withLastModified(creationDate).build();

        DatabaseEntry a2 = new DatabaseEntry.Builder().withTitle("A").withId(1).withLastModified(creationDate).build();
        DatabaseEntry b2 = new DatabaseEntry.Builder().withTitle("B").withId(2).withLastModified(creationDate).build();
        DatabaseEntry c2 = new DatabaseEntry.Builder().withTitle("C").withId(3).withLastModified(creationDate).build();

        List<DatabaseEntry> localList = Lists.newArrayList(a, c);
        List<DatabaseEntry> onlineList = Lists.newArrayList(a2, b2, c2);

        DatabaseSyncHelper databaseSyncHelper = new DatabaseSyncHelper(new DatabaseData(lastSync.toDate(), localList,
                    new ArrayList<DatabaseEntryCategory>()),
                new DatabaseData(lastSync.toDate(), onlineList, new ArrayList<DatabaseEntryCategory>()));

        List<DatabaseEntry> requiredUpdates = databaseSyncHelper.getEntriesWithRequiredUpdate();
        List<DatabaseEntry> newEntries = databaseSyncHelper.getNewEntries();
        List<DatabaseEntry> removedEntries = databaseSyncHelper.getRemovedEntries();

        Assert.assertNotNull(requiredUpdates);
        Assert.assertTrue(requiredUpdates.isEmpty());

        Assert.assertNotNull(newEntries);
        Assert.assertTrue(newEntries.size() == 1);
        Assert.assertEquals(2, newEntries.get(0).getId());

        Assert.assertNotNull(removedEntries);
        Assert.assertTrue(removedEntries.isEmpty());
    }

    @Test
    public void testToFindOneRemoved() throws Exception {
        DateTime created = new DateTime(2014, 2, 27, 5, 0);
        DateTime lastSync = new DateTime(2014, 2, 27, 5, 30);

        Date creationDate = created.toDate();

        DatabaseEntry a = new DatabaseEntry.Builder().withTitle("A").withId(1).withLastModified(creationDate).build();
        DatabaseEntry b = new DatabaseEntry.Builder().withTitle("B").withId(2).withLastModified(creationDate).build();
        DatabaseEntry c = new DatabaseEntry.Builder().withTitle("C").withId(3).withLastModified(creationDate).build();

        DatabaseEntry a2 = new DatabaseEntry.Builder().withTitle("A").withId(1).withLastModified(creationDate).build();
        DatabaseEntry c2 = new DatabaseEntry.Builder().withTitle("C").withId(3).withLastModified(creationDate).build();

        List<DatabaseEntry> localList = Lists.newArrayList(a, b, c);
        List<DatabaseEntry> onlineList = Lists.newArrayList(a2, c2);

        DatabaseSyncHelper databaseSyncHelper = new DatabaseSyncHelper(new DatabaseData(lastSync.toDate(), localList,
                    new ArrayList<DatabaseEntryCategory>()),
                new DatabaseData(lastSync.toDate(), onlineList, new ArrayList<DatabaseEntryCategory>()));

        List<DatabaseEntry> requiredUpdates = databaseSyncHelper.getEntriesWithRequiredUpdate();
        List<DatabaseEntry> newEntries = databaseSyncHelper.getNewEntries();
        List<DatabaseEntry> removedEntries = databaseSyncHelper.getRemovedEntries();

        Assert.assertNotNull(requiredUpdates);
        Assert.assertTrue(requiredUpdates.isEmpty());

        Assert.assertNotNull(newEntries);
        Assert.assertTrue(newEntries.isEmpty());
        Assert.assertEquals(2, removedEntries.get(0).getId());
        Assert.assertNotNull(removedEntries);
        Assert.assertTrue(removedEntries.size() == 1);
        Assert.assertEquals(2, removedEntries.get(0).getId());
    }

    @Test
    public void testToFindOneNewAndOneRemoved() throws Exception {
        DateTime created = new DateTime(2014, 2, 27, 5, 0);
        DateTime lastSync = new DateTime(2014, 2, 27, 5, 30);

        Date creationDate = created.toDate();

        DatabaseEntry a = new DatabaseEntry.Builder().withTitle("A").withId(1).withLastModified(creationDate).build();
        DatabaseEntry b = new DatabaseEntry.Builder().withTitle("B").withId(2).withLastModified(creationDate).build();
        DatabaseEntry c = new DatabaseEntry.Builder().withTitle("C").withId(3).withLastModified(creationDate).build();

        DatabaseEntry a2 = new DatabaseEntry.Builder().withTitle("A").withId(1).withLastModified(creationDate).build();
        DatabaseEntry c2 = new DatabaseEntry.Builder().withTitle("C").withId(3).withLastModified(creationDate).build();
        DatabaseEntry d2 = new DatabaseEntry.Builder().withTitle("D").withId(4).withLastModified(creationDate).build();

        List<DatabaseEntry> localList = Lists.newArrayList(a, b, c);
        List<DatabaseEntry> onlineList = Lists.newArrayList(a2, c2, d2);

        DatabaseSyncHelper databaseSyncHelper = new DatabaseSyncHelper(new DatabaseData(lastSync.toDate(), localList,
                    new ArrayList<DatabaseEntryCategory>()),
                new DatabaseData(lastSync.toDate(), onlineList, new ArrayList<DatabaseEntryCategory>()));

        List<DatabaseEntry> requiredUpdates = databaseSyncHelper.getEntriesWithRequiredUpdate();
        List<DatabaseEntry> newEntries = databaseSyncHelper.getNewEntries();
        List<DatabaseEntry> removedEntries = databaseSyncHelper.getRemovedEntries();

        Assert.assertNotNull(requiredUpdates);
        Assert.assertTrue(requiredUpdates.isEmpty());

        Assert.assertNotNull(newEntries);
        Assert.assertTrue(newEntries.size() == 1);
        Assert.assertEquals(4, newEntries.get(0).getId());

        Assert.assertNotNull(removedEntries);
        Assert.assertTrue(removedEntries.size() == 1);
        Assert.assertEquals(2, removedEntries.get(0).getId());
    }
}
