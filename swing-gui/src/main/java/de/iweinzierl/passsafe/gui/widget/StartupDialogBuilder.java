package de.iweinzierl.passsafe.gui.widget;

import java.awt.Dimension;

import javax.swing.JFrame;

import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.util.UiUtils;

public class StartupDialogBuilder {

    public interface ActionListener {
        void submitted(String password);

        void canceled();
    }

    public static final int DIALOG_WIDTH = 315;
    public static final int DIALOG_HEIGHT = 150;

    private ActionListener actionListener;
    private String errorMessage;

    public StartupDialogBuilder(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public StartupDialogBuilder setActionListener(final ActionListener actionListener) {
        this.actionListener = actionListener;
        return this;
    }

    public void build() {
        final JFrame dialog = new JFrame();

        //J-
        EnterPasswordPanel enterPasswordPanel = new EnterPasswordPanel.Builder()
                .withTitle(Messages.getMessage(Messages.CHANGEPASSWORD_PANEL_TITLE))
                .withLabel(Messages.getMessage(Messages.CHANGEPASSWORD_PANEL_LABEL))
                .withErrorMessage(errorMessage)
                .withListener(new EnterPasswordPanel.ActionListener() {
                    @Override
                    public void submitted(final String password) {
                        dialog.dispose();
                        actionListener.submitted(password);
                    }

                    @Override
                    public void canceled() {
                        dialog.dispose();
                        actionListener.canceled();
                    }
                }).build();
        //J+

        dialog.setTitle(Messages.getMessage(Messages.START_DIALOG_TITLE));
        dialog.setContentPane(enterPasswordPanel);

        dialog.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        dialog.setMinimumSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        dialog.setResizable(false);
        dialog.pack();
        dialog.setVisible(true);

        UiUtils.center(dialog);
    }
}
