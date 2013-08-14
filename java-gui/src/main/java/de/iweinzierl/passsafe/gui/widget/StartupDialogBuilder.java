package de.iweinzierl.passsafe.gui.widget;


import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.util.UiUtils;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

public class StartupDialogBuilder {

    public interface ActionListener {
        void submitted(String password);

        void canceled();
    }

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
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(100, 20));

        okButton = new JButton(Messages.getMessage(Messages.START_DIALOG_OK_BUTTON));
        cancelButton = new JButton(Messages.getMessage(Messages.START_DIALOG_CANCEL_BUTTON));

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
        rootPane.setLayout(new BoxLayout(rootPane, BoxLayout.PAGE_AXIS));

        rootPane.add(WidgetFactory.createLabel(Messages.getMessage(Messages.START_DIALOG_LABEL), 100, 20));
        rootPane.add(WidgetFactory.createInputPanel(
                WidgetFactory.createLabel(Messages.getMessage(Messages.START_DIALOG_PASSWORD_LABEL), 100, 20),
                passwordField));
        rootPane.add(WidgetFactory.createButtonPanel(okButton, cancelButton));

        dialog.setPreferredSize(new Dimension(300, 150));
        dialog.pack();
        dialog.show();

        UiUtils.center(dialog);
    }
}

