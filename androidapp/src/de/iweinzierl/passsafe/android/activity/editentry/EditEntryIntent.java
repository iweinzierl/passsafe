package de.iweinzierl.passsafe.android.activity.editentry;

import android.content.Context;
import android.content.Intent;

public class EditEntryIntent extends Intent {

    public static final String EXTRA_ENTRY_ID = "extra.entry.id";

    public EditEntryIntent(final Context packageContext) {
        super(packageContext, EditEntryActivity.class);
    }

    public EditEntryIntent(final Intent o) {
        super(o);
    }

    public void putEntryId(final int id) {
        putExtra(EXTRA_ENTRY_ID, id);
    }

    public int getEntryId() {
        return getIntExtra(EXTRA_ENTRY_ID, 0);
    }
}
