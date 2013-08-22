package de.iweinzierl.passsafe.gui;

import com.google.common.collect.Lists;
import de.iweinzierl.passsafe.gui.configuration.Configuration;
import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;
import de.iweinzierl.passsafe.gui.data.EntryDataSource;
import de.iweinzierl.passsafe.gui.event.RemovedListener;
import de.iweinzierl.passsafe.gui.secure.PasswordHandler;
import de.iweinzierl.passsafe.gui.sync.Sync;
import de.iweinzierl.passsafe.gui.widget.ButtonBar;
import de.iweinzierl.passsafe.gui.widget.EntryList;
import de.iweinzierl.passsafe.gui.widget.EntryView;
import de.iweinzierl.passsafe.gui.widget.NewCategoryDialog;
import de.iweinzierl.passsafe.gui.widget.NewEntryDialog;
import de.iweinzierl.passsafe.gui.widget.table.EntryTable;
import de.iweinzierl.passsafe.gui.widget.tree.CategoryNode;
import de.iweinzierl.passsafe.gui.widget.tree.EntryNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;


public class ApplicationController implements NewEntryDialog.OnEntryAddedListener, WindowListener, RemovedListener, TreeSelectionListener, EntryTable.SelectionListener, NewCategoryDialog.OnCategoryAddedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

    private final Configuration configuration;

    private final PasswordHandler passwordHandler;
    private final Sync sync;

    private Application application;
    private EntryDataSource dataSource;
    private EntryList entryList;
    private EntryTable entryTable;
    private EntryView entryView;
    private ButtonBar buttonBar;


    public ApplicationController(Configuration configuration, PasswordHandler passwordHandler, Sync sync) {
        this.configuration = configuration;
        this.passwordHandler = passwordHandler;
        this.sync = sync;
    }

    public PasswordHandler getPasswordHandler() {
        return passwordHandler;
    }

    @Override
    public void onEntryAdded(EntryCategory category, Entry entry) {
        LOGGER.debug("Caught 'onEntryAdded' event");

        Entry newEntry = dataSource.addEntry(category, entry);

        if (entryList.addEntry(category, newEntry)) {
            LOGGER.info("Successfully added entry '{}'", newEntry);
            entryTable.tableChanged();
        } else {
            LOGGER.error("Unable to add entry '{}'", newEntry);
        }
    }

    @Override
    public void onCategoryAdded(EntryCategory category) {
        LOGGER.debug("Caught 'onCategoryAdded' event");

        if (category == null) {
            LOGGER.warn("Received category: null");
            return;
        }

        EntryCategory newCategory = dataSource.addCategory(category);

        if (newCategory != null) {
            entryList.addCategory(category);
        }
    }

    @Override
    public void onEntryRemoved(Entry entry) {
        dataSource.removeEntry(entry);
        entryList.removeEntry(entry);
    }

    @Override
    public void onCategoryRemoved(EntryCategory category) {
        dataSource.removeCategory(category);
        entryList.removeCategory(category);
    }

    /**
     * Used to keep track on EntryList changes. The selected entries are published to the EntryTable.
     *
     * @param treeSelectionEvent Event that keeps an instance of the selected tree item.
     */
    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        TreePath newLeadSelectionPath = treeSelectionEvent.getNewLeadSelectionPath();
        TreePath oldLeadSelectionPath = treeSelectionEvent.getOldLeadSelectionPath();

        if (newLeadSelectionPath == null) {
            if (oldLeadSelectionPath.getLastPathComponent() instanceof EntryNode) {
                entryTable.getEntryTableModel().setEntries(null);
                entryTable.tableChanged();
            }
            return;
        }

        Object lastPathComponent = newLeadSelectionPath.getLastPathComponent();

        if (lastPathComponent instanceof CategoryNode) {
            EntryCategory category = ((CategoryNode) lastPathComponent).getCategory();
            LOGGER.debug("Selected category '{}'", category);

            entryTable.getEntryTableModel().setEntries(getDataSource().getAllEntries(category));
            entryTable.tableChanged();
        } else if (lastPathComponent instanceof EntryNode) {
            Entry entry = ((EntryNode) lastPathComponent).getEntry();
            LOGGER.debug("Selected entry '{}'", entry);

            entryTable.getEntryTableModel().setEntries(Lists.newArrayList(entry));
            entryTable.tableChanged();
        }
    }

    @Override
    public void onSelectionChanged(Entry entry) {
        if (entry != null) {
            entryView.apply(entry);
        } else {
            entryView.reset();
        }
    }

    public EntryDataSource getDataSource() {
        return dataSource;
    }

    public void requestSync() throws IOException {
        if (sync == null) {
            LOGGER.warn("No Sync handler configured");
            return;
        }

        File dataSource = new File(configuration.getDatabase());
        sync.sync(dataSource.getName());

        LOGGER.info("Successfully synchronized data source '{}'", dataSource);
    }


    public void setDataSource(EntryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public void setEntryList(EntryList entryList) {
        this.entryList = entryList;
    }

    public void setEntryTable(EntryTable table) {
        this.entryTable = table;
    }

    public void setEntryView(EntryView entryView) {
        this.entryView = entryView;
    }

    public void setButtonBar(ButtonBar buttonBar) {
        this.buttonBar = buttonBar;
    }

    private void shutdown() {
        dataSource.close();
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {
        // nothing to do
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        shutdown();
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {
        // nothing to do
    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {
        // nothing to do
    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {
        // nothing to do
    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {
        // nothing to do
    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {
        // nothing to do
    }
}
