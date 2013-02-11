package org.yousense.eventlog;

import android.content.Context;
import android.util.Log;
import org.apache.commons.lang3.StringUtils;
import org.yousense.common.Time;
import org.yousense.eventlog.data.DebugData;

import java.util.ArrayDeque;

/**
 * Debug log interface to eventlog.
 * Messages are logged to the Android DDMS console and eventlog with the "debug.log" tag.
 * Latest messages are cached in memory for display in the app.
 */
public class DebugLog {
    public static final int SCROLLBACK_LINES = 200;

    private static boolean logToEventLog = false;
    private static ArrayDeque<String> scrollback = new ArrayDeque<String>();

    // API for displaying latest messages

    public static synchronized String latest() {
        return StringUtils.join(scrollback, "\n");
    }

    // API similar to android.util.Log.

    public static void d(String tag, String message) {
        Log.d(tag, message);
        appendToEventLogAndScrollback(tag, Log.DEBUG, message, null);
    }

    public static void d(String tag, String message, Throwable tr) {
        Log.d(tag, message, tr);
        appendToEventLogAndScrollback(tag, Log.DEBUG, message, tr);
    }

    public static void i(String tag, String message) {
        Log.i(tag, message);
        appendToEventLogAndScrollback(tag, Log.INFO, message, null);
    }

    public static void i(String tag, String message, Throwable tr) {
        Log.i(tag, message, tr);
        appendToEventLogAndScrollback(tag, Log.INFO, message, tr);
    }

    public static void e(String tag, String message) {
        Log.e(tag, message);
        appendToEventLogAndScrollback(tag, Log.ERROR, message, null);
    }

    public static void e(String tag, String message, Throwable tr) {
        Log.e(tag, message, tr);
        appendToEventLogAndScrollback(tag, Log.ERROR, message, tr);
    }

    private static void appendToEventLogAndScrollback(String tag, int level, String message, Throwable tr) {
        DebugData data = new DebugData(Log.DEBUG, tag, message, null);
        appendToScrollback(data);
        if (logToEventLog)
            EventLog.append("debug.log", data);
    }

    private static synchronized void appendToScrollback(DebugData data) {
        String line = String.format("%s: %s", Time.timeOnlyWithMilliseconds(), data.message);
        if (data.stacktrace != null)
            line += "\n" + data.stacktrace;
        scrollback.addLast(line);
        while (scrollback.size() > SCROLLBACK_LINES)
            scrollback.removeFirst();
    }


}
