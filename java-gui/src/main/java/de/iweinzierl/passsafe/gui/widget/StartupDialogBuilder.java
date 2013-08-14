package de.iweinzierl.passsafe.gui.widget;


import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.util.UiUtils;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

public class StartupDialogBuilder {

    public interface ActionListener {
        void submitted(String password);

        void canceled();
    }

    public static final int DIALOG_WIDTH = 315;
    public static final int DIALOG_HEIGHT = 150;
    public static final int DIALOG_LABEL_WIDTH = 325;
    public static final int DIALOG_LABEL_HEIGHT = 25;
    public static final int BUTTON_WIDTH = 125;
    public static final int BUTTON_HEIGHT = 25;
    public static final int PASSWORD_FIELD_WIDTH = 175;
    public static final int PASSWORD_FIELD_HEIGHT = 25;
    public static final int PASSWORD_LABEL_WIDTH = 75;
    public static final int PASSWORD_LABEL_HEIGHT = 25;

    private ActionListener actionListener;

    private JPasswordField passwordField;
    private JButton okButton;
    private JButton cancelButton;

    public StartupDialogBuilder() {
    }

    public StartupDialogBuilder setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
        return this;
    }

    public void build() {
        final JFrame dialog = new JFrame();
        passwordField = (JPasswordField) WidgetFactory.createComponent(JPasswordField.class, PASSWORD_FIELD_WIDTH,
                PASSWORD_FIELD_HEIGHT);

        okButton = WidgetFactory.createButton(Messages.getMessage(Messages.START_DIALOG_OK_BUTTON), BUTTON_WIDTH,
                BUTTON_HEIGHT);

        cancelButton = WidgetFactory.createButton(Messages.getMessage(Messages.START_DIALOG_CANCEL_BUTTON),
                BUTTON_WIDTH, BUTTON_HEIGHT);

        okButton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                actionListener.submitted(passwordField.getText());
                dialog.dispose();
            }
        });

        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionListener.canceled();
                dialog.dispose();
            }
        });

        dialog.setTitle(Messages.getMessage(Messages.START_DIALOG_TITLE));
        JRootPane rootPane = dialog.getRootPane();
        rootPane.setLayout(new FlowLayout(FlowLayout.LEFT));

        rootPane.add(WidgetFactory.createLabel(Messages.getMessage(Messages.START_DIALOG_LABEL), DIALOG_LABEL_WIDTH,
                DIALOG_LABEL_HEIGHT));

        rootPane.add(WidgetFactory.createInputPanel(
                WidgetFactory.createLabel(Messages.getMessage(Messages.START_DIALOG_PASSWORD_LABEL),
                        PASSWORD_LABEL_WIDTH, PASSWORD_LABEL_HEIGHT), passwordField));

        rootPane.add(WidgetFactory.createButtonPanel(okButton, cancelButton));

        dialog.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        dialog.setMinimumSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        dialog.setResizable(false);
        dialog.pack();
        dialog.show();

        UiUtils.center(dialog);
    }
}

