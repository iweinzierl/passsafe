package de.iweinzierl.passsafe.gui.widget;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionListener;

import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iweinzierl.passsafe.gui.resources.Images;

public class WidgetFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetFactory.class);

    public static final int DEFAULT_ICON_WIDTH = 16;
    public static final int DEFAULT_ICON_HEIGHT = 16;

    public static JButton createImageButton(final String key) {
        try {
            ImageIcon imageIcon = Images.getImageIcon(key);
            Image scaledInstance = imageIcon.getImage().getScaledInstance(DEFAULT_ICON_WIDTH, DEFAULT_ICON_HEIGHT,
                    Image.SCALE_SMOOTH);

            return new JButton(new ImageIcon(scaledInstance));

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("Unable to load icon '" + key + "'");
        }
    }

    public static JComponent createComponent(final Class<? extends JComponent> targetClass, final int width,
            final int height) {

        try {
            JComponent jComponent = targetClass.newInstance();
            jComponent.setPreferredSize(new Dimension(width, height));

            return jComponent;

        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("Cannot create instance of '{}'", targetClass.toString(), e);
        }

        return null;
    }

    public static JLabel createLabel(final String text, final int width, final int height) {
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(width, height));

        return label;
    }

    public static Component createErrorLabel(final String errorMessage, final int width, final int height) {
        JLabel label = createLabel(errorMessage, width, height);
        label.setForeground(Color.RED);

        return label;
    }

    public static JButton createButton(final String text, final int width, final int height,
            final ActionListener listener) {
        JButton button = createButton(text, width, height);
        button.addActionListener(listener);

        return button;
    }

    public static JButton createButton(final String text, final int width, final int height) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, height));

        return button;
    }

    public static JPanel createInputPanel(final JLabel label, final JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        panel.add(label);
        panel.add(textField);

        return panel;
    }

    public static JPanel createButtonPanel(final JButton... buttons) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        for (JButton button : buttons) {
            panel.add(button);
        }

        return panel;
    }
}
