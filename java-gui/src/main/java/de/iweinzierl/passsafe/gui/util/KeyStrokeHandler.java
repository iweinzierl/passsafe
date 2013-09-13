package de.iweinzierl.passsafe.gui.util;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.action.NewCategoryDialogAction;
import de.iweinzierl.passsafe.gui.action.NewEntryDialogAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class KeyStrokeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyStrokeHandler.class);

    private static KeyStrokeHandler keyStrokeHandler;

    private final ApplicationController controller;
    private final Map<KeyStroke, Action> keyStrokes;
    private final Map<Integer, Action> keys;

    private KeyStrokeHandler(ApplicationController controller) {
        this.keyStrokes = new HashMap<>();
        this.keys = new HashMap<>();
        this.controller = controller;

        keyStrokes.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK),
                new NewEntryDialogAction(controller, controller.getApplication()));

        keyStrokes.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK),
                new NewCategoryDialogAction(controller, controller.getApplication()));

        keys.put(KeyEvent.VK_ESCAPE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window[] windows = Window.getWindows();
                for (Window window: windows) {
                    if (window.isShowing() && window.isFocused() && window.isActive()) {
                        window.dispose();
                        return;
                    }
                }
            }
        });
    }

    public static KeyStrokeHandler getKeyStrokeHandler(ApplicationController controller) {
        if (keyStrokeHandler == null) {
            keyStrokeHandler = new KeyStrokeHandler(controller);
        }

        return keyStrokeHandler;
    }

    private Action getActionForEvent(KeyEvent event) {
        Action action = keyStrokes.get(KeyStroke.getKeyStrokeForEvent(event));

        if (action == null) {
            action = keys.get(event.getKeyCode());
        }

        return action;
    }

    public boolean handle(final KeyEvent event) {
        if (KeyStroke.getKeyStrokeForEvent(event).isOnKeyRelease()) {
            return false;
        }

        final Action action = getActionForEvent(event);

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
