package de.iweinzierl.passsafe.android.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.data.DatabaseEntryCategory;
import de.iweinzierl.passsafe.android.util.ColorUtils;
import de.iweinzierl.passsafe.android.util.UiUtils;
import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

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
        applyColorBar(listItem, entry.getCategory());
        applyTitle(listItem, entry);
        applyCategory(listItem, entry);

        return listItem;
    }

    @Override
    protected List<Entry> filter(final List<Entry> items, final String filter) {
        List<Entry> filtered = new ArrayList<Entry>();

        for (Entry entry : items) {
            if (entry.getTitle().toLowerCase().contains(filter.toLowerCase())) {
                filtered.add(entry);
            }
        }

        return filtered;
    }

    private void applyColorBar(final View listItem, final EntryCategory category) {
        int id = ((DatabaseEntryCategory) category).getId();

        View view = listItem.findViewById(R.id.colorbar);
        if (view != null) {
            view.setBackgroundColor(ColorUtils.colorById(id));
        }
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
