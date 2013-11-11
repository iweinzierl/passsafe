package de.iweinzierl.passsafe.gui.widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.repackaged.com.google.common.base.Strings;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.exception.PassSafeException;
import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.secure.PasswordHandler;
import de.iweinzierl.passsafe.gui.util.UiUtils;
import de.iweinzierl.passsafe.gui.widget.secret.PasswordInputPanel;
import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class NewEntryDialog extends JDialog {

    public interface OnEntryAddedListener {
        void onEntryAdded(EntryCategory category, Entry entry);
    }

    public static final int TEXT_COLUMNS = 30;
    public static final int LABEL_WIDTH = 100;
    public static final int LABEL_HEIGHT = 25;
    public static final int TEXT_WIDTH = 100;
    public static final int TEXT_HEIGHT = 25;

    public static final int DEFAULT_WIDTH = 550;
    public static final int DEFAULT_HEIGHT = 325;

    private static final Logger LOGGER = LoggerFactory.getLogger(NewEntryDialog.class);

    private static NewEntryDialog INSTANCE;

    private final ApplicationController controller;

    private final PasswordHandler passwordHandler;

    private List<OnEntryAddedListener> onEntryAddedListeners;

    private JComboBox<EntryCategory> categoryBox;
    private JTextField titleField;
    private JTextField usernameField;
    private JTextField urlField;
    private JTextArea commentsField;

    private PasswordInputPanel passwordInputPanel;

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

        GridBagLayout layout = new GridBagLayout();
        contentPane.setLayout(layout);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1d;
        constraints.weighty = 1d;

        createCategoryPanel(contentPane, layout, constraints);
        createTitlePanel(contentPane, layout, constraints);
        createUrlPanel(contentPane, layout, constraints);
        createUsernamePanel(contentPane, layout, constraints);
        createPasswordPanel(contentPane, layout, constraints);
        createCommentsPanel(contentPane, layout, constraints);
        createButtons(contentPane, layout, constraints);
        constraints.gridwidth = GridBagConstraints.REMAINDER;

        contentPane.setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        contentPane.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setContentPane(contentPane);

        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        UiUtils.center(this);

        this.origBorder = titleField.getBorder();
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

    private void createCommentsPanel(final JPanel contentPane, final GridBagLayout layout,
            final GridBagConstraints constraints) {

        JLabel label = createLabel(Messages.getMessage(Messages.NEWENTRYDIALOG_LABEL_COMMENTS));
        constraints.gridwidth = 1;
        constraints.weightx = 1d;
        layout.setConstraints(label, constraints);
        contentPane.add(label);

        commentsField = new JTextArea(4, 50);
        commentsField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 5d;
        layout.setConstraints(commentsField, constraints);
        contentPane.add(commentsField);
    }

    private void createPasswordPanel(final JPanel contentPane, final GridBagLayout layout,
            final GridBagConstraints constraints) {

        JLabel label = createLabel(Messages.getMessage(Messages.NEWENTRYDIALOG_LABEL_PASSWORD));
        constraints.gridwidth = 1;
        constraints.weightx = 1d;
        layout.setConstraints(label, constraints);
        contentPane.add(label);

        passwordInputPanel = new PasswordInputPanel.Builder().build();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 5d;
        layout.setConstraints(passwordInputPanel, constraints);
        contentPane.add(passwordInputPanel);
    }

    private void createUsernamePanel(final JPanel contentPane, final GridBagLayout layout,
            final GridBagConstraints constraints) {

        JLabel label = createLabel(Messages.getMessage(Messages.NEWENTRYDIALOG_LABEL_USERNAME));
        constraints.gridwidth = 1;
        layout.setConstraints(label, constraints);
        contentPane.add(label);

        usernameField = createTextField();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(usernameField, constraints);
        contentPane.add(usernameField);
    }

    private void createTitlePanel(final JPanel contentPane, final GridBagLayout layout,
            final GridBagConstraints constraints) {

        JLabel label = createLabel(Messages.getMessage(Messages.NEWENTRYDIALOG_LABEL_TITLE));
        constraints.gridwidth = 1;
        layout.setConstraints(label, constraints);
        contentPane.add(label);

        titleField = createTextField();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(titleField, constraints);
        contentPane.add(titleField);
    }

    private void createUrlPanel(final JPanel contentPane, final GridBagLayout layout,
            final GridBagConstraints constraints) {

        JLabel label = createLabel(Messages.getMessage(Messages.NEWENTRYDIALOG_LABEL_URL));
        constraints.gridwidth = 1;
        layout.setConstraints(label, constraints);
        contentPane.add(label);

        urlField = createTextField();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(urlField, constraints);
        contentPane.add(urlField);
    }

    private void createCategoryPanel(final JPanel contentPane, final GridBagLayout layout,
            final GridBagConstraints constraints) {

        JLabel label = createLabel(Messages.getMessage(Messages.NEWENTRYDIALOG_LABEL_CATEGORY));
        constraints.gridwidth = 1;
        layout.setConstraints(label, constraints);
        contentPane.add(label);

        final List<EntryCategory> categories = controller.getDataSource().getCategories();
        categoryBox = new JComboBox<>(categories.toArray(new EntryCategory[categories.size()]));
        categoryBox.setRenderer(new CategoryCellRenderer());

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(categoryBox, constraints);
        contentPane.add(categoryBox);
    }

    private void createButtons(final JPanel contentPane, final GridBagLayout layout,
            final GridBagConstraints constraints) {
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
                            String encryptedPassword = passwordHandler.encrypt(passwordInputPanel.getPassword());

                            fireOnEntryAdded(
                                new Entry((EntryCategory) categoryBox.getSelectedItem(), titleField.getText(),
                                    urlField.getText(), encryptedUsername, encryptedPassword, commentsField.getText()));
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

        constraints.weightx = 1d;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(panel, constraints);
        contentPane.add(panel);
    }

    private boolean verifyFields() {
        titleField.setBorder(origBorder);
        passwordInputPanel.clearBorder();

        boolean isValid = true;

        if (Strings.isNullOrEmpty(titleField.getText())) {
            LOGGER.warn("No title entered!");
            UiUtils.markFieldAsInvalid(titleField);

            isValid = false;
        }

        if (!passwordInputPanel.verifyFields()) {
            LOGGER.warn("Passwords are not equal!");
            isValid = false;
        }

        return isValid;
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
        urlField.setText(null);
        usernameField.setText(null);
        passwordInputPanel.reset();
        commentsField.setText(null);
    }
}
