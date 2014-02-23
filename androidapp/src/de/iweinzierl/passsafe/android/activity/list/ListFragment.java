package de.iweinzierl.passsafe.android.activity.list;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;

import android.os.Bundle;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ListView;

import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.adapter.EntryListAdapter;
import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.util.UiUtils;
import de.iweinzierl.passsafe.shared.domain.Entry;

public class ListFragment extends Fragment {

    public interface Callback {
        void onEntryClicked(Entry entry);

        void onEntryEdit(Entry entry);

        void onEntryDelete(Entry entry);
    }

    private static final int OPTION_EDIT_ENTRY = 1;
    private static final int OPTION_DELETE_ENTRY = 2;

    private static final Logger LOGGER = new Logger("ListFragment");
    private Callback callback;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        if (activity instanceof Callback) {
            callback = (Callback) activity;
        }
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) { }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.entry_list) {
            menu.add(1, OPTION_EDIT_ENTRY, 1, R.string.fragment_entrylist_contextmenu_edit);
            menu.add(1, OPTION_DELETE_ENTRY, 2, R.string.fragment_entrylist_contextmenu_delete);
        }

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        ContextMenu.ContextMenuInfo menuInfo = item.getMenuInfo();
        Entry entry = null;

        if (menuInfo instanceof AdapterView.AdapterContextMenuInfo) {
            int pos = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
            entry = (Entry) getEntryList().getAdapter().getItem(pos);
        }

        if (callback == null || entry == null) {
            LOGGER.debug("Callback or entry is null - do nothing");
            return false;
        }

        switch (item.getItemId()) {

            case OPTION_EDIT_ENTRY :
                callback.onEntryEdit(entry);
                return true;

            case OPTION_DELETE_ENTRY :
                callback.onEntryDelete(entry);
                return true;
        }

        return super.onContextItemSelected(item);
    }

    public void showEntries(final List<Entry> entries) {
        final EntryListAdapter entryListAdapter = new EntryListAdapter(getActivity(), entries);

        ListView entryList = getEntryList();
        entryList.setAdapter(entryListAdapter);

        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, final View view, final int position,
                        final long id) {

                    LOGGER.debug("Clicked entry at position: " + position);
                    if (callback != null) {
                        callback.onEntryClicked(entryListAdapter.getItem(position));
                    } else {
                        LOGGER.warn("No Callback defined; parent Activity must implement Callback for this!");
                    }
                }
            });

        registerForContextMenu(entryList);
    }

    public void remove(final Entry entry) {
        ListView entryList = UiUtils.getListView(getView(), R.id.entry_list);
        ((EntryListAdapter) entryList.getAdapter()).remove(entry);
    }

    public void filterEntries(final String filter) {
        ListView entryList = UiUtils.getListView(getView(), R.id.entry_list);
        ((EntryListAdapter) entryList.getAdapter()).filter(filter);
    }

    protected ListView getEntryList() {
        return UiUtils.getListView(getView(), R.id.entry_list);
    }
}
