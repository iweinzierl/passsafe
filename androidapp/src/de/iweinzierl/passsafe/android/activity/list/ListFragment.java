package de.iweinzierl.passsafe.android.activity.list;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.logging.Logger;

public class ListFragment extends Fragment {

    private static final Logger LOGGER = new Logger("ListFragment");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }
}
