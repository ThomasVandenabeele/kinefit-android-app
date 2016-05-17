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
 * Created by Thomas on 28/04/16.
 */
public class LoggingsAdapter extends ArrayAdapter<Logging> {

    // declaring our ArrayList of items
    private ArrayList<Logging> logs;

    /* here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Item> objects,
    * because it is the list of objects we want to display.
    */
    public LoggingsAdapter(Context context, int textViewResourceId, ArrayList<Logging> objects) {
        super(context, textViewResourceId, objects);
        this.logs = objects;
    }

    /*
     * we are overriding the getView method here - this is what defines how each
     * list item will look.
     */
    public View getView(int position, View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.logs_list_item, null);
            if (position % 2 == 1) {
                v.setBackgroundColor(Color.rgb(202, 225, 143));//Color.rgb(246, 164, 134));
            } else {
                v.setBackgroundColor(Color.WHITE);
            }
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        Logging l = logs.get(position);

        if (l != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            TextView id = (TextView) v.findViewById(R.id.pid);
            TextView description = (TextView) v.findViewById(R.id.description);
            TextView time = (TextView) v.findViewById(R.id.time);
            TextView date = (TextView) v.findViewById(R.id.date);
            TextView amount = (TextView) v.findViewById(R.id.amount);
            TextView unit = (TextView) v.findViewById(R.id.unit);
            TextView sScore = (TextView) v.findViewById(R.id.sScore);
            TextView pScore = (TextView) v.findViewById(R.id.pScore);

            DateFormat df = new SimpleDateFormat("H:mm");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //String t = String.valueOf(l.getId());
            // check to see if each individual textview is null.
            // if not, assign some text!
            if (id != null){
                id.setText(String.valueOf(l.getId()));
            }
            if (description != null){
                description.setText(l.getDescription());
            }
            if (time != null){
                time.setText(df.format(l.getTime()));
            }
            if (date != null){
                amount.setText(sdf.format(l.getDate()));
            }
            if (unit != null){
                unit.setText(l.getUnit());
            }
            if (amount != null){
                amount.setText(String.valueOf(l.getAmount()));
            }
            if (pScore != null){

                int c = Color.BLACK;

                if(l.getpScore() >= 0 && l.getpScore() < 3) c = Color.GREEN;
                else if(l.getpScore() > 7 && l.getpScore() <= 10) c = Color.RED;

                pScore.setTextColor(c);
                pScore.setText(String.valueOf(l.getpScore()));

            }
            if (sScore != null){
                int c = Color.BLACK;
                int score = l.getsScore();

                if (score >= 0 && score < 3) c = Color.RED;
                else if(score > 7 && score <= 10) c = Color.GREEN;

                sScore.setTextColor(c);
                sScore.setText(String.valueOf(score));
            }

        }

        // the view must be returned to our activity
        return v;

    }

}
