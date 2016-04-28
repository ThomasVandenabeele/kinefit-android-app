package com.example.myfirstapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Thomas on 28/04/16.
 */
public class DiaryActivity extends Activity {

    private TextView currentDate;
    private TextView newLogBtn;
    private CalendarView calendar;
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE d MMMM yyyy", new Locale("nl_NL"));

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary);
        currentDate = (TextView) findViewById(R.id.currentDate);
        newLogBtn = (TextView) findViewById(R.id.newLogBtn);
        calendar = (CalendarView) findViewById(R.id.calendar);
        currentDate.setText(sdf.format(new Date(calendar.getDate())));

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                //Toast.makeText(getApplicationContext(), ""+dayOfMonth, Toast.LENGTH_SHORT).show();// TODO Auto-generated method stub
                currentDate.setText(sdf.format(new Date(view.getDate())));
            }
        });

    }





}
