package de.iweinzierl.passsafe.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

import java.util.List;

public class CategoryListAdapter extends AbstractListAdapter<EntryCategory> {

    public CategoryListAdapter(Context context, List<EntryCategory> items) {
        super(context, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO implement proper category item view

        TextView v = new TextView(getContext());
        v.setText(getItem(position).getTitle());

        return v;
    }
}
