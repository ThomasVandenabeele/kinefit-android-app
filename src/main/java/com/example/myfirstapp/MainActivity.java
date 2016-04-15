package com.example.myfirstapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.joda.time.LocalTime;

public class MainActivity extends Activity
{
    static final public String TAG = "eerstevoorbeeld";
    private int teller;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        teller = 0;
        Log.d(TAG, "onCreate()");
    }

    @Override
    public void onStart() 
    {
        super.onStart();
        LocalTime currentTime = new LocalTime();
        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText("The current local time is now: " + currentTime);

        teller++;
        Log.d(TAG, "onStart()");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
	// Hoe de toestand bij.
	savedInstanceState.putInt("teller", teller);
    
	// Altijd de superclass oproepen zodat ook de viewhierarchietoestand opgeslagen wordt.
	super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "onSave() " + teller);
    }
}
