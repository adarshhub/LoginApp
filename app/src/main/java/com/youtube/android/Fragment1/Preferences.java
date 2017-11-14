package com.youtube.android.Fragment1;

import android.content.Context;
import android.content.SharedPreferences;


public class Preferences {

    private Context context;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    public Preferences(Context context) {
        this.context = context;
        getpreferences();
    }

    public void getpreferences(){

        sharedPreferences = context.getSharedPreferences("FirstTime", Context.MODE_PRIVATE);


    }
    public void writepreferences(){
        editor =sharedPreferences.edit();
        editor.putString("Really","True");
        editor.commit();
    }
    public boolean checkpreferences()
    {
        return !(sharedPreferences.getString("Really", "null")).equalsIgnoreCase("null");
    }
    public void clearpreferences()
    {
        editor.clear().commit();
    }
}
