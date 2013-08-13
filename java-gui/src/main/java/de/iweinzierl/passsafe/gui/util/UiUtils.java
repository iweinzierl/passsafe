package de.iweinzierl.passsafe.gui.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;


public class UiUtils {

    public static void center(Component component) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - component.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - component.getHeight()) / 2);
        component.setLocation(x, y);
    }

    public static void displayError(String text) {
        // TODO
    }
}
