package de.iweinzierl.passsafe.android.adapter;

import android.content.Context;
import android.widget.BaseAdapter;
import com.google.common.base.Preconditions;

import java.util.List;

public abstract class AbstractListAdapter<T> extends BaseAdapter {

    private List<T> items;
    private Context context;

    public AbstractListAdapter(Context context, List<T> items) {
        super();

        Preconditions.checkNotNull(context, "Context in AbstractListAdapter constructor may not be null.");
        Preconditions.checkNotNull(items, "List in AbstractListAdapter constructor may not be null.");

        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int position) {
        if (position < getCount()) {
            return items.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected Context getContext() {
        return context;
    }
}
