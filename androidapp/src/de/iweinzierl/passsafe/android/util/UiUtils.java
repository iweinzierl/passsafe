package de.iweinzierl.passsafe.android.util;

import android.view.View;
import android.widget.ListView;

public class UiUtils {

    public static ListView getListView(final View parent, final int resId) {
        View view = parent.findViewById(resId);

        if (view instanceof ListView) {
            return (ListView) view;
        }

        return null;
    }
}
