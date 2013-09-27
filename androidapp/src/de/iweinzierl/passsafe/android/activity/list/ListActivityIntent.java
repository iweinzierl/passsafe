package de.iweinzierl.passsafe.android.activity.list;

import android.content.Context;
import android.content.Intent;

public class ListActivityIntent extends Intent {

    public ListActivityIntent(Context context) {
        super(context, ListActivity.class);
    }
}
