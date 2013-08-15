package de.iweinzierl.passsafe.gui.widget;

import de.iweinzierl.passsafe.gui.Application;
import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.resources.Errors;
import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.util.UiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class ButtonBar extends JPanel {

    public static final int BUTTON_WIDTH = 100;
    public static final int BUTTON_HEIGHT = 25;

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
        add(createAddCategoryButton());
        add(createSyncButton());
    }

    private JButton createAddEntryButton() {
        return WidgetFactory.createButton(Messages.getMessage(Messages.BUTTONBAR_NEWENTRY), BUTTON_WIDTH, BUTTON_HEIGHT,
                new ActionListener() {
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

    private JButton createAddCategoryButton() {
        return WidgetFactory.createButton(Messages.getMessage(Messages.BUTTONBAR_NEWCATEGORY), BUTTON_WIDTH,
                BUTTON_HEIGHT, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NewCategoryDialog(parent, controller).show();
            }
        });
    }

    private JButton createSyncButton() {
        return WidgetFactory.createButton(Messages.getMessage(Messages.BUTTONBAR_SYNC), BUTTON_WIDTH, BUTTON_HEIGHT,
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            controller.requestSync();
                        } catch (IOException ex) {
                            LOGGER.error("Unable to sync", ex);
                            UiUtils.displayError(null, Errors.getError(Errors.SYNC_FAILED));
                        }
                    }
                });
    }
}
