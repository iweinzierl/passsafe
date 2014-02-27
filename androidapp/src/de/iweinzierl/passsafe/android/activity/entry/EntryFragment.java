package de.iweinzierl.passsafe.android.activity.entry;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

import android.app.Activity;
import android.app.Fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import de.iweinzierl.passsafe.android.PassSafeApplication;
import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.exception.PassSafeSecurityException;
import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.util.ColorUtils;
import de.iweinzierl.passsafe.android.util.UiUtils;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntryCategory;
import de.iweinzierl.passsafe.shared.domain.Entry;

public class EntryFragment extends Fragment {

    public interface Callback {

        void onOpenUrl(String url);

        void onRemoveEntry(Entry entry);

        void onEditEntry(Entry entry);
    }

    private static final Logger LOGGER = new Logger("EntryFragment");

    private static final String FAILURE_TEXT = "- failure -";
    private static final String DECRYPTED_VALUE_PLACEHOLDER = "**********";

    private Callback callback;

    private Entry entry;

    private boolean displayDecryptedUsername = false;
    private boolean displayDecryptedPassword = false;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_entry, container, false);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.entry, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {

            case R.id.remove_entry :

                if (callback != null) {
                    callback.onRemoveEntry(entry);
                }

                return true;

            case R.id.edit_entry :

                if (callback != null) {
                    callback.onEditEntry(entry);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
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
        this.entry = entry;

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
            initOpenUrlButton(parent, entry);
            initCopyUsernameButton(parent, entry);
            initCopyPasswordButton(parent, entry);
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

            button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        Toast.makeText(getActivity(), R.string.fragment_entry_hint_toggleusername, Toast.LENGTH_SHORT)
                             .show();
                        return true;
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

            button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        Toast.makeText(getActivity(), R.string.fragment_entry_hint_togglepassword, Toast.LENGTH_SHORT)
                             .show();
                        return true;
                    }
                });
        }
    }

    private void initOpenUrlButton(final View parent, final Entry entry) {
        View button = UiUtils.getButtonOrImageButton(parent, R.id.open_url);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (callback != null) {
                            callback.onOpenUrl(entry.getUrl());
                        }
                    }
                });

            button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        Toast.makeText(getActivity(), R.string.fragment_entry_hint_openurl, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
        }
    }

    private void initCopyUsernameButton(final View parent, final Entry entry) {
        View button = UiUtils.getButtonOrImageButton(parent, R.id.copy_username);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        String decryptedUsername = decrypt(entry, entry.getUsername());

                        if (!StringUtils.equals(FAILURE_TEXT, decryptedUsername)) {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(
                                    Context.CLIPBOARD_SERVICE);
                            clipboard.setPrimaryClip(ClipData.newPlainText("username", decryptedUsername));

                            Toast.makeText(getActivity(), R.string.fragment_entry_toast_copiedusername,
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        Toast.makeText(getActivity(), R.string.fragment_entry_hint_copyusername, Toast.LENGTH_SHORT)
                             .show();
                        return true;
                    }
                });
        }
    }

    private void initCopyPasswordButton(final View parent, final Entry entry) {
        View button = UiUtils.getButtonOrImageButton(parent, R.id.copy_password);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        String decryptedPassword = decrypt(entry, entry.getPassword());

                        if (!StringUtils.equals(FAILURE_TEXT, decryptedPassword)) {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(
                                    Context.CLIPBOARD_SERVICE);
                            clipboard.setPrimaryClip(ClipData.newPlainText("password", decryptedPassword));

                            Toast.makeText(getActivity(), R.string.fragment_entry_toast_copiedpassword,
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        Toast.makeText(getActivity(), R.string.fragment_entry_hint_copypassword, Toast.LENGTH_SHORT)
                             .show();
                        return true;
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
            return FAILURE_TEXT;
        }
    }
}
