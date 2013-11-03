package de.iweinzierl.passsafe.android.activity.login;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.common.base.Preconditions;

import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.logging.Logger;

public class LoginFragment extends Fragment {

    public interface ActionHandler {
        void login(String password);
    }

    private static final Logger LOGGER = new Logger("LoginFragment");

    private ActionHandler actionHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Preconditions.checkArgument(getActivity() instanceof ActionHandler);

        this.actionHandler = (ActionHandler) getActivity();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button loginButton = findLoginButton(view);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fireLoginEvent();
            }
        });
    }

    public void fireLoginEvent() {
        EditText passwordField = findPasswordField(getView());
        if (passwordField == null) {
            return;
        }

        actionHandler.login(passwordField.getText().toString());
    }

    private Button findLoginButton(View container) {
        View view = container.findViewById(R.id.login_button);
        if (view != null) {
            return (Button) view;
        }

        LOGGER.warn("Did not find fireLoginEvent button!");
        return null;
    }

    private EditText findPasswordField(View container) {
        View view = container.findViewById(R.id.password);
        if (view != null) {
            return (EditText) view;
        }

        LOGGER.warn("Did not find password field!");
        return null;
    }
}