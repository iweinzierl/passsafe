package de.iweinzierl.passsafe.gui.util;

import de.iweinzierl.passsafe.gui.resources.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;


public class UiUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(UiUtils.class);

    public static final int DEFAULT_ICON_WIDTH = 16;
    public static final int DEFAULT_ICON_HEIGHT = 16;

    public static void center(Component component) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - component.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - component.getHeight()) / 2);
        component.setLocation(x, y);
    }

    public static JButton createImageButton(String key) {
        try {
            ImageIcon imageIcon = Images.getImageIcon(key);
            Image scaledInstance = imageIcon.getImage().getScaledInstance(DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT,
                    Image.SCALE_SMOOTH);

            JButton button = new JButton(new ImageIcon(scaledInstance));

            return button;
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("Unable to load icon '" + key + "'");
        }
    }
}
