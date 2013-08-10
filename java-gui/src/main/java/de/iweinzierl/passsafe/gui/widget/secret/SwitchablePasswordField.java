package de.iweinzierl.passsafe.gui.widget.secret;

import com.google.common.base.Strings;
import de.iweinzierl.passsafe.gui.exception.PassSafeSecurityException;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class SwitchablePasswordField extends JPanel {

    private boolean visible;

    final private JTextField visibleField;
    final private JPasswordField invisibleField;

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

    public String getPassword() throws PassSafeSecurityException {
        return visible ? visibleField.getText() : invisibleField.getText();
    }

    public void setPassword(String password) throws PassSafeSecurityException {
        if (!Strings.isNullOrEmpty(password)) {
            this.visibleField.setText(password);
            this.invisibleField.setText(password);
        }
        else {
            this.visibleField.setText("");
            this.invisibleField.setText("");
        }
    }

    public void hidePassword() {
        invisibleField.setText(visibleField.getText());
        remove(visibleField);
        add(invisibleField);
        visible = false;
    }

    public void showPassword() {
        visibleField.setText(invisibleField.getText());
        remove(invisibleField);
        add(visibleField);
        visible = true;
    }

    public void setEditable(boolean editable) {
        visibleField.setEditable(editable);
        invisibleField.setEditable(editable);
    }

    @Override
    public void setComponentPopupMenu(JPopupMenu popup) {
        visibleField.setComponentPopupMenu(popup);
        invisibleField.setComponentPopupMenu(popup);
    }
}
