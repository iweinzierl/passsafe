package de.iweinzierl.passsafe.android.widget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import de.iweinzierl.passsafe.android.PassSafeApplication;
import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.sync.gdrive.GoogleDriveSync;
import de.iweinzierl.passsafe.android.util.UiUtils;

public class FirstAppStartDialog {

    public interface Callback {

        void onNewDatabaseCreated();

        void onSettingsAdjusted();
    }

    public static class Builder {

        private static final Logger LOGGER = new Logger("FirstAppStartDialog.Builder");

        private Activity context;
        private Callback callback;

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

            Button newDatabase = UiUtils.getButton(content, R.id.newdatabase);
            if (newDatabase != null) {
                newDatabase.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            Toast.makeText(context, "Neue Datenbank erstellen", Toast.LENGTH_SHORT).show();
                            ((PassSafeApplication) context.getApplication()).createNewDatabase();
                        }
                    });
            }

            Button gotoSettings = UiUtils.getButton(content, R.id.gotosettings);
            if (gotoSettings != null) {
                gotoSettings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            Toast.makeText(context, "Einstellungen öffnen", Toast.LENGTH_SHORT).show();
                            synchronizeDatabase();
                        }
                    });
            }

            return new AlertDialog.Builder(context).setView(content).show();
        }

        public void synchronizeDatabase() {

            new GoogleDriveSync(context).sync();
            callback.onNewDatabaseCreated();
        }
    }
}
