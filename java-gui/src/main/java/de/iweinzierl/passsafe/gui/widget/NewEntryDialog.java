package de.iweinzierl.passsafe.gui.widget;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import de.iweinzierl.passsafe.gui.data.Entry;
import de.iweinzierl.passsafe.gui.data.EntryCategory;
import de.iweinzierl.passsafe.gui.exception.PassSafeException;
import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.secure.PasswordHandler;
import de.iweinzierl.passsafe.gui.util.UiUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BoxLayout;
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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


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

    private PasswordHandler passwordHandler;

    private final List<EntryCategory> categories;

    private List<OnEntryAddedListener> onEntryAddedListeners;

    private JComboBox<EntryCategory> categoryBox;
    private JTextField titleField;
    private JTextField usernameField;
    private JTextField passwordField;
    private JTextField passwordVerifyField;

    private Border origBorder;


    public NewEntryDialog(JFrame parent, List<EntryCategory> categories, PasswordHandler passwordHandler) {
        super(parent, Messages.getMessage(Messages.NEWENTRYDIALOG_TITLE), true);
        this.categories = categories;
        this.passwordHandler = passwordHandler;
        this.onEntryAddedListeners = new ArrayList<>();
        initialize();
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


    public void addOnEntryAddedListeners(OnEntryAddedListener onEntryAddedListener) {
        if (onEntryAddedListener != null) {
            onEntryAddedListeners.add(onEntryAddedListener);
        }
    }

    private JPanel createRow(JLabel label, JComponent inputField) {
        JPanel row = new JPanel();
        row.setLayout(new FlowLayout(FlowLayout.LEFT));

        label.setPreferredSize(new Dimension(75, 25));
        inputField.setPreferredSize(new Dimension(150, 25));

        row.add(label);
        row.add(inputField);

        return row;
    }


    private JLabel createLabel(String text) {
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
            public void actionPerformed(ActionEvent e) {
                LOGGER.debug("Clicked 'save new entry'");

                try {

                    if (!verifyFields()) {
                        return;
                    }

                    String encryptedUsername = passwordHandler.encrypt(usernameField.getText());
                    String encryptedPassword = passwordHandler.encrypt(passwordField.getText());

                    fireOnEntryAdded(new Entry((EntryCategory) categoryBox.getSelectedItem(), titleField.getText(),
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
            public void actionPerformed(ActionEvent e) {
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

    private void fireOnEntryAdded(Entry entry) {
        for (OnEntryAddedListener listener : onEntryAddedListeners) {
            listener.onEntryAdded((EntryCategory) categoryBox.getSelectedItem(), entry);
        }
    }


    private JButton createButton(String label, ActionListener listener) {
        JButton button = new JButton(label);
        button.addActionListener(listener);

        return button;
    }
}
