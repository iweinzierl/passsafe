package de.iweinzierl.passsafe.gui.widget;

import de.iweinzierl.passsafe.gui.Application;
import de.iweinzierl.passsafe.gui.ApplicationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


public class ButtonBar extends JPanel {

    private Logger LOGGER = LoggerFactory.getLogger(ButtonBar.class);

    private ApplicationController controller;
    private Application parent;


    public ButtonBar(ApplicationController controller, Application parent) {
        super();
        this.controller = controller;
        this.parent = parent;

        initialize();
    }


    private void initialize() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(createAddEntryButton());
        add(createDeleteEntryButton());
    }


    private JButton createButton(String label, ActionListener clickListener) {
        JButton button = new JButton(label);
        button.addActionListener(clickListener);

        return button;
    }


    private JButton createAddEntryButton() {
        return createButton("Neuer Eintrag", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LOGGER.debug("Clicked 'new entry'");
                List<NewEntryDialog.OnEntryAddedListener> listeners = new ArrayList<>();
                listeners.add(controller);

                new NewEntryDialog(parent, listeners).show();
            }
        });
    }


    private JButton createDeleteEntryButton() {
        return createButton("Eintrag entfernen", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LOGGER.debug("Clicked 'delete entry'");
            }
        });
    }
}
