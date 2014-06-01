package de.iweinzierl.passsafe.gui.widget.secret;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import com.google.common.base.Strings;

public class SwitchablePasswordField extends JPanel {

    private boolean visible;

    private final JTextField visibleField;
    private final JPasswordField invisibleField;

    public SwitchablePasswordField() {
        super();

        this.visibleField = new JTextField();
        this.invisibleField = new JPasswordField();
        this.visible = false;

        initialize();
    }

    private void initialize() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(invisibleField);
    }

    public String getPassword() {
        return visible ? visibleField.getText() : invisibleField.getText();
    }

    public void setPassword(final String password) {
        if (!Strings.isNullOrEmpty(password)) {
            this.visibleField.setText(password);
            this.invisibleField.setText(password);
        } else {
            this.visibleField.setText("");
            this.invisibleField.setText("");
        }
    }

    public void hidePassword() {
        invisibleField.setText(visibleField.getText());
        remove(visibleField);
        add(invisibleField);
        visible = false;
        updateUI();
    }

    public void showPassword() {
        visibleField.setText(invisibleField.getText());
        remove(invisibleField);
        add(visibleField);
        visible = true;
        updateUI();
    }

    public void setEditable(final boolean editable) {
        visibleField.setEditable(editable);
        invisibleField.setEditable(editable);
    }

    @Override
    public void setComponentPopupMenu(final JPopupMenu popup) {
        visibleField.setComponentPopupMenu(popup);
        invisibleField.setComponentPopupMenu(popup);
    }
}
