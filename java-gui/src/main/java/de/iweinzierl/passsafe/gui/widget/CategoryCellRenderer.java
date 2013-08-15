package de.iweinzierl.passsafe.gui.widget;

import de.iweinzierl.passsafe.gui.data.EntryCategory;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class CategoryCellRenderer implements ListCellRenderer<EntryCategory> {

    @Override
    public Component getListCellRendererComponent(JList<? extends EntryCategory> list, EntryCategory value, int index,
            boolean isSelected, boolean cellHasFocus) {

        JLabel label = new JLabel(value.getTitle());

        if (isSelected) {
            label.setForeground(Color.BLUE);
        }

        return label;
    }
}
