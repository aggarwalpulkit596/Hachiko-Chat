package me.dats.com.datsme;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hp on 12-Jul-18.
 */
public class MyPreference {
    Context context;

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;

    private static final String PREF_NAME = "com.example.App";
    public static final String USERNAME = "id";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String COMPPRO = "DONE";

    public MyPreference(Context context) {

        this.context = context;

        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        editor = sharedPreferences.edit();

        editor.apply();

    }
    public void putString(String key, String value) {

        editor.putString(key, value);

        editor.apply();

    }
    public String getString(String key) {

        return sharedPreferences.getString(key, "false");

    }
    //Method to clear the login data of the application.
    public void clearLoginData() {

        editor.remove(USERNAME);
        editor.remove(KEY_ACCESS_TOKEN);
        editor.apply();

    }


}
