package de.iweinzierl.passsafe.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.File;
import java.io.IOException;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import de.iweinzierl.passsafe.gui.configuration.Configuration;
import de.iweinzierl.passsafe.gui.event.Event;
import de.iweinzierl.passsafe.gui.event.EventBus;
import de.iweinzierl.passsafe.gui.event.EventListener;
import de.iweinzierl.passsafe.gui.event.EventType;
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
import de.iweinzierl.passsafe.shared.data.PassSafeDataSource;
import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class ApplicationController implements NewEntryDialog.OnEntryAddedListener, WindowListener, RemovedListener,
    TreeSelectionListener, EntryTable.SelectionListener, NewCategoryDialog.OnCategoryAddedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

    private final Configuration configuration;

    private final PasswordHandler passwordHandler;
    private final Sync sync;

    private Application application;
    private PassSafeDataSource dataSource;
    private EntryList entryList;
    private EntryTable entryTable;
    private EntryView entryView;
    private ButtonBar buttonBar;

    public ApplicationController(final Configuration configuration, final PasswordHandler passwordHandler,
            final Sync sync) {
        this.configuration = configuration;
        this.passwordHandler = passwordHandler;
        this.sync = sync;
        setupEventListeners();
    }

    public PasswordHandler getPasswordHandler() {
        return passwordHandler;
    }

    public Application getApplication() {
        return application;
    }

    @Override
    public void onEntryAdded(final EntryCategory category, final Entry entry) {
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
    public void onCategoryAdded(final EntryCategory category) {
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
    public void onEntryRemoved(final Entry entry) {
        dataSource.removeEntry(entry);
        entryList.removeEntry(entry);
    }

    @Override
    public void onCategoryRemoved(final EntryCategory category) {
        dataSource.removeCategory(category);
        entryList.removeCategory(category);
    }

    /**
     * Used to keep track on EntryList changes. The selected entries are published to the EntryTable.
     *
     * @param  treeSelectionEvent  Event that keeps an instance of the selected tree item.
     */
    @Override
    public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {
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
    public void onSelectionChanged(final Entry entry) {
        if (entry != null) {
            entryView.apply(entry);
        } else {
            entryView.reset();
        }
    }

    public PassSafeDataSource getDataSource() {
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

    public void setDataSource(final PassSafeDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setApplication(final Application application) {
        this.application = application;
    }

    public void setEntryList(final EntryList entryList) {
        this.entryList = entryList;
    }

    public void setEntryTable(final EntryTable table) {
        this.entryTable = table;
    }

    public void setEntryView(final EntryView entryView) {
        this.entryView = entryView;
    }

    public void setButtonBar(final ButtonBar buttonBar) {
        this.buttonBar = buttonBar;
    }

    private void shutdown() {
        dataSource.close();
    }

    @Override
    public void windowOpened(final WindowEvent windowEvent) {
        // nothing to do
    }

    @Override
    public void windowClosing(final WindowEvent windowEvent) {
        shutdown();
    }

    @Override
    public void windowClosed(final WindowEvent windowEvent) {
        // nothing to do
    }

    @Override
    public void windowIconified(final WindowEvent windowEvent) {
        // nothing to do
    }

    @Override
    public void windowDeiconified(final WindowEvent windowEvent) {
        // nothing to do
    }

    @Override
    public void windowActivated(final WindowEvent windowEvent) {
        // nothing to do
    }

    @Override
    public void windowDeactivated(final WindowEvent windowEvent) {
        // nothing to do
    }

    private void setupEventListeners() {
        EventBus eventBus = EventBus.getInstance();

        eventBus.register(EventType.MODIFY_ENTRY, new EventListener() {
                @Override
                public void notify(final Event event) {
                    getDataSource().updateEntry((Entry) event.getData());
                }
            });
    }
}
