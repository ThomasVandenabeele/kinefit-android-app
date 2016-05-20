package com.KineFit.app.services;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.util.Log;

import java.net.HttpURLConnection;

/**
 * JSON parser om data te verzamelen en GET/POST te maken naar REST client.
 *
 * Created by Thomas on 15/04/16.
 * @author Thomas Vandenabeele
 */
public class JSONParser {

    //region DATAMEMBERS

    /** JSONObject voor de opgehaalde data */
    private static JSONObject jObj = null;

    /** */
    private static String jsonResultaat = "";

    //endregion

    /**
     * Constructor voor JSON parser
     */
    public JSONParser() {
        // Niets te doen hier
    }

    /**
     * Maakt HTTP POST of GET en geeft het resultaat terug als JSONObject
     * @param url REST url
     * @param methode GET of POST
     * @param params ContentValues met parameters
     * @return de opgehaalde data als JSONobject
     */
    public JSONObject makeHttpRequest(String url, String methode, ContentValues params) {

        HttpURLConnection connectie = null;

        StringBuilder parameters = new StringBuilder("");
        String result = "";
        int responseCode = 0;
        boolean first = true;

        JSONObject jsonObject = null;

        try {
            // Bouw parameterstring op + encode
            for (String s : params.keySet()) {
                if (first) {
                    first = false;
                    parameters.append(s + "=");
                } else {
                    parameters.append("&" + s + "=");
                }
                parameters.append(URLEncoder.encode(params.getAsString(s), "UTF-8"));
            }

            // Maak URL object van url
            URL url_db = new URL(url);

            // Check soort request (post/get)
            if (methode == "POST") {
                // Open nieuwe connectie
                connectie = (HttpURLConnection) url_db.openConnection();
                connectie.setRequestMethod("POST");
                connectie.setReadTimeout(15000);
                connectie.setConnectTimeout(15000);
                connectie.setDoInput(true);
                connectie.setDoOutput(true);

                // Writer om de parameter string mee te geven
                OutputStream os = new BufferedOutputStream(connectie.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(parameters.toString());
                writer.flush();
                writer.close();
                os.close();

                // Response code ophalen
                responseCode = connectie.getResponseCode();

                // Print info in console
                System.out.println("POST request naar: " + url);

            } else if (methode == "GET") {
                url_db = new URL(url + "?" + parameters);
                connectie = (HttpURLConnection) url_db.openConnection();
                connectie.setRequestMethod("GET");
                connectie.setReadTimeout(15000);
                connectie.setConnectTimeout(15000);
                connectie.setDoOutput(true);

                // Response code ophalen
                responseCode = connectie.getResponseCode();

                // Print info in console
                System.out.println("GET request naar: " + url);

            }

            // Indien gelukt haal data op
            if(responseCode == 200){
                // Print info in de console
                System.out.println("Post parameters: " + params);
                System.out.println("Response Code: " + responseCode);

                // Haal het antwoord van POST request op
                BufferedReader in = new BufferedReader(new InputStreamReader(connectie.getInputStream()));
                String inputLine;
                StringBuffer antwoord = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    antwoord.append(inputLine + "\n");
                }
                in.close();

                // Schrijf het resultaat als String
                result = antwoord.toString();
                System.out.println(result);

                // Sluit connectie
                connectie.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            // Probeer input string om te zetten naar JSONobject
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // return JSON Object
        return jsonObject;
    }

}
