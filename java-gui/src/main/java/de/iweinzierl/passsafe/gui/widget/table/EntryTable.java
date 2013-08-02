package de.iweinzierl.passsafe.gui.widget.table;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.data.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;

public class EntryTable extends JTable {

    public interface SelectionListener {
        void onSelectionChanged(Entry entry);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(EntryTable.class);

    private final ApplicationController controller;
    private final SelectionListener selectionListener;

    public EntryTable(ApplicationController controller, EntryTableModel model, SelectionListener selectionListener) {
        super(model);
        this.controller = controller;
        this.selectionListener = selectionListener;

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

    @Override
    public void valueChanged(ListSelectionEvent e) {
        super.valueChanged(e);

        int row = getSelectedRow();

        if (row >= 0) {
            selectionListener.onSelectionChanged(getEntryTableModel().getEntry(row));
        }
        else {
            selectionListener.onSelectionChanged(null);
        }
    }
}
