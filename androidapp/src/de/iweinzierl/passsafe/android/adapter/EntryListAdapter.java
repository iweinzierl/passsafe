package de.iweinzierl.passsafe.android.adapter;

import java.util.List;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.util.UiUtils;
import de.iweinzierl.passsafe.shared.domain.Entry;

public class EntryListAdapter extends AbstractListAdapter<Entry> {

    private static final int RES_TITLE = R.id.title;
    private static final int RES_CATEGORY = R.id.category;

    public EntryListAdapter(final Context context, final List<Entry> items) {
        super(context, items);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = inflater.inflate(R.layout.listitem_entry, parent, false);

        Entry entry = getItem(position);
        applyTitle(listItem, entry);
        applyCategory(listItem, entry);

        return listItem;
    }

    private void applyTitle(final View listItem, final Entry entry) {
        TextView view = UiUtils.getTextView(listItem, RES_TITLE);
        if (view != null) {
            view.setText(entry.getTitle());
        }
    }

    private void applyCategory(final View listItem, final Entry entry) {
        TextView view = UiUtils.getTextView(listItem, RES_CATEGORY);
        if (view != null) {
            view.setText(entry.getCategory().getTitle());
        }
    }
}
