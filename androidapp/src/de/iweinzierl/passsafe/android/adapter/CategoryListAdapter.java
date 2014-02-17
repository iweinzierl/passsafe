package de.iweinzierl.passsafe.android.adapter;

import java.util.List;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class CategoryListAdapter extends AbstractListAdapter<EntryCategory> {

    public CategoryListAdapter(final Context context, final List<EntryCategory> items) {
        super(context, items);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        EntryCategory item = getItem(position);

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View entry = layoutInflater.inflate(R.layout.listitem_entry, parent, false);

        TextView tv = (TextView) entry.findViewById(R.id.title);
        tv.setText(item.getTitle());

        return entry;
    }
}
