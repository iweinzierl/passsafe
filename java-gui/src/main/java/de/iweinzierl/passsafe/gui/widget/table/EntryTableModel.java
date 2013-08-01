package de.iweinzierl.passsafe.gui.widget.table;

import de.iweinzierl.passsafe.gui.data.Entry;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

public class EntryTableModel implements TableModel {

    public static final String[] COLUMNS_NAMES = {"title", "username", "password"};
    public static final Class[] COLUMNS_CLASSES = {String.class, String.class, String.class};

    private List<Entry> entries;

    public EntryTableModel() {
        this.entries = new ArrayList<>(0);
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

                case 0: return entry.getTitle();
                case 1: return "********";
                case 2: return "********";
                default: return "???";
            }
        }

        return null;
    }

    @Override
    public void setValueAt(Object o, int i, int i2) {
        // table will not be editable
    }

    @Override
    public void addTableModelListener(TableModelListener tableModelListener) {
        // TODO
    }

    @Override
    public void removeTableModelListener(TableModelListener tableModelListener) {
        // TODO
    }

    public void setEntries(List<Entry> entries) {
        if (entries == null) {
            this.entries = new ArrayList<>();
        }
        else {
            this.entries = entries;
        }
    }
}
