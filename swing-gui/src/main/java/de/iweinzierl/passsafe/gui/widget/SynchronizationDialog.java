package de.iweinzierl.passsafe.gui.widget;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class SynchronizationDialog extends JDialog {

    public SynchronizationDialog() {
        super();
        init();
    }

    private void init() {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.LINE_AXIS));

        contentPane.add(WidgetFactory.createLabel("Synchronisierung der PassSafe Datenbank...", 200, 90));
        contentPane.setSize(new Dimension(300, 100));
        contentPane.setPreferredSize(new Dimension(300, 100));

        // TODO 1) text is currently not display - why?
        // TODO 2) i18n text

        setContentPane(contentPane);
        setTitle("Synchronisierung");

        setPreferredSize(new Dimension(300, 100));
        setVisible(true);
        pack();
    }
}
