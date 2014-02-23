package de.iweinzierl.passsafe.android.activity.editentry;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

import android.app.Activity;
import android.app.Fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import de.iweinzierl.passsafe.android.PassSafeApplication;
import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.adapter.CategoryListAdapter;
import de.iweinzierl.passsafe.android.data.DatabaseEntry;
import de.iweinzierl.passsafe.android.data.DatabaseEntryCategory;
import de.iweinzierl.passsafe.android.data.SQLiteRepository;
import de.iweinzierl.passsafe.android.exception.PassSafeSecurityException;
import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.secure.PasswordHandler;
import de.iweinzierl.passsafe.android.util.UiUtils;
import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class EditEntryFragment extends Fragment {

    public interface Callback {
        void onUpdate(Entry entry);
    }

    private static final Logger LOGGER = new Logger("EditEntryFragment");

    private Callback callback;
    private Entry entry;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_editentry, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeCategories();
        initializeSaveButton();
    }

    @Override
    public void onStart() {
        super.onStart();

        Activity activity = getActivity();
        if (activity instanceof Callback) {
            callback = (Callback) activity;
        }
    }

    public void applyEntry(final Entry entry) {
        if (entry != null) {
            this.entry = entry;

            applyCategory(R.id.category, entry.getCategory());
            applyText(R.id.entrytitle, entry.getTitle());
            applyText(R.id.url, entry.getUrl());
            applyText(R.id.username, decrypt(entry.getUsername()));
            applyText(R.id.password, decrypt(entry.getPassword()));
            applyText(R.id.verifypassword, decrypt(entry.getPassword()));
            applyText(R.id.comment, entry.getComment());
        }
    }

    private void applyCategory(final int resId, final EntryCategory category) {
        Spinner categorySpinner = UiUtils.getSpinner(getView(), resId);
        if (categorySpinner != null) {
            CategoryListAdapter adapter = (CategoryListAdapter) categorySpinner.getAdapter();
            int pos = adapter.getPosition(category);

            categorySpinner.setSelection(pos);
        }
    }

    private void applyText(final int resId, final String text) {
        TextView textView = UiUtils.getTextView(getView(), resId);
        if (textView != null) {
            textView.setText(text);
        }
    }

    private void initializeCategories() {
        PassSafeApplication application = (PassSafeApplication) getActivity().getApplication();
        SQLiteRepository repository = application.getRepository();

        CategoryListAdapter adapter = new CategoryListAdapter(getActivity(), repository);

        Spinner spinner = UiUtils.getSpinner(getView(), R.id.category);
        spinner.setAdapter(adapter);
    }

    private void initializeSaveButton() {
        Button save = UiUtils.getButton(getView(), R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (validate()) {
                        updateEntryAndExit();
                    }
                }
            });
    }

    protected boolean validate() {
        boolean titleIsValid = validateTitle();
        boolean passwordIsValid = validatePassword();
        boolean passwordVerificationIsValid = validatePasswordVerification();

        return titleIsValid && passwordIsValid && passwordVerificationIsValid;
    }

    private boolean validateTitle() {
        String password = getEntryTitle();

        if (Strings.isNullOrEmpty(password)) {
            addErrorMessageToEditText(R.id.entrytitle, R.string.fragment_addentry_error_titlemissing);
            return false;
        }

        return true;
    }

    private boolean validatePassword() {
        String password = getPassword();

        if (Strings.isNullOrEmpty(password)) {
            addErrorMessageToEditText(R.id.password, R.string.fragment_addentry_error_passwordmissing);
            return false;
        }

        return true;
    }

    private boolean validatePasswordVerification() {
        String password = getPassword();
        String passwordVerification = getPasswordVerification();

        if (Strings.isNullOrEmpty(passwordVerification)) {
            addErrorMessageToEditText(R.id.verifypassword,
                R.string.fragment_addentry_error_passwordverificationmissing);
            return false;
        }

        if (!Strings.isNullOrEmpty(password) && !StringUtils.equals(password, passwordVerification)) {
            addErrorMessageToEditText(R.id.verifypassword, R.string.fragment_addentry_error_passwordverificationfailed);
            return false;
        }

        return true;
    }

    private void addErrorMessageToEditText(final int resId, final int messageId) {
        EditText textField = UiUtils.getEditText(getView(), resId);
        if (textField != null) {
            textField.setError(getString(messageId));
        }
    }

    private void updateEntryAndExit() {
        Entry entry = createEntry();

        if (callback != null) {
            callback.onUpdate(entry);
        }
    }

    private Entry createEntry() {
        //J-
        DatabaseEntry.Builder builder = new DatabaseEntry.Builder()
                .withCategory(getCategory())
                .withId(((DatabaseEntry)this.entry).getId())
                .withTitle(getEntryTitle())
                .withUrl(getUrl())
                .withUsername(encrypt(getUsername()))
                .withPassword(encrypt(getPassword()))
                .withComment(getComment());
        //J+

        return builder.build();
    }

    private DatabaseEntryCategory getCategory() {
        Spinner spinner = UiUtils.getSpinner(getView(), R.id.category);
        Object selectedItem = spinner.getSelectedItem();

        if (selectedItem instanceof EntryCategory) {
            return (DatabaseEntryCategory) selectedItem;
        }

        return null;
    }

    private String getEntryTitle() {
        return getValue(R.id.entrytitle);
    }

    private String getUrl() {
        return getValue(R.id.url);
    }

    private String getUsername() {
        return getValue(R.id.username);
    }

    private String getPassword() {
        return getValue(R.id.password);
    }

    private String getPasswordVerification() {
        return getValue(R.id.verifypassword);
    }

    private String getComment() {
        return getValue(R.id.comment);
    }

    private String getValue(final int resId) {
        EditText text = UiUtils.getEditText(getView(), resId);
        if (text != null) {
            return text.getText().toString();
        }

        return null;
    }

    private String decrypt(final String encrypted) {
        PassSafeApplication application = (PassSafeApplication) getActivity().getApplication();
        PasswordHandler passwordHandler = application.getPasswordHandler();

        try {
            return passwordHandler.decrypt(encrypted);
        } catch (PassSafeSecurityException e) {
            LOGGER.error("Decryption of value (length = " + encrypted.length() + ") failed", e);
            return null;
        }
    }

    private String encrypt(final String plainValue) {
        PassSafeApplication application = (PassSafeApplication) getActivity().getApplication();
        PasswordHandler passwordHandler = application.getPasswordHandler();

        try {
            return passwordHandler.encrypt(plainValue);
        } catch (PassSafeSecurityException e) {
            LOGGER.error("Encryption of value (length = " + plainValue.length() + ") failed", e);
            return null;
        }
    }
}
