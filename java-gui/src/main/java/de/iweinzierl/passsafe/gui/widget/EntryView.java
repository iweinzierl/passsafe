package de.iweinzierl.passsafe.gui.widget;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.data.Entry;
import de.iweinzierl.passsafe.gui.exception.PassSafeSecurityException;
import de.iweinzierl.passsafe.gui.resources.Images;
import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.util.UiUtils;
import de.iweinzierl.passsafe.gui.widget.secret.SwitchablePasswordField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

// TODO make text fields smaller
public class EntryView extends JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntryView.class);

    private class ClipboardMenu extends JMenuItem {

        private ClipboardMenu(JComponent component) {
            super(new ClipboardMenuAction(component));
        }
    }

    private class ClipboardMenuAction implements Action {

        private JComponent component;

        private ClipboardMenuAction(JComponent component) {
            this.component = component;
        }

        @Override
        public Object getValue(String key) {
            switch (key) {
                case "Name":
                    return Messages.getMessage(Messages.ENTRYVIEW_MENU_COPYTOCLIPBOARD);
            }

            return null;
        }

        @Override
        public void putValue(String key, Object value) {
            // nothing to do here
        }

        @Override
        public void setEnabled(boolean b) {
            // nothing to do here
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            // nothing to do here
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            // nothing to do here
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Clipboard clipboard = getToolkit().getSystemClipboard();
            String text = null;
            if (component instanceof JTextField) {
                text = ((JTextField) component).getText();
            } else if (component instanceof SwitchablePasswordField) {
                try {
                    text = ((SwitchablePasswordField) component).getPassword();
                    UiUtils.displayError("TODO");
                } catch (PassSafeSecurityException e1) {
                    LOGGER.error("Unable to retrieve password from password field", e);
                    return;
                }
            }

            StringSelection stringSelection = new StringSelection(text);
            clipboard.setContents(stringSelection, null);
        }
    }

    final private ApplicationController controller;

    final private JTextField titleField;
    final private SwitchablePasswordField usernameField;
    final private SwitchablePasswordField passwordField;

    private Entry entry;

    public EntryView(ApplicationController controller) {
        super();
        this.controller = controller;

        titleField = new JTextField();
        usernameField = new SwitchablePasswordField();
        passwordField = new SwitchablePasswordField();

        initialize();
    }

    private void initialize() {
        setLayout(new FlowLayout(FlowLayout.CENTER));

        titleField.setEditable(false);
        usernameField.setEditable(false);
        passwordField.setEditable(false);

        add(createTitleRow());
        add(createUsernameRow());
        add(createPasswordRow());

        setMinimumSize(new Dimension(425, 125));
        setPreferredSize(new Dimension(425, 125));
        setMaximumSize(new Dimension(700, 200));
    }

    private JPanel createRow(JLabel label, JComponent inputField, JPanel buttons) {
        JPanel row = new JPanel();
        row.setLayout(new FlowLayout(FlowLayout.LEFT));

        label.setPreferredSize(new Dimension(75, 25));
        inputField.setPreferredSize(new Dimension(150, 25));
        buttons.setPreferredSize((new Dimension(200, 25)));

        row.add(label);
        row.add(inputField);
        row.add(buttons);

        return row;
    }

    private JPanel createTitleRow() {

        initializeClipboardFunctions(titleField);

        return createRow(new JLabel(Messages.getMessage(Messages.ENTRYVIEW_LABEL_TITLE)), titleField,
                createStandardButtons(titleField));
    }

    private JPanel createUsernameRow() {

        initializeClipboardFunctions(usernameField);

        return createRow(new JLabel(Messages.getMessage(Messages.ENTRYVIEW_LABEL_USERNAME)), usernameField,
                createSecretButtons(usernameField));
    }

    private JPanel createPasswordRow() {

        initializeClipboardFunctions(passwordField);

        return createRow(new JLabel(Messages.getMessage(Messages.ENTRYVIEW_LABEL_PASSWORD)), passwordField,
                createSecretButtons(passwordField));
    }

    private void initializeClipboardFunctions(JComponent textField) {
        JPopupMenu componentPopupMenu = textField.getComponentPopupMenu();
        if (componentPopupMenu == null) {
            componentPopupMenu = new JPopupMenu(Messages.getMessage(Messages.ENTRYVIEW_MENU_TITLE));
            textField.setComponentPopupMenu(componentPopupMenu);
        }

        componentPopupMenu.add(new ClipboardMenu(textField));
    }

    public void apply(Entry entry) {
        if (entry != null) {
            this.entry = entry;
            titleField.setText(entry.getTitle());
            try {
                usernameField.setPassword(controller.getPasswordHandler().decrypt(entry.getUsername()));
                passwordField.setPassword(controller.getPasswordHandler().decrypt(entry.getPassword()));
            }
            catch (PassSafeSecurityException e) {
                LOGGER.error("Unable to set password to password field", e);
                UiUtils.displayError("TODO");
            }
        }
    }

    public void reset() {
        titleField.setText("");

        try {
            usernameField.setPassword(null);
            passwordField.setPassword(null);
        }
        catch (PassSafeSecurityException e) {
            LOGGER.error("Unable to reset password fields", e);
            UiUtils.displayError("TODO");
        }
    }

    private JPanel createStandardButtons(final JTextField textField) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        final JButton edit = WidgetFactory.createImageButton(Images.ENTRYVIEW_BUTTON_EDIT);
        final JButton save = WidgetFactory.createImageButton(Images.ENTRYVIEW_BUTTON_SAVE);
        final JButton cancel = WidgetFactory.createImageButton(Images.ENTRYVIEW_BUTTON_CANCEL);

        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField.setEditable(true);
                edit.setEnabled(false);
                save.setEnabled(true);
                cancel.setEnabled(true);
            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO save entry
                textField.setEditable(false);
                save.setEnabled(false);
                cancel.setEnabled(false);
                edit.setEnabled(true);
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField.setEditable(false);
                save.setEnabled(false);
                cancel.setEnabled(false);
                edit.setEnabled(true);

                if (entry != null) {
                    textField.setText(entry.getTitle()); // XXX specific to title property!
                }
            }
        });

        save.setEnabled(false);
        cancel.setEnabled(false);

        panel.add(edit);
        panel.add(save);
        panel.add(cancel);

        return panel;
    }

    private JPanel createSecretButtons(final SwitchablePasswordField secretField) {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        final JButton edit = WidgetFactory.createImageButton(Images.ENTRYVIEW_BUTTON_EDIT);
        final JButton save = WidgetFactory.createImageButton(Images.ENTRYVIEW_BUTTON_SAVE);
        final JButton cancel = WidgetFactory.createImageButton(Images.ENTRYVIEW_BUTTON_CANCEL);
        final JButton visible = WidgetFactory.createImageButton(Images.ENTRYVIEW_BUTTON_VISIBLE);
        final JButton invisible = WidgetFactory.createImageButton(Images.ENTRYVIEW_BUTTON_INVISIBLE);

        edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secretField.setEditable(true);
                edit.setEnabled(false);
                save.setEnabled(true);
                cancel.setEnabled(true);
            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO save entry
                secretField.setEditable(false);
                save.setEnabled(false);
                cancel.setEnabled(false);
                edit.setEnabled(true);
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secretField.setEditable(false);
                save.setEnabled(false);
                cancel.setEnabled(false);
                edit.setEnabled(true);
            }
        });

        visible.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secretField.showPassword();
                invisible.setVisible(true);
                visible.setVisible(false);
            }
        });

        invisible.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secretField.hidePassword();
                invisible.setVisible(false);
                visible.setVisible(true);
            }
        });

        save.setEnabled(false);
        cancel.setEnabled(false);
        invisible.setVisible(false);

        panel.add(visible);
        panel.add(invisible);
        panel.add(edit);
        panel.add(save);
        panel.add(cancel);

        return panel;
    }
}
