package com.KineFit.app.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Intent;

import com.KineFit.app.activities.LoginActivity;

/**
 * Created by Thomas on 17/05/16.
 */
public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor voor Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref bestandsnaam
    private static final String PREF_NAME = "KineFitPref";

    // Tag voor login
    private static final String IS_LOGIN = "IsLoggedIn";

    // Tag voor username
    public static final String KEY_USERNAME = "username";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Maak login sessie
     * @param name username
     * */
    public void createLoginSession(String name){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USERNAME, name);
        editor.commit();

        launchStepsService();
    }

    /**
     * Deze methode checkt of er een gebruiker is ingelogd.
     * Indien niet, doorverwezen naar login pagina.
     * Indien wel, gebeurt er niets.
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // wanneer gebruiker niet is ingelogd --> naar loginpagina!
            launchLoginActivity();
        }

    }

    /**
     * Get de ingelogde gebruikersnaam
     * */
    public String getUsername(){
        return pref.getString(KEY_USERNAME, null);
    }

    /**
     * Loguit
     * */
    public void logoutUser(){

        editor.clear();
        editor.commit();

        // Bij loguit, terug naar loginactivity!
        launchLoginActivity();
    }

    /**
     * Quick check voor login
     * **/
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Stopt alle activities en start loginactivity.
     */
    private void launchLoginActivity(){
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Start LoginActivity
        _context.startActivity(i);
    }

    private void launchStepsService(){
        Intent i = new Intent(_context, RegisterStepsService.class);
        _context.startService(i);
    }
}
