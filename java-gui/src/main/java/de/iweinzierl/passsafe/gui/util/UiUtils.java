package de.iweinzierl.passsafe.gui.util;

import de.iweinzierl.passsafe.gui.resources.Messages;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;


public class UiUtils {

    public static final int ERRORDIALOG_WIDTH = 350;
    public static final int ERRORDIALOG_HEIGHT = 125;

    private static final String ERROR_TEMPLATE = "<html><body style='width: %spx'>%s</body></html>";

    public static void center(Component component) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - component.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - component.getHeight()) / 2);
        component.setLocation(x, y);
    }

    public static void displayError(Window owner, String text) {
        JLabel label = new JLabel(String.format(ERROR_TEMPLATE, 250, text));
        label.setPreferredSize(new Dimension(250, 100));

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(label);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JDialog dialog = new JDialog(owner);
        dialog.setContentPane(panel);
        dialog.setPreferredSize(new Dimension(ERRORDIALOG_WIDTH, ERRORDIALOG_HEIGHT));
        dialog.setMinimumSize(new Dimension(ERRORDIALOG_WIDTH, ERRORDIALOG_HEIGHT));
        dialog.setTitle(Messages.getMessage(Messages.ERROR_DIALOG_TITLE));

        center(dialog);
        dialog.show();
    }
}
