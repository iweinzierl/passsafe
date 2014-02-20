package de.iweinzierl.passsafe.android.adapter;

import java.util.List;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.util.UiUtils;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class CategoryListAdapter extends AbstractListAdapter<EntryCategory> {

    private static final int RES_TITLE = R.id.title;
    public static final int RES_ENTRY_COUNT = R.id.entry_count;

    public CategoryListAdapter(final Context context, final List<EntryCategory> items) {
        super(context, items);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = layoutInflater.inflate(R.layout.listitem_category, parent, false);

        EntryCategory category = getItem(position);
        applyTitle(listItem, category);
        applyNumberOfEntries(listItem, category);

        return listItem;
    }

    private void applyTitle(final View listItem, final EntryCategory category) {
        TextView view = UiUtils.getTextView(listItem, RES_TITLE);
        if (view != null) {
            view.setText(category.getTitle());
        }
    }

    private void applyNumberOfEntries(final View listItem, final EntryCategory category) {
        View view = UiUtils.getTextView(listItem, RES_ENTRY_COUNT);
        if (view != null) {
            ((TextView) view).setText("0"); // TODO how to retrieve number of entries in this category?
        }
    }
}
