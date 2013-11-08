package de.iweinzierl.passsafe.gui.widget.secret;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;

import de.iweinzierl.passsafe.gui.resources.Images;
import de.iweinzierl.passsafe.gui.widget.WidgetFactory;

public class PasswordInputPanel extends JPanel {

    public static class Builder {

        public PasswordInputPanel build() {
            PasswordInputPanel panel = new PasswordInputPanel();
            panel.build();

            return panel;
        }
    }

    private SwitchablePasswordField passwordField;
    private SwitchablePasswordField verifyPasswordField;

    private Border origBorder;

    private PasswordInputPanel() {
        passwordField = new SwitchablePasswordField();
        verifyPasswordField = new SwitchablePasswordField();
    }

    public String getPassword() {
        return passwordField.getPassword();
    }

    public boolean verifyFields() {
        if (!arePasswordsEqual()) {
            saveOrigBorder();
            verifyPasswordField.setBorder(BorderFactory.createLineBorder(Color.RED));
            return false;
        }

        return true;
    }

    public void reset() {
        clearBorder();
        passwordField.setPassword(null);
        verifyPasswordField.setPassword(null);
    }

    public void clearBorder() {
        verifyPasswordField.setBorder(origBorder);
    }

    private boolean arePasswordsEqual() {
        String password = passwordField.getPassword();
        String verificationPassword = verifyPasswordField.getPassword();

        return password != null && password.equals(verificationPassword);
    }

    private void saveOrigBorder() {
        if (origBorder == null) {
            origBorder = verifyPasswordField.getBorder();
        }
    }

    private void build() {
        GridBagConstraints constraints = new GridBagConstraints();
        GridBagLayout layout = new GridBagLayout();

        setLayout(layout);
        setPreferredSize(new Dimension(400, 60));
        setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;

        passwordField.setPreferredSize(new Dimension(350, 25));
        passwordField.setSize(new Dimension(350, 25));
        passwordField.setMaximumSize(new Dimension(400, 25));
        verifyPasswordField.setPreferredSize(new Dimension(350, 25));
        verifyPasswordField.setSize(new Dimension(350, 25));
        verifyPasswordField.setMaximumSize(new Dimension(350, 25));

        Component verifyHide = createHideButton(verifyPasswordField);
        Component verifyShow = createShowButton(verifyPasswordField);

        constraints.gridwidth = 2;
        layout.setConstraints(verifyHide, constraints);
        layout.setConstraints(verifyShow, constraints);
        add(verifyHide);
        add(verifyShow);

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(verifyPasswordField, constraints);
        add(verifyPasswordField);

        Component hidePassword = createHideButton(passwordField);
        Component showPassword = createShowButton(passwordField);

        constraints.gridwidth = 2;
        layout.setConstraints(hidePassword, constraints);
        layout.setConstraints(showPassword, constraints);
        add(hidePassword);
        add(showPassword);

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(passwordField, constraints);
        add(passwordField);
    }

    protected Component createShowButton(final SwitchablePasswordField field) {
        JButton imageButton = WidgetFactory.createImageButton(Images.ENTRYVIEW_BUTTON_VISIBLE);
        imageButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    field.showPassword();
                }
            });

        imageButton.setMaximumSize(new Dimension(55, 55));
        imageButton.setSize(new Dimension(55, 55));
        return imageButton;
    }

    protected Component createHideButton(final SwitchablePasswordField field) {
        JButton imageButton = WidgetFactory.createImageButton(Images.ENTRYVIEW_BUTTON_INVISIBLE);
        imageButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    field.hidePassword();
                }
            });

        return imageButton;
    }
}
