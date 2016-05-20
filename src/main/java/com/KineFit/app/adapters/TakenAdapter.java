package com.KineFit.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.KineFit.app.R;
import com.KineFit.app.model.Taak;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * ArrayAdapter voor de ListView voor Taken.
 *
 * Created by Thomas on 28/04/16.
 * @author Thomas Vandenabeele
 */
public class TakenAdapter extends ArrayAdapter<Taak> {

    /** Lijst van taken */
    private ArrayList<Taak> taken;

    /**
     * Constructor voor de TakenAdapter
     * @param context context
     * @param textViewResourceId id van textViewResource
     * @param objects Arraylist<Logging>
     */
    public TakenAdapter(Context context, int textViewResourceId, ArrayList<Taak> objects) {
        super(context, textViewResourceId, objects);
        this.taken = objects;
    }

    /**
     * In deze methode leggen we vast hoe ieder item er moet uitzien.
     * @param position positie
     * @param convertView view
     * @param parent viewGroup
     * @return View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View v = convertView;

        if (v == null) {
            // Definieer layout
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.task_list_item, null);

            // Alternerende kleuren voor achtergrond
            if (position % 2 == 1) {
                v.setBackgroundColor(Color.rgb(202, 225, 143));//Color.rgb(246, 164, 134));
            } else {
                v.setBackgroundColor(Color.WHITE);
            }
        }

        Taak l = taken.get(position);

        if (l != null) {

            //region UI componenten toekennen
            TextView id = (TextView) v.findViewById(R.id.task_pid);
            TextView task_name = (TextView) v.findViewById(R.id.task_name);
            TextView task_date = (TextView) v.findViewById(R.id.task_date);
            TextView task_item_status = (TextView) v.findViewById(R.id.task_item_status);
            //endregion

            DateFormat df = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            // Waarden toekennen aan de textViews.
            if (id != null){
                id.setText(String.valueOf(l.getId()));
            }
            if (task_name != null){
                task_name.setText(l.getBericht());
            }
            if (task_date != null){
                task_date.setText(sdf.format(l.getAanmaakDatum()));
            }
            if (task_item_status != null){
                task_name.setTypeface(Typeface.DEFAULT);
                switch (l.getStatus()) {
                    case DONE:
                        task_item_status.setBackgroundColor(Color.rgb(255, 165, 0));
                        break;
                    case FAILED:
                        task_item_status.setBackgroundColor(Color.RED);
                        break;
                    case NEW:
                        task_name.setTypeface(Typeface.DEFAULT_BOLD);
                        task_name.setTextColor(Color.RED);
                        task_item_status.setBackgroundColor(Color.GREEN);
                        String name = task_name.getText().toString();
                        task_name.setText("NEW: " + name);
                        break;
                    case OPEN:
                        task_item_status.setBackgroundColor(Color.GREEN);
                        break;
                }
            }

        }

        // de view teruggeven
        return v;

    }

}
