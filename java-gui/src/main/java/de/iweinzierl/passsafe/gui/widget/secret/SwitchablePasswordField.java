package de.iweinzierl.passsafe.gui.widget.secret;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class SwitchablePasswordField extends JPanel {

    private String password;

    private boolean visible;

    private JTextField visibleField;
    private JPasswordField invisibleField;

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

    public String getOrigPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        this.visibleField.setText(password);
        this.invisibleField.setText(password);
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
