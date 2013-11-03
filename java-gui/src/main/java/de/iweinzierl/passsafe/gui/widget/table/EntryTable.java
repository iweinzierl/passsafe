package de.iweinzierl.passsafe.gui.widget.table;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.shared.domain.Entry;

public class EntryTable extends JTable {

    public interface SelectionListener {
        void onSelectionChanged(Entry entry);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(EntryTable.class);

    private final ApplicationController controller;
    private final SelectionListener selectionListener;

    public EntryTable(final ApplicationController controller, final EntryTableModel model,
            final SelectionListener selectionListener) {
        super(model);
        this.controller = controller;
        this.selectionListener = selectionListener;

        initializeHeaders();
        initializeKeyListener();
    }

    private void initializeHeaders() {
        JTableHeader headers = getTableHeader();
        headers.setReorderingAllowed(false);
        headers.setResizingAllowed(true);
    }

    private void initializeKeyListener() {
        addKeyListener(new EntryTableKeyHandler(this, controller));
    }

    public EntryTableModel getEntryTableModel() {
        return (EntryTableModel) getModel();
    }

    public void tableChanged() {
        LOGGER.debug("EntryTable model changed. Entries: {}", getModel().getRowCount());
        tableChanged(new TableModelEvent(getEntryTableModel()));
    }

    @Override
    public void valueChanged(final ListSelectionEvent e) {
        super.valueChanged(e);

        int row = getSelectedRow();

        if (row >= 0) {
            selectionListener.onSelectionChanged(getEntryTableModel().getEntry(row));
        } else {
            selectionListener.onSelectionChanged(null);
        }
    }
}
