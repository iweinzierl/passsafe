package de.iweinzierl.passsafe.android.activity.addcategory;

import com.google.common.base.Strings;

import android.app.Fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.iweinzierl.passsafe.android.PassSafeApplication;
import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.data.SQLiteRepository;
import de.iweinzierl.passsafe.android.util.UiUtils;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntryCategory;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class AddCategoryFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_addcategory, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        initializeSaveButton();
    }

    private void initializeSaveButton() {
        Button save = UiUtils.getButton(getView(), R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (validate()) {
                        saveNewCategoryAndExit();
                    }
                }
            });
    }

    protected boolean validate() {
        return validateTitle();
    }

    private boolean validateTitle() {
        String title = getCategoryTitle();

        if (Strings.isNullOrEmpty(title)) {
            addErrorMessageToEditText(R.id.categorytitle, R.string.fragment_addcategory_error_titlemissing);
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

    private void saveNewCategoryAndExit() {
        EntryCategory category = createCategory();

        PassSafeApplication application = (PassSafeApplication) getActivity().getApplication();
        SQLiteRepository repository = application.getRepository();

        EntryCategory savedCategory = repository.save(category);

        if (savedCategory != null) {
            Toast.makeText(getActivity(), R.string.fragment_addcategory_savedsuccessfully, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), R.string.fragment_addcategory_savefailed, Toast.LENGTH_SHORT).show();
        }
    }

    private EntryCategory createCategory() {
        //J-
        return new DatabaseEntryCategory.Builder()
                .withTitle(getCategoryTitle())
                .build();
        //J+
    }

    private String getCategoryTitle() {
        return getValue(R.id.categorytitle);
    }

    private String getValue(final int resId) {
        EditText text = UiUtils.getEditText(getView(), resId);
        if (text != null) {
            return text.getText().toString();
        }

        return null;
    }
}
