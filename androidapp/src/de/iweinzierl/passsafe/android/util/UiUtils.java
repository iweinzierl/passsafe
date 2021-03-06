package de.iweinzierl.passsafe.android.util;

import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public final class UiUtils {

    public static TextView getTextView(final View parent, final int resId) {
        View view = parent.findViewById(resId);

        if (view instanceof TextView) {
            return (TextView) view;
        }

        return null;
    }

    public static EditText getEditText(final View parent, final int resId) {
        View view = parent.findViewById(resId);

        if (view instanceof EditText) {
            return (EditText) view;
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

    public static View getButtonOrImageButton(final View parent, final int resId) {
        View view = parent.findViewById(resId);

        if (view instanceof Button || view instanceof ImageButton) {
            return view;
        }

        return null;
    }

    public static CheckBox getCheckBox(final View parent, final int resId) {
        View view = parent.findViewById(resId);

        if (view instanceof CheckBox) {
            return (CheckBox) view;
        }

        return null;
    }

    public static Spinner getSpinner(final View parent, final int resId) {
        View view = parent.findViewById(resId);

        if (view instanceof Spinner) {
            return (Spinner) view;
        }

        return null;
    }
}
