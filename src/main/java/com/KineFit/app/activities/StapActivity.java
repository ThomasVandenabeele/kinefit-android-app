package com.KineFit.app.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.KineFit.app.R;
import com.KineFit.app.model.Stap;
import com.KineFit.app.services.JSONParser;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Activity voor het stappen scherm.
 * Op deze activity kunnen de gebruikers hun stap-statistieken raadplegen.
 *
 * Created by Thomas on 17/04/16.
 * @author Thomas Vandenabeele
 */
public class StapActivity extends BasisActivity {

    //region DATAMEMBERS

    /** ProgressDialog voor de UI */
    private ProgressDialog pDialog;

    /** GraphView voor de stappen weer te geven in grafiek */
    private GraphView stapGrafiek;

    /** TextView voor het aantal stappen deze week */
    private TextView selectedWeekSteps;

    /** TextView voor het totaal aantal stappen */
    private TextView totalSteps;

    /** TextView voor het aantal stappen deze week */
    private TextView stepWeekCount;

    /** TextView voor het aantal stappen vandaag */
    private TextView stepDayCount;

    /** Button voor week terug te scrollen */
    private Button weekTerug;

    /** Button voor week verder te scrollen */
    private Button weekVerder;

    /** Het geselecteerde weeknummer */
    private int weekNummer;

    /** Het geselecteerde jaar */
    private int jaar;

    /** SQL datum formatter */
    private SimpleDateFormat sqlDatumFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** Korte datum formatter */
    private SimpleDateFormat korteDatum = new SimpleDateFormat("dd-MM-yyyy");

    /** Korte tijd formatter */
    private SimpleDateFormat korteTijd = new SimpleDateFormat("HH:mm");

    //endregion

    //region REST: TAGS & URL

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    /** Tag voor gebruikersnaam */
    private static final String TAG_GEBRUIKERSNAAM = "username";

    //-------------------------------------------------------------------------------------------------------------------------------//

    /** URL om alle stappen op te vragen tussen 2 data */
    private static String url_get_stappen_tussen_data = "http://thomasvandenabeele.no-ip.org/KineFit/get_steps_between_datetimes.php";

    /** Tag om de startdatum in te stellen */
    private static final String TAG_URL_STARTDATUM = "startdate";

    /** Tag om de einddatum in te stellen */
    private static final String TAG_URL_EINDDATUM = "enddate";

    /** Tag voor succes-waarde */
    private static final String TAG_SUCCESS = "success";

    /** Tag voor stappen */
    private static final String TAG_STAPPEN = "steps";

    /** Tag voor stap id */
    private static final String TAG_ID = "id";

    /** Tag voor stap aantal */
    private static final String TAG_AANTALSTAPPEN = "number_of_steps";

    /** Tag voor stap starttijd */
    private static final String TAG_STARTTIJD = "start_time";

    /** Tag voor stap eindtijd */
    private static final String TAG_EINDTIJD = "end_time";

    //------------------------------------------------------------------------------------------------------------------------------//

    /** URL om huidige stapinformatie te verkrijgen */
    private static String url_get_stap_info = "http://thomasvandenabeele.no-ip.org/KineFit/get_step_info.php";

    /** Tag voor totaal aantal stappen */
    private static final String TAG_TOTAALSTAPPEN = "totaalStappen";

    /** Tag voor aantal stappen deze week */
    private static final String TAG_STAPPENDEZEWEEK = "stappenDezeWeek";

    /** Tag voor aantal stappen vandaag */
    private static final String TAG_STAPPENVANDAAG = "stappenVandaag";

    //------------------------------------------------------------------------------------------------------------------------------//

    //endregion

