package de.iweinzierl.passsafe.gui.action;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.widget.NewEntryDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;

public class NewEntryDialogAction extends AbstractAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewEntryDialogAction.class);

    private final ApplicationController controller;
    private final JFrame parent;

    public NewEntryDialogAction(ApplicationController controller, JFrame parent) {
        this.controller = controller;
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOGGER.debug("Clicked 'new entry'");

        NewEntryDialog dialog = NewEntryDialog.show(controller, parent, controller.getPasswordHandler());
        dialog.addOnEntryAddedListeners(controller);
    }
}
