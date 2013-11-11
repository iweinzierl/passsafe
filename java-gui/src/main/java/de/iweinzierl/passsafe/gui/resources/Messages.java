package de.iweinzierl.passsafe.gui.resources;

import java.util.ResourceBundle;

public class Messages {

    public static final String MESSAGE_PROPERTIES = "Messages";

    public static final ResourceBundle MESSAGE_BUNDLE = ResourceBundle.getBundle(MESSAGE_PROPERTIES);

    public static final String APP_TITLE = "app.title";

    public static final String ERROR_DIALOG_TITLE = "errordialog.title";

    public static final String START_DIALOG_TITLE = "startup.title";
    public static final String START_DIALOG_LABEL = "startup.label";
    public static final String START_DIALOG_PASSWORD_LABEL = "startup.password.label";
    public static final String START_DIALOG_OK_BUTTON = "startup.button.ok";
    public static final String START_DIALOG_CANCEL_BUTTON = "startup.button.cancel";

    public static final String ENTRYLIST_CATEGORIES = "entrylist.popup.categories";
    public static final String ENTRYLIST_ROOTNODE = "entrylist.rootnode";
    public static final String ENTRYLIST_MENU_REMOVEITEM = "entrylist.menu.removeitem";

    public static final String ENTRYTABLE_COLUMN_TITLE = "entrytable.column.title";
    public static final String ENTRYTABLE_COLUMN_USERNAME = "entrytable.column.username";
    public static final String ENTRYTABLE_COLUMN_PASSWORD = "entrytable.column.password";

    public static final String BUTTONBAR_NEWENTRY = "buttonbar.newentry";
    public static final String BUTTONBAR_REMOVEENTRY = "buttonbar.removeentry";
    public static final String BUTTONBAR_NEWCATEGORY = "buttonbar.newcategory";
    public static final String BUTTONBAR_SYNC = "buttonbar.sync";

    public static final String NEWENTRYDIALOG_TITLE = "newentrydialog.title";
    public static final String NEWENTRYDIALOG_LABEL_PASSWORD = "newentrydialog.label.password";
    public static final String NEWENTRYDIALOG_LABEL_VERIFYPASSWORD = "newentrydialog.label.verifypassword";
    public static final String NEWENTRYDIALOG_LABEL_USERNAME = "newentrydialog.label.username";
    public static final String NEWENTRYDIALOG_LABEL_TITLE = "newentrydialog.label.title";
    public static final String NEWENTRYDIALOG_LABEL_URL = "newentrydialog.label.url";
    public static final String NEWENTRYDIALOG_LABEL_COMMENTS = "newentrydialog.label.comments";
    public static final String NEWENTRYDIALOG_LABEL_CATEGORY = "newentrydialog.label.category";
    public static final String NEWENTRYDIALOG_BUTTON_SAVE = "newentrydialog.button.save";
    public static final String NEWENTRYDIALOG_BUTTON_CANCEL = "newentrydialog.button.cancel";

    public static final String NEWCATEGORYDIALOG_TITLE = "newcategorydialog.title";
    public static final String NEWCATEGORYDIALOG_LABEL_TITLE = "newcategorydialog.label.title";
    public static final String NEWCATEGORYDIALOG_BUTTON_SAVE = "newcategorydialog.button.save";
    public static final String NEWCATEGORYDIALOG_BUTTON_CANCEL = "newcategorydialog.button.cancel";

    public static final String ENTRYVIEW_MENU_COPYTOCLIPBOARD = "entryview.menu.copytoclipboard";
    public static final String ENTRYVIEW_LABEL_TITLE = "entryview.label.title";
    public static final String ENTRYVIEW_LABEL_USERNAME = "entryview.label.username";
    public static final String ENTRYVIEW_LABEL_PASSWORD = "entryview.label.password";
    public static final String ENTRYVIEW_BUTTON_EDIT = "entryview.button.edit";
    public static final String ENTRYVIEW_BUTTON_SAVE = "entryview.button.save";
    public static final String ENTRYVIEW_BUTTON_CANCEL = "entryview.button.cancel";
    public static final String ENTRYVIEW_BUTTON_VISIBLE = "entryview.button.visible";
    public static final String ENTRYVIEW_BUTTON_INVISIBLE = "entryview.button.invisible";
    public static final String ENTRYVIEW_MENU_TITLE = "entryview.menu.title";

    private Messages() { }

    public static String getMessage(final String key) {
        return MESSAGE_BUNDLE.getString(key);
    }
}
