package de.iweinzierl.passsafe.android.logging;

import com.google.common.base.Preconditions;

import android.util.Log;

public class Logger {

    public static final String TAG = "PassSafe";

    private String tag;

    public Logger(final String className) {
        Preconditions.checkNotNull(className, "Class name must not be null");
        this.tag = String.format("[%s | %s]", TAG, className);
    }

    public void debug(final String message) {
        Log.d(tag, message);
    }

    public void debug(final String message, final Object... args) {
        Log.d(tag, String.format(message, args));
    }

    public void info(final String message) {
        Log.i(tag, message);
    }

    public void info(final String message, final Object... args) {
        Log.i(tag, String.format(message, args));
    }

    public void warn(final String message) {
        Log.w(tag, message);
    }

    public void warn(final String message, final Throwable throwable) {
        Log.w(tag, message, throwable);
    }

    public void error(final String message) {
        Log.e(tag, message);
    }

    public void error(final String message, final Throwable throwable) {
        Log.e(tag, message, throwable);
    }

    public void error(final String message, final Throwable throwable, final Object... args) {
        Log.e(tag, String.format(message, args), throwable);
    }
}
