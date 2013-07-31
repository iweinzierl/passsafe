package de.iweinzierl.passsafe.gui;

import de.iweinzierl.passsafe.gui.configuration.Configuration;
import de.iweinzierl.passsafe.gui.data.Entry;
import de.iweinzierl.passsafe.gui.data.EntryCategory;
import de.iweinzierl.passsafe.gui.data.EntryDataSource;
import de.iweinzierl.passsafe.gui.event.RemovedListener;
import de.iweinzierl.passsafe.gui.widget.ButtonBar;
import de.iweinzierl.passsafe.gui.widget.EntryList;
import de.iweinzierl.passsafe.gui.widget.NewEntryDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


public class ApplicationController implements NewEntryDialog.OnEntryAddedListener, WindowListener, RemovedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

    private Configuration configuration;

    private EntryDataSource dataSource;
    private EntryList entryList;
    private ButtonBar buttonBar;


    public ApplicationController(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void onEntryAdded(EntryCategory category, Entry entry) {
        LOGGER.debug("Caught 'onEntryAdded' event");

        dataSource.addEntry(category, entry);

        if (entryList.addEntry(category, entry)) {
            LOGGER.info("Successfully added entry '{}'", entry);
        } else {
            LOGGER.error("Unable to add entry '{}'", entry);
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

    public EntryDataSource getDataSource() {
        return dataSource;
    }


    public void setDataSource(EntryDataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void setEntryList(EntryList entryList) {
        this.entryList = entryList;
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
