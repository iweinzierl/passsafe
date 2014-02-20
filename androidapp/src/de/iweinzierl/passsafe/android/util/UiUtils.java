package de.iweinzierl.passsafe.android.util;

import android.view.View;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public final class UiUtils {

    public static TextView getTextView(final View parent, final int resId) {
        View view = parent.findViewById(resId);

        if (view instanceof TextView) {
            return (TextView) view;
        }

        return null;
    }

    public static ListView getListView(final View parent, final int resId) {
        View view = parent.findViewById(resId);

        if (view instanceof ListView) {
            return (ListView) view;
        }

        return null;
    }

    public static Button getButton(final View parent, final int resId) {
        View view = parent.findViewById(resId);

        if (view instanceof Button) {
            return (Button) view;
        }

        return null;
    }
}
