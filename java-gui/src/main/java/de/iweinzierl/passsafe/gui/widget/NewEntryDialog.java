package de.iweinzierl.passsafe.gui.widget;

import de.iweinzierl.passsafe.gui.data.Entry;
import de.iweinzierl.passsafe.gui.data.EntryCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    public static final int DEFAULT_WIDTH = 200;
    public static final int DEFAULT_HEIGHT = 125;

    private static final Logger LOGGER = LoggerFactory.getLogger(NewEntryDialog.class);

    private List<OnEntryAddedListener> onEntryAddedListeners;

    private JTextField titleField;
    private JTextField usernameField;
    private JTextField passwordField;


    public NewEntryDialog(JFrame parent, List<OnEntryAddedListener> onEntryAddedListeners) {
        super(parent, "Neuen Eintrag erstellen", true);
        this.onEntryAddedListeners = onEntryAddedListeners;
        initialize();
    }


    public void initialize() {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        contentPane.add(createCategoryPanel());
        contentPane.add(createTitlePanel());
        contentPane.add(createUsernamePanel());
        contentPane.add(createPasswordPanel());
        contentPane.add(createButtons());

        contentPane.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setContentPane(contentPane);

        setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setSize(new Dimension(TEXT_WIDTH + LABEL_WIDTH, TEXT_HEIGHT + LABEL_HEIGHT));

        passwordField = new JPasswordField(TEXT_COLUMNS);
        panel.add(createLabel("Passwort:"));
        panel.add(passwordField);

        return panel;
    }


    private Component createUsernamePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setSize(new Dimension(TEXT_WIDTH + LABEL_WIDTH, TEXT_HEIGHT + LABEL_HEIGHT));

        usernameField = createTextField();
        panel.add(createLabel("Benutzer:"));
        panel.add(usernameField);

        return panel;
    }


    private Component createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setSize(new Dimension(TEXT_WIDTH + LABEL_WIDTH, TEXT_HEIGHT + LABEL_HEIGHT));

        titleField = createTextField();
        panel.add(createLabel("Titel:"));
        panel.add(titleField);

        return panel;
    }


    private Container createCategoryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setSize(new Dimension(TEXT_WIDTH + LABEL_WIDTH, TEXT_HEIGHT + LABEL_HEIGHT));

        panel.add(createLabel("Kategory:"));
        panel.add(new JLabel("TODO"));

        return panel;
    }


    private Container createButtons() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setSize(new Dimension(TEXT_WIDTH + LABEL_WIDTH, TEXT_HEIGHT + LABEL_HEIGHT));

        panel.add(createButton("Speichern", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LOGGER.debug("Clicked 'save new entry'");
                for (OnEntryAddedListener listener : onEntryAddedListeners) {
                    listener.onEntryAdded(
                            EntryCategory.DEFAULT_CATEGORY,
                            new Entry(titleField.getText(), usernameField.getText(), passwordField.getToolTipText()));
                }
            }
        }));

        panel.add(createButton("Abbrechen", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LOGGER.debug("Clicked 'cancel new entry'");
                dispose();
            }
        }));

        return panel;
    }


    private JButton createButton(String label, ActionListener listener) {
        JButton button = new JButton(label);
        button.addActionListener(listener);

        return button;
    }
}
