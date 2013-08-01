package de.iweinzierl.passsafe.gui.widget.table;

import de.iweinzierl.passsafe.gui.ApplicationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;

public class EntryTable extends JTable {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntryTable.class);

    private final ApplicationController controller;

    public EntryTable(ApplicationController controller, EntryTableModel model) {
        super(model);
        this.controller = controller;

        initializeHeaders();
    }

    private void initializeHeaders() {
        JTableHeader headers = getTableHeader();
        headers.setReorderingAllowed(false);
        headers.setResizingAllowed(true);
    }

    public EntryTableModel getEntryTableModel() {
        return (EntryTableModel) getModel();
    }

    public void tableChanged() {
        LOGGER.debug("EntryTable model changed. Entries: {}", getModel().getRowCount());
        tableChanged(new TableModelEvent(getEntryTableModel()));
    }
}
