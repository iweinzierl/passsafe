package de.iweinzierl.passsafe.android.activity.addcategory;

import android.app.Activity;

import android.os.Bundle;

import de.iweinzierl.passsafe.android.R;

public class AddCategoryActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcategory);

        getFragmentManager().beginTransaction().replace(R.id.fragment_addcategory, new AddCategoryFragment()).commit();
    }
}
