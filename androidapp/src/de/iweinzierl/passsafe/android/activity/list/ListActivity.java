package de.iweinzierl.passsafe.android.activity.list;

import java.util.List;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.support.v4.widget.DrawerLayout;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import de.iweinzierl.passsafe.android.PassSafeApplication;
import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.activity.addentry.AddEntryActivity;
import de.iweinzierl.passsafe.android.activity.editentry.EditEntryIntent;
import de.iweinzierl.passsafe.android.activity.entry.EntryActivityIntent;
import de.iweinzierl.passsafe.android.adapter.CategoryListAdapter;
import de.iweinzierl.passsafe.android.data.DatabaseEntry;
import de.iweinzierl.passsafe.android.data.DatabaseEntryCategory;
import de.iweinzierl.passsafe.android.data.SQLiteRepository;
import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class ListActivity extends Activity implements ListFragment.Callback {

    private static final Logger LOGGER = new Logger("ListActivity");

    private SearchView searchView;
    private EntryCategory selectedCategory;

    private class DrawerClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            EntryCategory category = getCategoryByPosition(position);

            if (category == null) {
                return;
            }

            LOGGER.debug("Clicked category: " + category.getTitle());
            showCategory(category);

            categoryDrawer.closeDrawers();
        }
    }

    private DrawerLayout categoryDrawer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LOGGER.debug("Entered ListActivity");

        setContentView(R.layout.activity_list);
        getFragmentManager().beginTransaction().replace(R.id.entry_list_container, new ListFragment()).commit();

        categoryDrawer = (DrawerLayout) findViewById(R.id.root_layout);
        initializeCategoryList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showAllEntries();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateCategories();

        if (selectedCategory != null) {
            showEntries(selectedCategory);
        }

        if (searchView != null) {
            filterEntries(searchView.getQuery().toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.list, menu);

        final MenuItem item = menu.findItem(R.id.search);
        searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(final String searchText) {
                    filterEntries(searchText);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(final String searchText) {
                    filterEntries(searchText);
                    return true;
                }
            });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add_entry :

                startActivity(new Intent(this, AddEntryActivity.class));
                return true;

            default :
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onEntryClicked(final Entry entry) {
        int entryId = ((DatabaseEntry) entry).getId();
        LOGGER.debug("Going to start EntryActivity with entry id: " + entryId);

        EntryActivityIntent intent = new EntryActivityIntent(this);
        intent.putEntryId(entryId);

        startActivity(intent);

    }

    @Override
    public void onEntryEdit(final Entry entry) {
        int entryId = ((DatabaseEntry) entry).getId();
        LOGGER.debug("Going to start EditEntryActivity with entry id: " + entryId);

        EditEntryIntent i = new EditEntryIntent(this);
        i.putEntryId(entryId);

        startActivity(i);
    }

    @Override
    public void onEntryDelete(final Entry entry) {
        PassSafeApplication application = (PassSafeApplication) getApplication();
        SQLiteRepository repository = application.getRepository();

        if (repository.delete(entry)) {
            Toast.makeText(this, R.string.fragment_entrylist_info_deletionsuccessful, Toast.LENGTH_SHORT).show();
            getListFragment().remove(entry);
            updateCategories();
        } else {
            Toast.makeText(this, R.string.fragment_entrylist_info_deletionfailed, Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeCategoryList() {
        View header = findViewById(R.id.drawer_header);
        header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    showAllEntries();
                    categoryDrawer.closeDrawers();
                }
            });

        ListView categoryList = getCategoryList();
        categoryList.setAdapter(new CategoryListAdapter(this,
                ((PassSafeApplication) getApplication()).getRepository()));
        categoryList.setOnItemClickListener(new DrawerClickListener());
    }

    protected List<Entry> getEntriesFromBackend() {
        SQLiteRepository repository = ((PassSafeApplication) getApplication()).getRepository();
        return repository.listEntries();
    }

    protected List<Entry> getEntriesFromBackend(final EntryCategory category) {
        SQLiteRepository repository = ((PassSafeApplication) getApplication()).getRepository();
        return repository.findEntries(((DatabaseEntryCategory) category).getId());
    }

    protected ListView getCategoryList() {
        return (ListView) findViewById(R.id.category_list);
    }

    protected ListFragment getListFragment() {
        return (ListFragment) getFragmentManager().findFragmentById(R.id.entry_list_container);
    }

    protected EntryCategory getCategoryByPosition(final int position) {
        ListView categoryList = getCategoryList();
        return (EntryCategory) categoryList.getAdapter().getItem(position);
    }

    protected void showAllEntries() {
        LOGGER.debug("> showAllEntries");
        selectedCategory = null;

        getListFragment().showEntries(getEntriesFromBackend());

        if (searchView != null) {
            filterEntries(searchView.getQuery().toString());
        }
    }

    protected void showCategory(final EntryCategory category) {
        selectedCategory = category;
        showEntries(category);
    }

    protected void showEntries(final EntryCategory category) {
        LOGGER.debug(String.format("> showCategory(%s)", category.getTitle()));
        getListFragment().showEntries(getEntriesFromBackend(category));

        if (searchView != null) {
            filterEntries(searchView.getQuery().toString());
        }
    }

    protected void updateCategories() {
        ListView categoryList = getCategoryList();
        ListAdapter adapter = categoryList.getAdapter();

        if (adapter instanceof CategoryListAdapter) {
            ((CategoryListAdapter) adapter).notifyDataSetChanged();
        }
    }

    protected void filterEntries(final String filter) {
        getListFragment().filterEntries(filter);
    }
}
