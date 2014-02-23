package de.iweinzierl.passsafe.android.adapter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import android.content.Context;

import android.widget.BaseAdapter;

public abstract class AbstractListAdapter<T> extends BaseAdapter {

    private List<T> visibleItems;
    private List<T> items;
    private Context context;

    public AbstractListAdapter(final Context context, final List<T> items) {
        super();

        Preconditions.checkNotNull(context, "Context in AbstractListAdapter constructor may not be null.");
        Preconditions.checkNotNull(items, "List in AbstractListAdapter constructor may not be null.");

        this.context = context;
        this.items = items;
        this.visibleItems = new ArrayList<T>(items);
    }

    protected abstract List<T> filter(List<T> items, String filter);

    @Override
    public int getCount() {
        return visibleItems.size();
    }

    @Override
    public T getItem(final int position) {
        if (position < getCount()) {
            return visibleItems.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    public int getPosition(final T category) {
        return items.indexOf(category);
    }

    public void remove(final T item) {
        items.remove(item);
        visibleItems.remove(item);
        notifyDataSetChanged();
    }

    public void filter(final String filter) {
        if (Strings.isNullOrEmpty(filter)) {
            visibleItems = new ArrayList<T>(items);
            notifyDataSetChanged();
            return;
        }

        visibleItems = filter(items, filter);
        notifyDataSetChanged();
    }

    protected Context getContext() {
        return context;
    }
}
