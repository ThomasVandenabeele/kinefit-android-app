package com.KineFit.app.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.KineFit.app.activities.LoginActivity;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * SessieManager klasse.
 * Met deze klasse kan men de opgeslagen ingelogde gebruiker ophalen,
 * kijken of er gebruiker is ingelogd en de referenties opvragen.
 * Created by Thomas on 17/05/16.
 */
public class SessieManager {

    //region DATAMEMBERS

    /** SharedPreferences om waarden op te slaan */
    private SharedPreferences instellingen;

    /** Editor om instellingen te veranderen */
    private Editor editor;

    /*+ De huidige context */
    private Context _context;

    /** Instellingen modus: privaat */
    int PRIVATE_MODE = 0;

    //endregion

    //region TAGS

    /** Key voor instellingen naam */
    private static final String KEY_INSTELLINGEN = "KineFitInstellingen";

    /** Key voor boolean is ingelogd */
    private static final String IS_LOGIN = "IsIngelogd";

    /** Key voor gebruikersnaarm */
    private static final String KEY_GEBRUIKERSNAAM = "gebruikersnaam";

    /** Key voor email */
    private static final String KEY_EMAIL = "email";

    /** Key voor naam */
    private static final String KEY_NAAM = "naam";

    /** Key voor voornaam */
    private static final String KEY_VOORNAAM = "voornaam";

    /** Key voor boolean herinner mij */
    private static final String KEY_HERINNER_MIJ = "herinnerGebruiker";

    //endregion

    /**
     * Constructor voor SessieManager.
     * @param context de context
     */
    public SessieManager(Context context){
        this._context = context;

        // Instellingen en editor instantiëren
        instellingen = _context.getSharedPreferences(KEY_INSTELLINGEN, PRIVATE_MODE);
        editor = instellingen.edit();
    }

    /**
     * Maak login sessie aan
     * @param gebruikersnaam de gebruikersnaam
     * @param naam naam van gebruiker
     * @param voornaam voornaam van gebruiker
     * @param email email van gebruiker
     * @param onthouden onthouden of niet
     */
    public void createLoginSession(String gebruikersnaam, String naam, String voornaam, String email, Boolean onthouden){

        System.out.println("Doe login");
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_GEBRUIKERSNAAM, gebruikersnaam);
        editor.putString(KEY_NAAM, naam);
        editor.putString(KEY_VOORNAAM, voornaam);
        editor.putString(KEY_EMAIL, email);

        if(onthouden) editor.putString(KEY_HERINNER_MIJ, gebruikersnaam);
        else editor.remove(KEY_HERINNER_MIJ);

        editor.commit();

        // Start stap service
        startStapService();
    }

    /**
     * Deze methode checkt of er een gebruiker is ingelogd.
     * Indien niet, doorverwezen naar login pagina.
     * Indien wel, gebeurt er niets.
     * */
    public void checkLogin(){

        // Check login status
        if(!this.isLoggedIn() || !isInternetBeschikbaar()){
            // wanneer gebruiker niet is ingelogd --> naar loginpagina!
            startLoginActivity();
        }

    }

    /**
     * Get de ingelogde gebruikersnaam
     * */
    public String getUsername(){
        return instellingen.getString(KEY_GEBRUIKERSNAAM, null);
    }

    /**
     * Log de gebruiker uit
     * */
    public void logoutUser(){
        String rem = instellingen.getString(KEY_HERINNER_MIJ, null);
        editor.clear();
        if(rem != null) editor.putString(KEY_HERINNER_MIJ, rem);
        editor.commit();

        // Bij loguit, terug naar loginactivity!
        startLoginActivity();
    }

    /**
     * Geeft terug of er een gebruiker is ingelogd
     * **/
    public boolean isLoggedIn(){
        return instellingen.getBoolean(IS_LOGIN, false);
    }

    /**
     * Geeft de voornaam van de ingelogde gebruiker terug
     * @return voornaam
     */
    public String getVoornaam(){
        return instellingen.getString(KEY_VOORNAAM, null);
    }

    /**
     * Geeft de volledige naam (naam voornaam) terug van ingelogde gebruiker
     * @return naam voornaam
     */
    public String getVolledigeNaam(){
        return instellingen.getString(KEY_NAAM, null) + " " + instellingen.getString(KEY_VOORNAAM, null);
    }

    /**
     * Heeft de herinnerde gebruikersnaam terug
     * @return herinnerde gebruikersnaam
     */
    public String getHerinnerdeGebruiker(){
        return instellingen.getString(KEY_HERINNER_MIJ, null);
    }

    /**
     * Start loginactivity.
     */
    private void startLoginActivity(){
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Start LoginActivity
        _context.startActivity(i);
    }

    /**
     * Start RegistreerStappen service
     */
    private void startStapService(){
        Intent i = new Intent(_context, RegistreerStappenService.class);
        _context.startService(i);
    }

    /**
     * Geeft terug of er een connectie is met het internet
     * @return true of false
     */
    public boolean isInternetBeschikbaar() {
        ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
