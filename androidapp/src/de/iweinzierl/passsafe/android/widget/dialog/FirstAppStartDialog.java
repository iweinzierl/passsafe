package de.iweinzierl.passsafe.android.widget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;

import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.util.UiUtils;

public class FirstAppStartDialog {

    public interface Callback {

        void onCreateNewDatabase();

        void onSynchronizeDatabase();
    }

    public static class Builder {

        private Activity context;
        private Callback callback;
        private AlertDialog instance;

        public Builder(final Activity context) {
            this.context = context;
        }

        public Builder withCallback(final Callback callback) {
            this.callback = callback;
            return this;
        }

        public Dialog build() {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = layoutInflater.inflate(R.layout.dialog_firstappstart, null, false);

            buildNewDatabaseButton(content);
            buildSynchronizeDatabaseButton(content);

            instance = new AlertDialog.Builder(context).setCancelable(false).setView(content).show();

            return instance;
        }

        private void buildSynchronizeDatabaseButton(final View content) {
            Button synchronizeDatabase = UiUtils.getButton(content, R.id.synchronizedatabases);
            if (synchronizeDatabase != null) {
                synchronizeDatabase.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            callback.onSynchronizeDatabase();
                            instance.dismiss();
                        }
                    });
            }
        }

        private void buildNewDatabaseButton(final View content) {
            Button newDatabase = UiUtils.getButton(content, R.id.newdatabase);
            if (newDatabase != null) {
                newDatabase.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            callback.onCreateNewDatabase();
                            instance.dismiss();
                        }
                    });
            }
        }
    }
}
