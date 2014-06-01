package de.iweinzierl.passsafe.gui.widget.secret;

import java.awt.Dimension;

import javax.swing.JDialog;

import com.google.common.base.Preconditions;

import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.util.UiUtils;
import de.iweinzierl.passsafe.gui.widget.EnterPasswordPanel;

public class PasswordInputDialog extends JDialog {

    public interface Listener {
        void onSubmit(String password);
    }

    public static class Builder {

        private PasswordInputDialog dialog;

        public Builder() {
            dialog = new PasswordInputDialog();
        }

        public Builder withListener(final Listener listener) {
            dialog.setListener(listener);
            return this;
        }

        public PasswordInputDialog build() {
            Preconditions.checkNotNull(dialog.listener);
            dialog.initialize();
            return dialog;
        }
    }

    private Listener listener;

    private PasswordInputDialog() { }

    public void setListener(final Listener listener) {
        this.listener = listener;
    }

    private void initialize() {
        setPreferredSize(new Dimension(350, 150));
        setMinimumSize(new Dimension(350, 150));

        //J-
        EnterPasswordPanel enterPasswordPanel = new EnterPasswordPanel.Builder()
                .withTitle(Messages.getMessage(Messages.CHANGEPASSWORD_PANEL_TITLE))
                .withLabel(Messages.getMessage(Messages.CHANGEPASSWORD_PANEL_LABEL))
                .withListener(new EnterPasswordPanel.ActionListener() {
                    @Override
                    public void submitted(
                        final String password) {
                            PasswordInputDialog.this.dispose();
                            listener.onSubmit(password);
                        }

                        @Override
                        public void canceled() {
                            PasswordInputDialog.this.dispose();
                        }
                }).build();
        //J+

        setContentPane(enterPasswordPanel);
        UiUtils.center(this);
    }
}
