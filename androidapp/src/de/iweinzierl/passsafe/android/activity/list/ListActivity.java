package de.iweinzierl.passsafe.android.activity.list;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.ListView;
import com.google.common.collect.Lists;
import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.adapter.CategoryListAdapter;
import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class ListActivity extends Activity {

    private static final Logger LOGGER = new Logger("ListActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LOGGER.debug("Entered ListActivity");

        setContentView(R.layout.activity_list);
        getFragmentManager().beginTransaction().replace(R.id.entry_list_container, new ListFragment()).commit();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.root_layout);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ListView categoryList = (ListView) findViewById(R.id.category_list);
        categoryList.addHeaderView(layoutInflater.inflate(R.layout.category_drawer_header, null, false));
        categoryList.setAdapter(new CategoryListAdapter(this, Lists.newArrayList(new EntryCategory("TEST CATEGORY"))));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }
}
