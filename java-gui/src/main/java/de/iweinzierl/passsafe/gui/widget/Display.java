package de.iweinzierl.passsafe.gui.widget;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.widget.table.EntryTable;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

public class Display extends JPanel {

    private ApplicationController controller;

    private EntryTable entryTable;
    private EntryView entryView;

    public Display(ApplicationController controller, EntryTable entryTable, EntryView entryView) {
        super();

        this.controller = controller;
        this.entryTable = entryTable;
        this.entryView = entryView;

        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        add(new JScrollPane(entryTable), BorderLayout.CENTER);
        add(new JScrollPane(entryView), BorderLayout.SOUTH);
    }
}
