package files;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsPrefs {
    private static final String PREFS_NAME = "game_settings_prefs";
    private static final String KEY_QUESTIONS_AMOUNT = "questions_amount";
    private static final String KEY_IS_FLIPPED = "is_flipped";
    private static final String KEY_IS_RECORDING_ONLY = "is_recording_only";

    private static final int DEFAULT_QUESTIONS_AMOUNT = 10;
    private static final boolean DEFAULT_IS_FLIPPED = false;
    private static final boolean DEFAULT_IS_RECORDING_ONLY = false;

    public static int getQuestionsAmount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_QUESTIONS_AMOUNT, DEFAULT_QUESTIONS_AMOUNT);
    }

    public static void setQuestionsAmount(Context context, int amount) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_QUESTIONS_AMOUNT, amount).apply();
    }

    public static boolean isFlipped(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_FLIPPED, DEFAULT_IS_FLIPPED);
    }

    public static void setFlipped(Context context, boolean isFlipped) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_IS_FLIPPED, isFlipped).apply();
    }

    public static boolean isRecordingOnly(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_RECORDING_ONLY, DEFAULT_IS_RECORDING_ONLY);
    }

    public static void setRecordingOnly(Context context, boolean isRecordingOnly) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_IS_RECORDING_ONLY, isRecordingOnly).apply();
    }
}
