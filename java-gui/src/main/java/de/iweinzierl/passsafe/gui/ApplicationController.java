package de.iweinzierl.passsafe.gui;

import de.iweinzierl.passsafe.gui.configuration.Configuration;
import de.iweinzierl.passsafe.gui.data.Entry;
import de.iweinzierl.passsafe.gui.data.EntryCategory;
import de.iweinzierl.passsafe.gui.data.EntryDataSource;
import de.iweinzierl.passsafe.gui.widget.ButtonBar;
import de.iweinzierl.passsafe.gui.widget.EntryList;
import de.iweinzierl.passsafe.gui.widget.NewEntryDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ApplicationController implements NewEntryDialog.OnEntryAddedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

    private Configuration configuration;

    private EntryDataSource dataSource;
    private EntryList entryList;
    private ButtonBar buttonBar;


    public ApplicationController(Configuration configuration) {
        this.configuration = configuration;
    }


    public EntryDataSource getDataSource() {
        return dataSource;
    }


    public void setDataSource(EntryDataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void onEntryAdded(EntryCategory category, Entry entry) {
        LOGGER.debug("Caught 'onEntryAdded' event");

        if (entryList.addEntry(category, entry)) {
            LOGGER.info("Successfully added entry '{}'", entry);
        } else {
            LOGGER.error("Unable to add entry '{}'", entry);
        }
    }


    public void setEntryList(EntryList entryList) {
        this.entryList = entryList;
    }


    public void setButtonBar(ButtonBar buttonBar) {
        this.buttonBar = buttonBar;
    }
}
