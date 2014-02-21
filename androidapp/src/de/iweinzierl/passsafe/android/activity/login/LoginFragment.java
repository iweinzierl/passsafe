package de.iweinzierl.passsafe.android.activity.login;

import com.google.common.base.Preconditions;

import android.app.Activity;
import android.app.Fragment;

import android.os.Bundle;

import android.text.method.PasswordTransformationMethod;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.util.UiUtils;

public class LoginFragment extends Fragment {

    public interface ActionHandler {
        void login(String password);
    }

    private static final Logger LOGGER = new Logger("LoginFragment");

    private ActionHandler actionHandler;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initToggleVisibility(view);

        return view;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        Preconditions.checkArgument(getActivity() instanceof ActionHandler);

        this.actionHandler = (ActionHandler) getActivity();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        Button loginButton = findLoginButton(view);
        loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
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

    private void initToggleVisibility(final View parent) {
        CheckBox checkbox = UiUtils.getCheckBox(parent, R.id.showpassword);
        if (checkbox != null) {
            checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        togglePasswordVisibility();
                    }
                });
        }
    }

    private Button findLoginButton(final View container) {
        View view = container.findViewById(R.id.login_button);
        if (view != null) {
            return (Button) view;
        }

        LOGGER.warn("Did not find fireLoginEvent button!");
        return null;
    }

    private EditText findPasswordField(final View container) {
        View view = container.findViewById(R.id.password);
        if (view != null) {
            return (EditText) view;
        }

        LOGGER.warn("Did not find password field!");
        return null;
    }

    private void togglePasswordVisibility() {
        EditText editText = UiUtils.getEditText(getView(), R.id.password);

        int selectionStart = editText.getSelectionStart();
        int selectionEnd = editText.getSelectionEnd();

        if (isPasswordVisibleChecked()) {
            editText.setTransformationMethod(null);
        } else {
            editText.setTransformationMethod(new PasswordTransformationMethod());
        }

        editText.setSelection(selectionStart, selectionEnd);
    }

    private boolean isPasswordVisibleChecked() {
        CheckBox checkBox = UiUtils.getCheckBox(getView(), R.id.showpassword);
        return checkBox != null && checkBox.isChecked();
    }
}