    /**
     * Methode die opgeroepen wordt bij aanmaak activity.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stap);

        //region UI componenten toekennen
        totalSteps = (TextView) findViewById(R.id.totalSteps);
        stepWeekCount = (TextView) findViewById(R.id.stepWeekCount);
        stepDayCount = (TextView) findViewById(R.id.stepDayCount);
        selectedWeekSteps = (TextView) findViewById(R.id.selectedWeekSteps);
        stapGrafiek = (GraphView) findViewById(R.id.graphSteps);
        weekTerug = (Button) findViewById(R.id.weekTerugBtn);
        weekVerder = (Button) findViewById(R.id.weekVerderBtn);
        //endregion

        // Juiste instellingen voor grafiek
        stapGrafiek.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(StapActivity.this));
        stapGrafiek.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space
        stapGrafiek.getViewport().setScrollable(true);
        stapGrafiek.getViewport().setScalable(true);

        stapGrafiek.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
        {
            @Override
            public String formatLabel(double value, boolean isValueX)
            {
                if (isValueX)
                {
                    // Formatteren van datum labels
                    String date = korteDatum.format(new Date((long) value));
                    String time = korteTijd.format(new Date((long) value));

                    return "\n" + time + "\n" + date;
                }
                return super.formatLabel(value, isValueX);
            }
        });

        stapGrafiek.getGridLabelRenderer().setVerticalAxisTitle("Stappen per uur");

        // Stel week en jaarnummer in op huidige week
        weekNummer = new GregorianCalendar().get(Calendar.WEEK_OF_YEAR);
        jaar = new GregorianCalendar().get(Calendar.YEAR);

        // Laad de grafiek in
        updateGrafiek();

        // Laad de stap informatie
        ContentValues para = new ContentValues();
        para.put(TAG_GEBRUIKERSNAAM, sessie.getUsername());
        new GetStapInformatie().execute(para);

        // OnClickListener voor weekTerug
        weekTerug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekVerder.setEnabled(true);

                // Stel juiste week en jaar in + herlaad grafiek
                setJuisteWeekJaar(-1);
            }
        });

        // OnClickListener voor weekVerder
        weekVerder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Stel juiste week en jaar in + herlaad grafiek
                setJuisteWeekJaar(1);

                Calendar calendar = Calendar.getInstance();
                if(weekNummer==calendar.get(Calendar.WEEK_OF_YEAR) && jaar==calendar.get(Calendar.YEAR)){
                    weekVerder.setEnabled(false);
                }
                else{
                    weekVerder.setEnabled(true);
                }
            }
        });

    }

    /**
     * Methode om juiste data weer te geven in de grafiek
     * @param verschil int aantal weken verder (>0), aantal weken terug (<0)
     */
    public void setJuisteWeekJaar(int verschil){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, weekNummer);
        calendar.set(Calendar.YEAR, jaar);
        calendar.add(Calendar.WEEK_OF_YEAR, verschil);
        weekNummer = calendar.get(Calendar.WEEK_OF_YEAR);
        jaar = calendar.get(Calendar.YEAR);

