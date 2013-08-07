package de.iweinzierl.passsafe.gui.widget.table;

import de.iweinzierl.passsafe.gui.data.Entry;
import de.iweinzierl.passsafe.gui.resources.Messages;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EntryTableModel extends AbstractTableModel {

    public static final String[] COLUMNS_NAMES = {Messages.getMessage(
            Messages.ENTRYTABLE_COLUMN_TITLE), Messages.getMessage(
            Messages.ENTRYTABLE_COLUMN_USERNAME), Messages.getMessage(Messages.ENTRYTABLE_COLUMN_PASSWORD)};

    public static final Class[] COLUMNS_CLASSES = {String.class, String.class, String.class};

    private List<Entry> entries;

    public EntryTableModel() {
        super();
        this.entries = new ArrayList<>(0);
    }

    public Entry getEntry(int row) {
        if (row < entries.size()) {
            return entries.get(row);
        }

        return null;
    }

    @Override
    public int getRowCount() {
        return entries.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS_NAMES.length;
    }

    @Override
    public String getColumnName(int i) {
        return COLUMNS_NAMES[i];
    }

    @Override
    public Class<?> getColumnClass(int i) {
        return COLUMNS_CLASSES[i];
    }

    @Override
    public boolean isCellEditable(int i, int i2) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (row < entries.size()) {
            Entry entry = entries.get(row);

            switch (column) {

                case 0:
                    return entry.getTitle();
                case 1:
                    return "********";
                case 2:
                    return "********";
                default:
                    return "???";
            }
        }

        return null;
    }

    @Override
    public void setValueAt(Object o, int i, int i2) {
        // table will not be editable
    }

    public void setEntries(List<Entry> entries) {
        if (entries == null) {
            this.entries = new ArrayList<>();
        } else {
            this.entries = entries;
        }
    }
}
