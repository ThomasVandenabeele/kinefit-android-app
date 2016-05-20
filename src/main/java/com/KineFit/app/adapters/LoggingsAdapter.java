package com.KineFit.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.KineFit.app.R;
import com.KineFit.app.model.Logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * ArrayAdapter voor de ListView voor Loggings.
 *
 * Created by Thomas on 28/04/16.
 * @author Thomas Vandenabeele
 */
public class LoggingsAdapter extends ArrayAdapter<Logging> {

    /** Lijst van loggings */
    private ArrayList<Logging> logs;

    /**
     * Constructor voor de LoggingsAdapter
     * @param context context
     * @param textViewResourceId id van textViewResource
     * @param objects Arraylist<Logging>
     */
    public LoggingsAdapter(Context context, int textViewResourceId, ArrayList<Logging> objects) {
        super(context, textViewResourceId, objects);
        this.logs = objects;
    }

    /**
     * In deze methode leggen we vast hoe ieder item er moet uitzien.
     * @param position positie
     * @param x view
     * @param parent viewGroup
     * @return View
     */
    @Override
    public View getView(int position, View x, ViewGroup parent){

        View v = null;
        
        // Definieer layout
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.logs_list_item, null);

        // Alternerende kleuren voor achtergrond
        if (position % 2 == 1) {
            v.setBackgroundColor(Color.rgb(202, 225, 143));//Color.rgb(246, 164, 134));
        } else {
            v.setBackgroundColor(Color.WHITE);
        }

        Logging l = logs.get(position);

        if (l != null) {

            //region UI componenten toekennen
            TextView id = (TextView) v.findViewById(R.id.id);
            TextView description = (TextView) v.findViewById(R.id.beschrijving);
            TextView time = (TextView) v.findViewById(R.id.tijd);
            TextView date = (TextView) v.findViewById(R.id.datum);
            TextView amount = (TextView) v.findViewById(R.id.hoeveelheid);
            TextView unit = (TextView) v.findViewById(R.id.eenheid);
            TextView sScore = (TextView) v.findViewById(R.id.tScore);
            TextView pScore = (TextView) v.findViewById(R.id.pScore);
            //endregion

            DateFormat df = new SimpleDateFormat("H:mm");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            // Waarden toekennen aan de textViews.
            if (id != null){
                id.setText(String.valueOf(l.getId()));
            }
            if (description != null){
                description.setText(l.getBeschrijving());
            }
            if (time != null){
                time.setText(df.format(l.getTijd()));
            }
            if (date != null){
                amount.setText(sdf.format(l.getDatum()));
            }
            if (unit != null){
                unit.setText(l.getEenheid());
            }
            if (amount != null){
                amount.setText(String.valueOf(l.getHoeveelheid()));
            }
            if (pScore != null){

                int c = Color.BLACK;

                // Kleurschaal toekennen
                if(l.getpScore() >= 0 && l.getpScore() < 3) c = Color.GREEN;
                else if(l.getpScore() > 7 && l.getpScore() <= 10) c = Color.RED;

                pScore.setTextColor(c);
                pScore.setText(String.valueOf(l.getpScore()));

            }
            if (sScore != null){
                int c = Color.BLACK;
                int score = l.gettScore();

                // Kleurschaal toekennen
                if (score >= 0 && score < 3) c = Color.RED;
                else if(score > 7 && score <= 10) c = Color.GREEN;

                sScore.setTextColor(c);
                sScore.setText(String.valueOf(score));
            }

        }

        // de view teruggeven
        return v;

    }

}
