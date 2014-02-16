package de.iweinzierl.passsafe.gui.widget;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import de.iweinzierl.passsafe.gui.resources.Messages;

public class EnterPasswordPanel extends JPanel {

    public interface ActionListener {
        void submitted(String password);

        void canceled();
    }

    public static class Builder {

        private EnterPasswordPanel panel;

        public Builder() {
            panel = new EnterPasswordPanel();
        }

        public Builder withTitle(final String title) {
            panel.setTitle(title);
            return this;
        }

        public Builder withLabel(final String label) {
            panel.setLabel(label);
            return this;
        }

        public Builder withListener(final ActionListener listener) {
            panel.setActionListener(listener);
            return this;
        }

        public EnterPasswordPanel build() {
            panel.init();
            return panel;
        }
    }

    public static final int LABEL_WIDTH = 325;
    public static final int LABEL_HEIGHT = 25;

    public static final int BUTTON_WIDTH = 125;
    public static final int BUTTON_HEIGHT = 25;

    public static final int PASSWORD_FIELD_WIDTH = 175;
    public static final int PASSWORD_FIELD_HEIGHT = 25;
    public static final int PASSWORD_LABEL_WIDTH = 75;
    public static final int PASSWORD_LABEL_HEIGHT = 25;

    private ActionListener actionListener;

    private JPasswordField passwordField;

    private String title;
    private String label;

    public EnterPasswordPanel() { }

    public void setActionListener(final ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    private void init() {

        passwordField = (JPasswordField) WidgetFactory.createComponent(JPasswordField.class, PASSWORD_FIELD_WIDTH,
                PASSWORD_FIELD_HEIGHT);

        JButton okButton = WidgetFactory.createButton(Messages.getMessage(Messages.START_DIALOG_OK_BUTTON),
                BUTTON_WIDTH, BUTTON_HEIGHT);

        JButton cancelButton = WidgetFactory.createButton(Messages.getMessage(Messages.START_DIALOG_CANCEL_BUTTON),
                BUTTON_WIDTH, BUTTON_HEIGHT);

        okButton.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    actionListener.submitted(passwordField.getText());
                }
            });

        cancelButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    actionListener.canceled();
                }
            });

        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(WidgetFactory.createLabel(title, LABEL_WIDTH, LABEL_HEIGHT));

        add(WidgetFactory.createInputPanel(
                WidgetFactory.createLabel(label, PASSWORD_LABEL_WIDTH, PASSWORD_LABEL_HEIGHT), passwordField));

        add(WidgetFactory.createButtonPanel(okButton, cancelButton));
    }
}
