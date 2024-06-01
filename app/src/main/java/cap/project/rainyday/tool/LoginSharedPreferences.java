package cap.project.rainyday.tool;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginSharedPreferences {
    // SharedPreferences 이름 정의
    private static final String PREF_USER_ID = "PREF_USER_ID";
    private static final String PREF_LOGIN_ID = "PREF_LOGIN_ID";

    private static final String PREF_USER_NAME = "PREF_USER_NAME";

    // 사용자 ID를 SharedPreferences에 저장하는 메서드
    public static void saveUserId(Context context, long userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_USER_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(PREF_USER_ID, userId);
        editor.apply();
    }

    public static void saveUserName(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_USER_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_USER_NAME, name);
        editor.apply();
    }

    public static void saveLoginrId(Context context, String loginId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_LOGIN_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_LOGIN_ID, loginId);
        editor.apply();
    }
    public static String getLoginId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_LOGIN_ID, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_LOGIN_ID, "");
    }

    // SharedPreferences에서 사용자 ID를 가져오는 메서드
    public static long getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_USER_ID, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(PREF_USER_ID, 0);
    }

    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_USER_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_USER_NAME, "");
    }

}
