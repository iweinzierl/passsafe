package de.iweinzierl.passsafe.android.activity.list;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
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
    }

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

    public void showEntries(final List<Entry> entries) {
        final EntryListAdapter entryListAdapter = new EntryListAdapter(getActivity(), entries);

        ListView entryList = UiUtils.getListView(getView(), R.id.entry_list);
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
    }
}
