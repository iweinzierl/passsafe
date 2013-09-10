package de.iweinzierl.passsafe.gui.util;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.action.NewCategoryDialogAction;
import de.iweinzierl.passsafe.gui.action.NewEntryDialogAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class KeyStrokeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyStrokeHandler.class);

    private static KeyStrokeHandler keyStrokeHandler;

    private final ApplicationController controller;
    private final Map<KeyStroke, Action> actions;

    private KeyStrokeHandler(ApplicationController controller) {
        this.actions = new HashMap<>();
        this.controller = controller;

        actions.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK),
                new NewEntryDialogAction(controller, controller.getApplication()));

        actions.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK),
                new NewCategoryDialogAction(controller, controller.getApplication()));
    }

    public static KeyStrokeHandler getKeyStrokeHandler(ApplicationController controller) {
        if (keyStrokeHandler == null) {
            keyStrokeHandler = new KeyStrokeHandler(controller);
        }

        return keyStrokeHandler;
    }

    public boolean handle(final KeyEvent event) {
        final Action action = actions.get(KeyStroke.getKeyStrokeForEvent(event));

        if (action != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    action.actionPerformed(new ActionEvent(event.getSource(), event.getID(), null));
                }
            });

            return true;
        }

        return false;
    }
}
