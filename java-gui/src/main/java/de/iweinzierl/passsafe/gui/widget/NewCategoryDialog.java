package de.iweinzierl.passsafe.gui.widget;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;
import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.util.UiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewCategoryDialog extends JDialog {

    public interface OnCategoryAddedListener {
        void onCategoryAdded(EntryCategory category);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NewCategoryDialog.class);

    public static final int DEFAULT_WIDTH = 300;
    public static final int DEFAULT_HEIGHT = 125;

    public static final int LABEL_WIDTH = 50;
    public static final int LABEL_HEIGHT = 25;

    public static final int TEXTFIELD_WIDTH = 200;
    public static final int TEXTFIELD_HEIGHT = 25;

    public static final int BUTTON_WIDTH = 125;
    public static final int BUTTON_HEIGHT = 25;

    private final ApplicationController controller;

    private final JTextField titleField;

    public NewCategoryDialog(Frame owner, ApplicationController controller) {
        super(owner);
        this.controller = controller;
        this.titleField = (JTextField) WidgetFactory.createComponent(JTextField.class, TEXTFIELD_WIDTH,
                TEXTFIELD_HEIGHT);

        initialize();
    }

    private void initialize() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEADING));

        panel.add(createTitlePanel());
        panel.add(createButtonPanel());

        setContentPane(panel);
        setTitle(Messages.getMessage(Messages.NEWCATEGORYDIALOG_TITLE));

        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setResizable(false);

        UiUtils.center(this);
    }

    private JPanel createTitlePanel() {
        return WidgetFactory.createInputPanel(
                WidgetFactory.createLabel(Messages.getMessage(Messages.NEWCATEGORYDIALOG_LABEL_TITLE), LABEL_WIDTH,
                        LABEL_HEIGHT), titleField);
    }

    private EntryCategory buildCategory() {
        if (!Strings.isNullOrEmpty(titleField.getText())) {
            return new EntryCategory(titleField.getText());
        }

        LOGGER.debug("User did not enter a title: '{}'", titleField.getText());

        return null;
    }

    private JPanel createButtonPanel() {
        ActionListener okListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EntryCategory entryCategory = buildCategory();

                if (entryCategory != null) {
                    controller.onCategoryAdded(entryCategory);
                    dispose();
                }
            }
        };

        ActionListener cancelListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };

        return WidgetFactory.createButtonPanel(
                WidgetFactory.createButton(Messages.getMessage(Messages.NEWCATEGORYDIALOG_BUTTON_SAVE), BUTTON_WIDTH,
                        BUTTON_HEIGHT, okListener),
                WidgetFactory.createButton(Messages.getMessage(Messages.NEWCATEGORYDIALOG_BUTTON_CANCEL), BUTTON_WIDTH,
                        BUTTON_HEIGHT, cancelListener));
    }
}
