package cap.project.rainyday.tool;

import android.content.Context;
import android.content.SharedPreferences;

public class SortSharedPreferences {

    private static final String PREF_SORT = "PREF_SORT";


    // 사용자 ID를 SharedPreferences에 저장하는 메서드
    public static void saveSort(Context context, int sort) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_SORT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_SORT, sort);
        editor.apply();
    }

    public static int getSort(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_SORT, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(PREF_SORT, 0);
    }

}
