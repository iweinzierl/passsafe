package de.iweinzierl.passsafe.android.activity.entry;

import com.google.common.base.Strings;

import android.app.Activity;

import android.content.Intent;

import android.net.Uri;

import android.os.Bundle;

import de.iweinzierl.passsafe.android.PassSafeApplication;
import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.data.SQLiteRepository;
import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.shared.domain.Entry;

public class EntryActivity extends Activity implements EntryFragment.Callback {

    private static final Logger LOGGER = new Logger("EntryActivity");

    private EntryFragment entryFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        entryFragment = new EntryFragment();
        getFragmentManager().beginTransaction().replace(R.id.entry_container, entryFragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        int entryId = getEntryIdFromIntent();
        LOGGER.info("Start Activity with entry id: " + entryId);

        Entry entry = getEntryById(entryId);
        entryFragment.applyEntry(entry);
    }

    @Override
    public void onOpenUrl(final String url) {
        if (!Strings.isNullOrEmpty(url)) {

            Uri uriToOpen;

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                uriToOpen = Uri.parse("http://" + url);
            } else {
                uriToOpen = Uri.parse(url);
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uriToOpen);
            startActivity(browserIntent);
        }
    }

    private int getEntryIdFromIntent() {
        return new EntryActivityIntent(getIntent()).getEntryId();
    }

    private Entry getEntryById(final int entryId) {
        SQLiteRepository repository = ((PassSafeApplication) getApplication()).getRepository();
        return repository.findEntry(entryId);
    }
}
