package de.iweinzierl.passsafe.android.activity.login;

import android.app.Application;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import de.iweinzierl.passsafe.android.PassSafeApplication;
import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.logging.Logger;

public class LoginActivity extends Activity implements LoginFragment.ActionHandler {

    private static final Logger LOGGER = new Logger("LoginActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getFragmentManager().beginTransaction().replace(R.id.fragment_login, new LoginFragment()).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public void login(String password) {
        LOGGER.info("Received login() event");
        ((PassSafeApplication) getApplication()).setPassword(password);

        // TODO start next activity
    }
}
