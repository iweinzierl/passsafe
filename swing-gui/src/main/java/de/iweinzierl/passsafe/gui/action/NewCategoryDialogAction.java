package de.iweinzierl.passsafe.gui.action;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.widget.NewCategoryDialog;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;

public class NewCategoryDialogAction extends AbstractAction {

    private final ApplicationController controller;
    private final JFrame parent;

    public NewCategoryDialogAction(ApplicationController controller, JFrame parent) {
        this.controller = controller;
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NewCategoryDialog.show(parent, controller);
    }
}
