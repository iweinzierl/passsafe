package de.iweinzierl.passsafe.android.activity.addentry;

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
import de.iweinzierl.passsafe.android.util.UiUtils;

public class AddEntryFragment extends Fragment {

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
                        // TODO save
                    } else {
                        Toast.makeText(getActivity(), "Nicht notwendigen Felder sind ausgefüllt", Toast.LENGTH_SHORT)
                             .show();
                    }
                }
            });
    }

    protected boolean validate() {
        String title = getEntryTitle();
        String password = getPassword();
        String verifyPassword = getPasswordVerification();

        // TODO mark invalid fields in ui

        return !Strings.isNullOrEmpty(title) && !Strings.isNullOrEmpty(password)
                && !Strings.isNullOrEmpty(verifyPassword) && password.equals(verifyPassword);
    }

    private String getEntryTitle() {
        return getValue(R.id.entrytitle);
    }

    private String getPassword() {
        return getValue(R.id.password);
    }

    private String getPasswordVerification() {
        return getValue(R.id.verifypassword);
    }

    private String getValue(final int resId) {
        EditText text = UiUtils.getEditText(getView(), resId);
        if (text != null) {
            return text.getText().toString();
        }

        return null;
    }
}
