package com.cj.music_player.db;

import android.content.SharedPreferences;
import android.content.Context;

public class SharedUtils
{
    public static String SHARE_NAME = "Shared";

    private static SharedPreferences getPreference(Context context)
    {
        return context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
    }

    public static int getInt(Context context, String key, int defValue)
    {
        return getPreference(context).getInt(key, defValue);
    }

    public static void saveInt(Context context, String key, int value)
    {
        getPreference(context).edit().putInt(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defValue)
    {
        return getPreference(context).getBoolean(key, defValue);
    }

    public static void saveBoolean(Context context, String key, boolean value)
    {
        getPreference(context).edit().putBoolean(key, value).commit();
    }
    
    public static void saveString(Context context, String key, String value)
    {
        getPreference(context).edit().putString(key, value).commit();
    }
    
    public static String getString(Context context, String key, String string)
    {
        return getPreference(context).getString(key, string);
    }
}
