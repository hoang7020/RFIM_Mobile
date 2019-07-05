package vn.com.rfim_mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtil {

    private static SharedPreferences pref;
    private static PreferenceUtil instance;

    public static PreferenceUtil getInstance(Context context) {

        if (instance == null) {
            instance = new PreferenceUtil();
        }
        if (pref == null) {
            pref = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return instance;
    }

    public String getStringValue(String KEY, String defvalue) {
        return pref.getString(KEY, defvalue);
    }

    public void putStringValue(String KEY, String value) {
        pref.edit().putString(KEY, value).apply();
    }

    public int getIntValue(String KEY, int defvalue) {
        return pref.getInt(KEY, defvalue);
    }

    public void putIntValue(String KEY, int value) {
        pref.edit().putInt(KEY, value).apply();
    }

}
