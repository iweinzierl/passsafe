package de.iweinzierl.passsafe.android.activity.entry;

import android.content.Context;
import android.content.Intent;

public class EntryActivityIntent extends Intent {

    private static final String EXTRA_ENTRY_ID = "extra.entry.id";

    public EntryActivityIntent(final Context packageContext) {
        super(packageContext, EntryActivity.class);
    }

    public void putEntryId(final int id) {
        putExtra(EXTRA_ENTRY_ID, id);
    }
}
