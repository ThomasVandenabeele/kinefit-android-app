package com.KineFit.app.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.hardware.camera2.params.StreamConfigurationMap;

import com.KineFit.app.activities.LoginActivity;

/**
 * Created by Thomas on 17/05/16.
 */
public class SessieManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor voor Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // tags
    private static final String PREF_NAME = "KineFitPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAAM = "naam";
    private static final String KEY_VOORNAAM = "voornaam";
    private static final String KEY_REMEMBERED_USER = "remUsername";

    // Constructor
    public SessieManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Maak login sessie
     * @param naam gebruikersnaam
     * */
    public void createLoginSession(String gebruikersnaam, String naam, String voornaam, String email, Boolean remembered){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USERNAME, gebruikersnaam);
        editor.putString(KEY_NAAM, naam);
        editor.putString(KEY_VOORNAAM, voornaam);
        editor.putString(KEY_EMAIL, email);

        if(remembered) editor.putString(KEY_REMEMBERED_USER, gebruikersnaam);
        else editor.remove(KEY_REMEMBERED_USER);

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
        String rem = pref.getString(KEY_REMEMBERED_USER, null);
        editor.clear();
        if(rem != null) editor.putString(KEY_REMEMBERED_USER, rem);
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


    public String getVoornaam(){
        return pref.getString(KEY_VOORNAAM, null);
    }

    public String getVolledigeNaam(){
        return pref.getString(KEY_NAAM, null) + " " + pref.getString(KEY_VOORNAAM, null);
    }

    public String getHerinnerdeGebruiker(){
        return pref.getString(KEY_REMEMBERED_USER, null);
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
