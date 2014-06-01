package de.iweinzierl.passsafe.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.widget.NewEntryDialog;

public class NewEntryDialogAction extends AbstractAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewEntryDialogAction.class);

    private final ApplicationController controller;
    private final JFrame parent;

    public NewEntryDialogAction(final ApplicationController controller, final JFrame parent) {
        this.controller = controller;
        this.parent = parent;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        LOGGER.debug("Clicked 'new entry'");

        NewEntryDialog.show(controller, parent, controller.getPasswordHandler(),
            Lists.<NewEntryDialog.OnEntryAddedListener>newArrayList(controller));
    }
}
