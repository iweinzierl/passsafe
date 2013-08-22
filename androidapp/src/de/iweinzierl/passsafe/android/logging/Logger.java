package de.iweinzierl.passsafe.android.logging;

import android.util.Log;

import com.google.common.base.Preconditions;

public class Logger {

    public static final String TAG = "PassSafe";

    private String tag;

    public Logger(String className) {
        Preconditions.checkNotNull(className, "Class name must not be null");
        this.tag = String.format("[%s | %s]", TAG, className);
    }

    public void debug(String message) {
        Log.d(tag, message);
    }

    public void info(String message) {
        Log.i(tag, message);
    }

    public void warn(String message) {
        Log.w(tag, message);
    }

    public void warn(String message, Throwable throwable) {
        Log.w(tag, message, throwable);
    }

    public void error(String message) {
        Log.e(tag, message);
    }

    public void error(String message, Throwable throwable) {
        Log.e(tag, message, throwable);
    }
}
