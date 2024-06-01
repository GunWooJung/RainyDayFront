package cap.project.rainyday.tool;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class LocationManager {

    private static final String PREFS_NAME = "LocationPrefs";

    // Method to save a location's visit count

    // Method to increment a location's visit count
    public static void incrementLocationVisitCount(Context context, String locationName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get current visit count, default to 0 if not found
        int currentCount = sharedPreferences.getInt(locationName, 0);

        // Increment the visit count
        editor.putInt(locationName, currentCount + 1);

        editor.apply();  // Don't forget to apply the changes!
    }

    // Method to load all location visit counts
    public static HashMap<String, Integer> loadAllLocationVisitCounts(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Get all entries from SharedPreferences
        Map<String, ?> allEntries = sharedPreferences.getAll();
        HashMap<String, Integer> locations = new HashMap<>();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getValue() instanceof Integer) {
                locations.put(entry.getKey(), (Integer) entry.getValue());
            }
        }

        return locations;
    }

    // Method to delete all saved location information
    public static void deleteAllSavedLocationInformation(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all data from SharedPreferences
        editor.apply(); // Don't forget to apply the changes!
    }
}
