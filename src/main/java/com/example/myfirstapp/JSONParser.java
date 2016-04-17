package com.example.myfirstapp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.util.Log;

import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Created by Thomas on 15/04/16.
 */
public class JSONParser {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    // function get json from url
    // by making HTTP POST or GET method
    public JSONObject makeHttpRequest(String url, String method,
                                      ContentValues params) {

        // Making HTTP request
        try {

            StringBuilder parameters =new StringBuilder("");
            String result="";
            boolean first = true;
            try {
                for(String s:params.keySet()){
                    if(first) {
                        first = false;
                        parameters.append(s+"=");
                    }
                    else {
                        parameters.append("&"+s+"=");
                    }
                    parameters.append(URLEncoder.encode(params.getAsString(s), "UTF-8"));
                }
                URL url_db = new URL(url);

            // check for request method
            if(method == "POST"){
                // request method is POST
                // defaultHttpClient
                HttpURLConnection con = (HttpURLConnection) url_db.openConnection();
                //con.setReadTimeout(15000);
                //con.setConnectTimeout(15000);
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);

                OutputStream os = new BufferedOutputStream(con.getOutputStream());
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(parameters.toString());

                writer.flush();
                writer.close();
                os.close();


                int responseCode = con.getResponseCode();
                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + params);
                System.out.println("Response Code : " + responseCode);



                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine + "\n");
                }
                in.close();
                result = response.toString();
                con.disconnect();
            }else if(method == "GET"){
                URL url_dbg = new URL(url+"?"+parameters);
                HttpURLConnection con = (HttpURLConnection) url_dbg.openConnection();
                //con.setReadTimeout(15000);
                //con.setConnectTimeout(15000);
                //con.setRequestMethod("GET");
                //con.setDoInput(true);
                con.setDoOutput(true);
                //for (Map.Entry<String, Object> entry : params.valueSet()) {
                    //con.setRequestProperty(entry.getKey(), entry.getValue().toString());
                //}


                //int responseCode = con.getResponseCode();
                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Post parameters : " + params);
                //System.out.println("Response Code : " + responseCode);



                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine + "\n");
                }
                in.close();
                result = response.toString();
                con.disconnect();

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        finally {

                try {
                    json = result;
                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }

                // try parse the string to a JSON object
                try {
                    jObj = new JSONObject(json);
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }


        }
    }
        finally {
            // return JSON String
            return jObj;
        }
}
}
