package de.iweinzierl.passsafe.android.activity.entry;

import com.google.common.base.Strings;

import android.app.Fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import de.iweinzierl.passsafe.android.PassSafeApplication;
import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.data.DatabaseEntryCategory;
import de.iweinzierl.passsafe.android.exception.PassSafeSecurityException;
import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.util.ColorUtils;
import de.iweinzierl.passsafe.android.util.UiUtils;
import de.iweinzierl.passsafe.shared.domain.Entry;

public class EntryFragment extends Fragment {

    private static final Logger LOGGER = new Logger("EntryFragment");

    private static final String DECRYPTED_VALUE_PLACEHOLDER = "**********";

    private boolean displayDecryptedUsername = false;
    private boolean displayDecryptedPassword = false;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedState) {
        return inflater.inflate(R.layout.fragment_entry, container, false);
    }

    public void applyEntry(final Entry entry) {
        View parent = getView();

        if (entry != null) {
            applyHeaderColor(parent, entry);
            applyTitle(parent, entry);
            applyCategory(parent, entry);
            applyUrl(parent, entry);
            applyUsername(parent, entry);
            applyPassword(parent, entry);
            applyComment(parent, entry);

            initToggleUsernameButton(parent, entry);
            initTogglePasswordButton(parent, entry);
        }
    }

    private void initToggleUsernameButton(final View parent, final Entry entry) {
        View button = UiUtils.getButtonOrImageButton(parent, R.id.toggle_username);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        toggleUsername(entry);
                    }
                });
        }
    }

    private void initTogglePasswordButton(final View parent, final Entry entry) {
        View button = UiUtils.getButtonOrImageButton(parent, R.id.toggle_password);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        togglePassword(entry);
                    }
                });
        }
    }

    private void applyHeaderColor(final View parent, final Entry entry) {
        View view = parent.findViewById(R.id.header);
        if (view != null && entry.getCategory() instanceof DatabaseEntryCategory) {
            view.setBackgroundColor(ColorUtils.colorById(((DatabaseEntryCategory) entry.getCategory()).getId()));
        }
    }

    private void toggleUsername(final Entry entry) {
        displayDecryptedUsername = !displayDecryptedUsername;
        applyUsername(getView(), entry);
    }

    private void togglePassword(final Entry entry) {
        displayDecryptedPassword = !displayDecryptedPassword;
        applyPassword(getView(), entry);
    }

    private void applyTitle(final View parent, final Entry entry) {
        applyText(parent, R.id.title, entry.getTitle());
    }

    private void applyCategory(final View parent, final Entry entry) {
        if (entry.getCategory() != null) {
            applyText(parent, R.id.category, entry.getCategory().getTitle());
        }
    }

    private void applyUrl(final View parent, final Entry entry) {
        applyText(parent, R.id.url, entry.getUrl());
    }

    private void applyUsername(final View parent, final Entry entry) {
        if (displayDecryptedUsername) {
            applyText(parent, R.id.username, decrypt(entry, entry.getUsername()));
        } else {
            applyText(parent, R.id.username, DECRYPTED_VALUE_PLACEHOLDER);
        }
    }

    private void applyPassword(final View parent, final Entry entry) {
        if (displayDecryptedPassword) {
            applyText(parent, R.id.password, decrypt(entry, entry.getPassword()));
        } else {
            applyText(parent, R.id.password, DECRYPTED_VALUE_PLACEHOLDER);
        }
    }

    private void applyComment(final View parent, final Entry entry) {
        applyText(parent, R.id.comment, entry.getComment());
    }

    private void applyText(final View parent, final int resId, final String value) {
        TextView view = UiUtils.getTextView(parent, resId);
        if (view != null && !Strings.isNullOrEmpty(value)) {
            view.setText(value);
        }
    }

    private String decrypt(final Entry entry, final String encryptedValue) {
        try {
            PassSafeApplication application = (PassSafeApplication) getActivity().getApplication();
            return application.getPasswordHandler().decrypt(encryptedValue);
        } catch (PassSafeSecurityException e) {
            LOGGER.error("Decryption of value failed entry:" + entry.getTitle());
            return "- failure -";
        }
    }
}
