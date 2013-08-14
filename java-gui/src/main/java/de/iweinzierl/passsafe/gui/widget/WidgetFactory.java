package de.iweinzierl.passsafe.gui.widget;

import de.iweinzierl.passsafe.gui.resources.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.IOException;

public class WidgetFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetFactory.class);

    public static final int DEFAULT_ICON_WIDTH = 16;
    public static final int DEFAULT_ICON_HEIGHT = 16;

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

    public static JComponent createComponent(Class<? extends JComponent> targetClass, int width, int height) {

        try {
            JComponent jComponent = targetClass.newInstance();
            jComponent.setPreferredSize(new Dimension(width, height));

            return jComponent;

        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("Cannot create instance of '{}'", targetClass.toString(), e);
        }

        return null;
    }

    public static JLabel createLabel(String text, int width, int height) {
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(width, height));

        return label;
    }

    public static JButton createButton(String text, int width, int height, ActionListener listener) {
        JButton button = createButton(text, width, height);
        button.addActionListener(listener);

        return button;
    }

    public static JButton createButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));

        return button;
    }

    public static JPanel createInputPanel(String label, JTextField textField) {
        return createInputPanel(label, textField);
    }

    public static JPanel createInputPanel(JLabel label, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        panel.add(label);
        panel.add(textField);

        return panel;
    }

    public static JPanel createButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        for (JButton button : buttons) {
            panel.add(button);
        }

        return panel;
    }
}