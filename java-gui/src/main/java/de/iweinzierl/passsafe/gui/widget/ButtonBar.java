package de.iweinzierl.passsafe.gui.widget;

import de.iweinzierl.passsafe.gui.Application;
import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.resources.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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
        return createButton(Messages.getMessage(Messages.BUTTONBAR_NEWENTRY), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LOGGER.debug("Clicked 'new entry'");

                NewEntryDialog dialog = new NewEntryDialog(parent, controller.getDataSource().getCategories(),
                        controller.getPasswordHandler());
                dialog.addOnEntryAddedListeners(controller);
                dialog.show();
            }
        });
    }


    private JButton createDeleteEntryButton() {
        return createButton(Messages.getMessage(Messages.BUTTONBAR_REMOVEENTRY), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LOGGER.debug("Clicked 'delete entry'");
            }
        });
    }
}
