package com.example.myfirstapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Thomas on 28/04/16.
 */
public class TasksAdapter extends ArrayAdapter<Task> {

    // declaring our ArrayList of items
    private ArrayList<Task> tasks;

    /* here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Item> objects,
    * because it is the list of objects we want to display.
    */
    public TasksAdapter(Context context, int textViewResourceId, ArrayList<Task> objects) {
        super(context, textViewResourceId, objects);
        this.tasks = objects;
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
            v = inflater.inflate(R.layout.task_list_item, null);
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
        Task l = tasks.get(position);

        if (l != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            TextView id = (TextView) v.findViewById(R.id.pid);
            TextView task_name = (TextView) v.findViewById(R.id.task_name);
            TextView task_date = (TextView) v.findViewById(R.id.task_date);
            TextView task_item_status = (TextView) v.findViewById(R.id.task_item_status);

            DateFormat df = new SimpleDateFormat("H:mm");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //String t = String.valueOf(l.getId());
            // check to see if each individual textview is null.
            // if not, assign some text!
            if (id != null){
                id.setText(String.valueOf(l.getId()));
            }
            if (task_name != null){
                task_name.setText(l.getMessage());
            }
            if (task_date != null){
                task_date.setText(sdf.format(l.getCreate_date()));
            }
            if (task_item_status != null){
                task_name.setTypeface(Typeface.DEFAULT);
                switch (l.getStatus()) {
                    case DONE:
                        task_item_status.setBackgroundColor(Color.GREEN);
                        break;
                    case FAILED:
                        task_item_status.setBackgroundColor(Color.RED);
                        break;
                    case NEW:
                        task_name.setTypeface(Typeface.DEFAULT_BOLD);
                        task_item_status.setBackgroundColor(Color.rgb(255, 165, 0));
                        break;
                    case OPEN:
                        task_item_status.setBackgroundColor(Color.rgb(255, 165, 0));
                        break;
                }
            }

        }

        // the view must be returned to our activity
        return v;

    }

}
