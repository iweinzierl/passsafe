package de.iweinzierl.passsafe.android.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.data.SQLiteRepository;
import de.iweinzierl.passsafe.android.util.ColorUtils;
import de.iweinzierl.passsafe.android.util.UiUtils;
import de.iweinzierl.passsafe.shared.domain.DatabaseEntryCategory;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class CategoryListAdapter extends AbstractListAdapter<EntryCategory> {

    private static final int RES_TITLE = R.id.title;
    public static final int RES_ENTRY_COUNT = R.id.entry_count;

    private final SQLiteRepository repository;

    public CategoryListAdapter(final Context context, final SQLiteRepository repository) {
        super(context, repository.listCategories());
        this.repository = repository;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = layoutInflater.inflate(R.layout.listitem_category, parent, false);

        EntryCategory category = getItem(position);
        applyColorBar(listItem, category);
        applyTitle(listItem, category);
        applyNumberOfEntries(listItem, category);

        return listItem;
    }

    @Override
    protected List<EntryCategory> filter(final List<EntryCategory> items, final String filter) {
        List<EntryCategory> categories = new ArrayList<EntryCategory>();

        for (EntryCategory category : items) {
            if (category.getTitle().toLowerCase().contains(filter.toLowerCase())) {
                categories.add(category);
            }
        }

        return categories;
    }

    private void applyColorBar(final View listItem, final EntryCategory category) {
        int id = ((DatabaseEntryCategory) category).getId();

        View view = listItem.findViewById(R.id.colorbar);
        if (view != null) {
            view.setBackgroundColor(ColorUtils.colorById(id));
        }
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
            ((TextView) view).setText(String.valueOf(getNumberOfEntries(category)));
        }
    }

    private int getNumberOfEntries(final EntryCategory category) {
        return repository.findEntries(((DatabaseEntryCategory) category).getId()).size();
    }
}
