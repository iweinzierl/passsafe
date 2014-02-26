package de.iweinzierl.passsafe.android.activity.login;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.Menu;

import de.iweinzierl.passsafe.android.PassSafeApplication;
import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.activity.sync.SyncActivity;
import de.iweinzierl.passsafe.android.logging.Logger;

public class LoginActivity extends Activity implements LoginFragment.ActionHandler {

    private static final Logger LOGGER = new Logger("LoginActivity");

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getFragmentManager().beginTransaction().replace(R.id.fragment_login, new LoginFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public void login(final String password) {
        LOGGER.info("Received login() event");
        ((PassSafeApplication) getApplication()).setPassword(password);

        // TODO Verify password
        startActivity(new Intent(this, SyncActivity.class));
    }
}
