package de.iweinzierl.passsafe.android.activity.addentry;

import android.app.Activity;

import android.os.Bundle;

import de.iweinzierl.passsafe.android.R;

public class AddEntryActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addentry);

        getFragmentManager().beginTransaction().replace(R.id.fragment_addentry, new AddEntryFragment()).commit();
    }
}
