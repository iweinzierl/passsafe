package de.iweinzierl.passsafe.android.activity.editentry;

import android.app.Activity;

import android.os.Bundle;

import android.widget.Toast;

import de.iweinzierl.passsafe.android.PassSafeApplication;
import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.data.SQLiteRepository;
import de.iweinzierl.passsafe.shared.domain.Entry;

public class EditEntryActivity extends Activity implements EditEntryFragment.Callback {

    private EditEntryFragment editEntryFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editentry);

        editEntryFragment = new EditEntryFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_editentry, editEntryFragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        EditEntryIntent i = new EditEntryIntent(getIntent());
        int entryId = i.getEntryId();

        editEntryFragment.applyEntry(getEntry(entryId));
    }

    @Override
    public void onUpdate(final Entry entry) {
        PassSafeApplication application = (PassSafeApplication) getApplication();
        SQLiteRepository repository = application.getRepository();

        Entry updatedEntry = repository.update(entry);

        if (updatedEntry != null) {
            Toast.makeText(this, R.string.fragment_editentry_updatesuccessful, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.fragment_editentry_updatefailed, Toast.LENGTH_SHORT).show();
        }
    }

    private Entry getEntry(final int id) {
        PassSafeApplication application = (PassSafeApplication) getApplication();
        return application.getRepository().findEntry(id);
    }
}
