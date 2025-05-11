package files; // Or your appropriate package

import android.content.Context;
import android.content.SharedPreferences;


public class HistoryOfUnitAndCategoryPrefs {

    private static final String PREFS_NAME = "history_app_prefs"; // Unique name for your SharedPreferences file
    private static final String KEY_CATEGORY_INDEX = "category_index";
    private static final String KEY_UNIT_INDEX = "unit_index";

    // Default values if nothing is stored yet
    private static final int DEFAULT_CATEGORY_INDEX = 1;
    private static final int DEFAULT_UNIT_INDEX = 1;

    /**
     * Retrieves the last saved category and unit index from SharedPreferences.
     * If no values are found, default values (1, 1) are returned.
     *
     * @param context The application context.
     * @return A UnitAndCaetogryHistoryHelper object containing the category and unit index.
     */
    public static UnitAndCaetogryHistoryHelper getUnitAndCategory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        int category = prefs.getInt(KEY_CATEGORY_INDEX, DEFAULT_CATEGORY_INDEX);
        int unit = prefs.getInt(KEY_UNIT_INDEX, DEFAULT_UNIT_INDEX);

        return new UnitAndCaetogryHistoryHelper(category, unit);
    }

    /**
     * Updates and saves the category and unit index to SharedPreferences.
     *
     * @param context  The application context.
     * @param category The category index to save.
     * @param unit     The unit index to save.
     */
    public static void updateUnitAndCategory(Context context, int category, int unit) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(KEY_CATEGORY_INDEX, category);
        editor.putInt(KEY_UNIT_INDEX, unit);

        editor.apply(); // Asynchronously saves the preferences
    }

    /**
     * Optional: Clears the saved history from SharedPreferences.
     * After calling this, getUnitAndCategory will return default values.
     *
     * @param context The application context.
     */
    public static void clearHistory(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_CATEGORY_INDEX);
        editor.remove(KEY_UNIT_INDEX);
        // Alternatively, to clear all preferences in this file:
        // editor.clear();
        editor.apply();
    }
}
