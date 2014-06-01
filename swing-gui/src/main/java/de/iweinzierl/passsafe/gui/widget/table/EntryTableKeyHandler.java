package de.iweinzierl.passsafe.gui.widget.table;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.exception.PassSafeSecurityException;
import de.iweinzierl.passsafe.gui.util.UiUtils;
import de.iweinzierl.passsafe.shared.domain.Entry;

public class EntryTableKeyHandler implements KeyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntryTableKeyHandler.class);

    private final EntryTable entryTable;
    private final ApplicationController controller;

    public EntryTableKeyHandler(final EntryTable entryTable, final ApplicationController controller) {
        this.entryTable = entryTable;
        this.controller = controller;
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        int modifiers = e.getModifiers();
        int keyCode = e.getKeyCode();

        if (modifiers == KeyEvent.CTRL_DOWN_MASK || modifiers == KeyEvent.CTRL_MASK) {
            switch (keyCode) {

                case KeyEvent.VK_U :
                    copyUsernameToClipboard();
                    break;

                case KeyEvent.VK_P :
                    copyPasswordToClipboard();
                    break;
            }
        }
    }

    @Override
    public void keyTyped(final KeyEvent e) { }

    @Override
    public void keyReleased(final KeyEvent e) { }

    private Entry getSelectedEntry() {
        int currentRow = entryTable.getSelectedRow();
        return entryTable.getEntryTableModel().getEntry(currentRow);
    }

    private void copyUsernameToClipboard() {
        LOGGER.debug("Copy username to clipboard");

        Entry entry = getSelectedEntry();
        copyToClipboard(entry.getUsername());
    }

    private void copyPasswordToClipboard() {
        LOGGER.debug("Copy password to clipboard");

        Entry entry = getSelectedEntry();
        copyToClipboard(entry.getPassword());
    }

    private void copyToClipboard(final String value) {
        try {
            String decrypted = controller.getPasswordHandler().decrypt(value);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(decrypted), null);
        } catch (PassSafeSecurityException e) {
            LOGGER.error("Unable to decrypt password!", e);
            UiUtils.displayError(UiUtils.getOwner(entryTable), "Unable to decrypt password! Cannot copy to clipboard.");
        }
    }
}
