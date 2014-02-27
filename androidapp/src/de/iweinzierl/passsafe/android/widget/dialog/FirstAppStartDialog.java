package de.iweinzierl.passsafe.android.widget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import de.iweinzierl.passsafe.android.R;
import de.iweinzierl.passsafe.android.logging.Logger;
import de.iweinzierl.passsafe.android.sync.gdrive.GoogleDriveSync;
import de.iweinzierl.passsafe.android.util.UiUtils;
import de.iweinzierl.passsafe.shared.data.SQLiteCommandExecutor;
import de.iweinzierl.passsafe.shared.data.SQLiteDatabaseCreator;
import de.iweinzierl.passsafe.shared.exception.PassSafeSqlException;

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
                            createNewDatabase();
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

        private void createNewDatabase() {
            SQLiteOpenHelper dbHelper = new SQLiteOpenHelper(context, "passsafe.sqlite", null, 1) {
                @Override
                public void onCreate(final SQLiteDatabase db) {

                    try {
                        final SQLiteDatabaseCreator creator = new SQLiteDatabaseCreator(new SQLiteCommandExecutor() {
                                    @Override
                                    public boolean execute(final String sql) throws PassSafeSqlException {
                                        db.execSQL(sql);
                                        return true;
                                    }
                                }, SQLiteDatabaseCreator.OS.ANDROID);
                        creator.setup();
                        callback.onNewDatabaseCreated();

                    } catch (PassSafeSqlException e) {
                        LOGGER.error("Unable to create passsafe database", e);
                        Toast.makeText(context, "Unable to create passsafe database", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) { }
            };

            dbHelper.getWritableDatabase();
            dbHelper.close();
        }

        public void synchronizeDatabase() {

            new GoogleDriveSync(context).sync();
            callback.onNewDatabaseCreated();
        }
    }
}
