package de.iweinzierl.passsafe.gui.widget;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.repackaged.com.google.common.base.Strings;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.exception.PassSafeException;
import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.secure.PasswordHandler;
import de.iweinzierl.passsafe.gui.util.UiUtils;
import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class NewEntryDialog extends JDialog {

    public interface OnEntryAddedListener {
        void onEntryAdded(EntryCategory category, Entry entry);
    }

    public static final int TEXT_COLUMNS = 30;
    public static final int LABEL_WIDTH = 100;
    public static final int LABEL_HEIGHT = 15;
    public static final int TEXT_WIDTH = 100;
    public static final int TEXT_HEIGHT = 15;

    public static final int DEFAULT_WIDTH = 450;
    public static final int DEFAULT_HEIGHT = 225;

    private static final Logger LOGGER = LoggerFactory.getLogger(NewEntryDialog.class);

    private static NewEntryDialog INSTANCE;

    private final ApplicationController controller;

    private final PasswordHandler passwordHandler;

    private List<OnEntryAddedListener> onEntryAddedListeners;

    private JComboBox<EntryCategory> categoryBox;
    private JTextField titleField;
    private JTextField usernameField;
    private JTextField passwordField;
    private JTextField passwordVerifyField;

    private Border origBorder;

    private NewEntryDialog(final JFrame parent, final ApplicationController controller,
            final PasswordHandler passwordHandler, final List<OnEntryAddedListener> onEntryAddedListeners) {
        super(parent, Messages.getMessage(Messages.NEWENTRYDIALOG_TITLE), true);
        this.controller = controller;
        this.passwordHandler = passwordHandler;
        this.onEntryAddedListeners = onEntryAddedListeners;
        initialize();
    }

    public static NewEntryDialog show(final ApplicationController controller, final JFrame parent,
            final PasswordHandler handler, final List<OnEntryAddedListener> onEntryAddedListeners) {
        if (INSTANCE == null) {
            INSTANCE = new NewEntryDialog(parent, controller, handler, onEntryAddedListeners);
        }

        if (!INSTANCE.isShowing()) {
            INSTANCE.reset();
        }

        INSTANCE.setVisible(true);

        return INSTANCE;
    }

    public void initialize() {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        contentPane.add(createCategoryPanel());
        contentPane.add(createTitlePanel());
        contentPane.add(createUsernamePanel());
        contentPane.add(createPasswordPanel());
        contentPane.add(createPasswordVerifyPanel());
        contentPane.add(createButtons());

        contentPane.setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        contentPane.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setContentPane(contentPane);

        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        UiUtils.center(this);

        this.origBorder = titleField.getBorder();
    }

    public void addOnEntryAddedListeners(final OnEntryAddedListener onEntryAddedListener) {
        if (onEntryAddedListener != null && !onEntryAddedListeners.contains(onEntryAddedListener)) {
            onEntryAddedListeners.add(onEntryAddedListener);
        }
    }

    private JPanel createRow(final JLabel label, final JComponent inputField) {
        JPanel row = new JPanel();
        row.setLayout(new FlowLayout(FlowLayout.LEFT));

        label.setPreferredSize(new Dimension(75, 25));
        inputField.setPreferredSize(new Dimension(150, 25));

        row.add(label);
        row.add(inputField);

        return row;
    }

    private JLabel createLabel(final String text) {
        JLabel label = new JLabel(text);
        label.setSize(new Dimension(100, 10));

        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(TEXT_COLUMNS);
        textField.setSize(new Dimension(TEXT_WIDTH, TEXT_HEIGHT));
        return textField;
    }

    private Component createPasswordPanel() {
        passwordField = new JPasswordField(TEXT_COLUMNS);
        return createRow(createLabel(Messages.getMessage(Messages.NEWENTRYDIALOG_LABEL_PASSWORD)), passwordField);
    }

    private Component createPasswordVerifyPanel() {
        passwordVerifyField = new JPasswordField(TEXT_COLUMNS);
        return createRow(createLabel(Messages.getMessage(Messages.NEWENTRYDIALOG_LABEL_VERIFYPASSWORD)),
                passwordVerifyField);
    }

    private Component createUsernamePanel() {
        usernameField = createTextField();
        return createRow(createLabel(Messages.getMessage(Messages.NEWENTRYDIALOG_LABEL_USERNAME)), usernameField);
    }

    private Component createTitlePanel() {
        titleField = createTextField();
        return createRow(createLabel(Messages.getMessage(Messages.NEWENTRYDIALOG_LABEL_TITLE)), titleField);
    }

    private Container createCategoryPanel() {
        final List<EntryCategory> categories = controller.getDataSource().getCategories();

        categoryBox = new JComboBox<>(categories.toArray(new EntryCategory[categories.size()]));
        categoryBox.setRenderer(new CategoryCellRenderer());
        return createRow(createLabel(Messages.getMessage(Messages.NEWENTRYDIALOG_LABEL_CATEGORY)), categoryBox);
    }

    private Container createButtons() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setSize(new Dimension(TEXT_WIDTH + LABEL_WIDTH, TEXT_HEIGHT + LABEL_HEIGHT));

        panel.add(createButton(Messages.getMessage(Messages.NEWENTRYDIALOG_BUTTON_SAVE), new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        LOGGER.debug("Clicked 'save new entry'");

                        try {

                            if (!verifyFields()) {
                                return;
                            }

                            String encryptedUsername = passwordHandler.encrypt(usernameField.getText());
                            String encryptedPassword = passwordHandler.encrypt(passwordField.getText());

                            fireOnEntryAdded(
                                new Entry((EntryCategory) categoryBox.getSelectedItem(), titleField.getText(),
                                    encryptedUsername, encryptedPassword));
                        } catch (PassSafeException ex) {
                            LOGGER.error("Unable to encrypt password", ex);
                            UiUtils.displayError(getOwner(), "TODO");
                        }

                        dispose();
                    }
                }));

        panel.add(createButton(Messages.getMessage(Messages.NEWENTRYDIALOG_BUTTON_CANCEL), new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        LOGGER.debug("Clicked 'cancel new entry'");
                        dispose();
                    }
                }));

        return panel;
    }

    private boolean verifyFields() {
        titleField.setBorder(origBorder);
        passwordField.setBorder(origBorder);
        passwordVerifyField.setBorder(origBorder);

        boolean isValid = true;

        if (Strings.isNullOrEmpty(titleField.getText())) {
            LOGGER.warn("No title entered!");
            UiUtils.markFieldAsInvalid(titleField);

            isValid = false;
        }

        if (!testPasswordsAreEqual()) {
            LOGGER.warn("Passwords are not equal!");

            UiUtils.markFieldAsInvalid(passwordField);
            UiUtils.markFieldAsInvalid(passwordVerifyField);

            isValid = false;
        }

        return isValid;
    }

    private boolean testPasswordsAreEqual() {
        String password = passwordField.getText();
        String verify = passwordVerifyField.getText();

        return StringUtils.equals(password, verify);
    }

    private void fireOnEntryAdded(final Entry entry) {
        for (OnEntryAddedListener listener : onEntryAddedListeners) {
            listener.onEntryAdded((EntryCategory) categoryBox.getSelectedItem(), entry);
        }
    }

    private JButton createButton(final String label, final ActionListener listener) {
        JButton button = new JButton(label);
        button.addActionListener(listener);

        return button;
    }

    private void reset() {
        List<EntryCategory> tmp = controller.getDataSource().getCategories();
        EntryCategory[] categories = tmp.toArray(new EntryCategory[tmp.size()]);

        categoryBox.setModel(new DefaultComboBoxModel<>(categories));
        titleField.setText(null);
        usernameField.setText(null);
        passwordField.setText(null);
        passwordVerifyField.setText(null);
    }
}
