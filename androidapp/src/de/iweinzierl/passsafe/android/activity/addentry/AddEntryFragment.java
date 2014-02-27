package de.iweinzierl.passsafe.android.activity.addentry;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

import android.app.Fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import de.iweinzierl.passsafe.android.PassSafeApplication;
import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.adapter.CategoryListAdapter;
import de.iweinzierl.passsafe.android.data.SQLiteRepository;
import de.iweinzierl.passsafe.android.exception.PassSafeSecurityException;
import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.secure.PasswordHandler;
import de.iweinzierl.passsafe.android.util.UiUtils;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntry;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntryCategory;
import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class AddEntryFragment extends Fragment {

    private static final Logger LOGGER = new Logger("AddEntryFragment");

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_addentry, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        initializeCategories();
        initializeSaveButton();
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
                        saveNewEntryAndExit();
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

    private void saveNewEntryAndExit() {
        Entry entry = createEntry();
        PassSafeApplication application = (PassSafeApplication) getActivity().getApplication();
        SQLiteRepository repository = application.getRepository();

        Entry savedEntry = repository.save(entry);

        if (savedEntry != null) {
            Toast.makeText(getActivity(), R.string.fragment_addentry_savedsuccessfully, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), R.string.fragment_addentry_savefailed, Toast.LENGTH_SHORT).show();
        }
    }

    private Entry createEntry() {
        //J-
        DatabaseEntry.Builder builder = new DatabaseEntry.Builder()
                .withCategory(getCategory())
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