        // Laad de grafiek opnieuw met nieuwe data
        updateGrafiek();
    }

    /**
     * Update de grafiek met de juiste data van server
     */
    public void updateGrafiek(){
        ContentValues cv = new ContentValues();
        cv.put(TAG_GEBRUIKERSNAAM, sessie.getUsername());
        cv.put(TAG_URL_STARTDATUM, getStartVanWeek(weekNummer, jaar));
        cv.put(TAG_URL_EINDDATUM, getEindeVanWeek(weekNummer, jaar));

        new LaadAlleStappen().execute(cv);
    }

    /**
     * Geeft de start van de week terug als string.
     * @param week int weeknummer
     * @param jaar int jaar
     * @return start van de opgegeven week
     */
    public String getStartVanWeek(int week, int jaar){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.YEAR, jaar);

        java.util.Date start = calendar.getTime();
        return sqlDatumFormatter.format(start);
    }

    /**
     * Geeft het einde van de week terug als string.
     * @param week int weeknummer
     * @param jaar int jaar
     * @return einde van de opgegeven week
     */
    public String getEindeVanWeek(int week, int jaar){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.YEAR, jaar);

        calendar.add(Calendar.DATE, 7);
        java.util.Date einde = calendar.getTime();
        return sqlDatumFormatter.format(einde);
    }

    /**
     * Async Taak op achtergrond om informatie header op te vragen van stappen.
     * Via HTTP Request naar REST client.
     * */
    class GetStapInformatie extends AsyncTask<ContentValues, String, JSONObject> {

        /**
         * Deze methode wordt in de achtergrond uitgevoerd.
         * @param params ContentValues voor de REST client.
         * @return JSONObject met de informatie
         */
        protected JSONObject doInBackground(ContentValues... params) {

            // Maakt de request en geeft het resultaat
            JSONObject json = jParser.makeHttpRequest(url_get_stap_info, "GET", params[0]);
            Log.d("Stappen info: ", json.toString());

            try {
                if (json.getInt(TAG_SUCCESS) == 1) {
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Methode voor na uitvoering taak.
         * Update de UI door de opgevraagde informatie weer te geven.
         * **/
        protected void onPostExecute(JSONObject json) {

            try {
                if(json != null){
                    totalSteps.setText(Integer.toString(json.getInt(TAG_TOTAALSTAPPEN)));
                    stepWeekCount.setText(Integer.toString(json.getInt(TAG_STAPPENDEZEWEEK)));
                    stepDayCount.setText(Integer.toString(json.getInt(TAG_STAPPENVANDAAG)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    /**
     * Async Taak op achtergrond om stappen van de opgegeven week op te halen.
     * Via HTTP Request naar REST client.
     * */
    class LaadAlleStappen extends AsyncTask<ContentValues, String, ArrayList<Stap>> {

        /** SQL start datum string */
        private String startDatum;

        /** SQL eind datum string */
        private String eindDatum;

        /**
         * Methode die opgeroepen wordt voor uitvoeren van taak.
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(StapActivity.this);
            pDialog.setMessage("Grafiek laden, gelieve te wachten...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Deze methode wordt in de achtergrond uitgevoerd.
         * @param params ContentValues voor de REST client.
         * @return ArrayList<Stap> Lijst van de stappen.
         */
        protected ArrayList<Stap> doInBackground(ContentValues... params) {

            startDatum = params[0].getAsString(TAG_URL_STARTDATUM);
            eindDatum = params[0].getAsString(TAG_URL_EINDDATUM);

            // Maakt de request en geeft het resultaat
            JSONObject json = jParser.makeHttpRequest(url_get_stappen_tussen_data, "GET", params[0]);
            Log.d("Opgehaalde stappen: ", json.toString());

            // Lege stappen lijst
            ArrayList<Stap> stapLijst = new ArrayList<>();
            try {
                if (json.getInt(TAG_SUCCESS) == 1) {

                    // Stappen ophalen
                    JSONArray stappenJson = json.getJSONArray(TAG_STAPPEN);

                    // Omzetten in ArrayList<Stap>
                    for (int i = 0; i < stappenJson.length(); i++) {
                        JSONObject c = stappenJson.getJSONObject(i);
                        Stap s = new Stap(  c.getInt(TAG_ID),
                                            c.getInt(TAG_AANTALSTAPPEN),
                                            new java.sql.Date(sqlDatumFormatter.parse(c.getString(TAG_STARTTIJD)).getTime()),
                                            new java.sql.Date(sqlDatumFormatter.parse(c.getString(TAG_EINDTIJD)).getTime()));
                        stapLijst.add(s);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Geef de lijst terug
            return stapLijst;
        }

        /**
         * Methode voor na uitvoering taak.
         * Update de UI door de grafiek met de juiste data te laden.
         * **/
        protected void onPostExecute(ArrayList<Stap> staps) {
            // pDialog sluiten
            pDialog.dismiss();

            // Seconden verwijderen, daarna alle stappen samentellen per minuut.
            // TreeMap sorteert automatisch op datum.
            Map<Date, Integer> punten = new TreeMap<Date, Integer>();

            for(Stap s : staps){
                Calendar kalender = Calendar.getInstance();
                kalender.setTime(s.getGemDateTime());
                kalender.set(Calendar.MINUTE, 0);
                kalender.set(Calendar.SECOND, 0);
                kalender.set(Calendar.MILLISECOND, 0);

                Date d = new java.sql.Date(kalender.getTime().getTime());
                if(punten.containsKey(d)){
                    int aantal = punten.get(d);
                    punten.remove(d);
                    punten.put(d, aantal+s.getAantalStappen());
                }
                else{
                    punten.put(d, s.getAantalStappen());
                }
            }

            int maxAantalStappen=0;
            DataPoint[] waarden = new DataPoint[punten.size()];

            // Datapunten maken van stappen + max aantal stappen bepalen
            int i = 0;
            for(Map.Entry<Date, Integer> e : punten.entrySet()){
                if(e.getValue() > maxAantalStappen) maxAantalStappen = e.getValue();

                DataPoint dp = new DataPoint(e.getKey(), e.getValue());
                waarden[i] = dp;
                i++;
            }

            // Grenzen vastleggen voor X én Y
            try {
                stapGrafiek.getViewport().setMinX(new Date(sqlDatumFormatter.parse(startDatum).getTime()).getTime());
                stapGrafiek.getViewport().setMaxX(new Date(sqlDatumFormatter.parse(eindDatum).getTime()).getTime());
                stapGrafiek.getViewport().setXAxisBoundsManual(true);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(staps.size()>0) {
                stapGrafiek.getViewport().setMinY(0);
                stapGrafiek.getViewport().setMaxY(maxAantalStappen+5);
                stapGrafiek.getViewport().setYAxisBoundsManual(true);
            }

            // Data serie toekennen aan grafiek
            stapGrafiek.removeAllSeries();
            System.out.println("verwijderd " + punten.size());
            if(punten.size()>0){
                BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(waarden);
                stapGrafiek.addSeries(series);
                series.setSpacing(10);
                series.setDrawValuesOnTop(true);
                series.setValuesOnTopColor(Color.RED);
                series.setColor(Color.rgb(241, 104, 54));
            }

            // Geselecteerde week instellen in UI
            try{
                selectedWeekSteps.setText("Week " + weekNummer + "\n("
                        + korteDatum.format(sqlDatumFormatter.parse(startDatum))
                        + " - "
                        + korteDatum.format(sqlDatumFormatter.parse(eindDatum)) + ")");
            } catch (ParseException e){
                e.printStackTrace();
            }

        }

    }


    public Map.Entry<Date, Integer> getEntry(Map<Date, Integer> map, int i)
    {
        Set<Map.Entry<Date, Integer>> entries = map.entrySet();
        int j = 0;

        for(Map.Entry<Date, Integer> entry : entries)
            if(j++ == i) return entry;

        return null;

    }

}
