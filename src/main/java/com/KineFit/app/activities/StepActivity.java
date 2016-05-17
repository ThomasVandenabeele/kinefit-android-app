package com.KineFit.app.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.KineFit.app.R;
import com.KineFit.app.services.SessionManager;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by Thomas on 17/04/16.
 */
public class StepActivity extends BaseActivity {

    private TextView count;
    boolean activityRunning;

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private Runnable mTimer2;
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private double graph2LastXValue = 5d;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step);

        count = (TextView) findViewById(R.id.textview);

        Calendar calendar = Calendar.getInstance();
        java.util.Date d1 = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        //calendar.add(Calendar.DATE, 1);
        java.util.Date d2 = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        java.util.Date d3 = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        java.util.Date d4 = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        java.util.Date d5 = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        java.util.Date d6 = calendar.getTime();

        GraphView graph = (GraphView) findViewById(R.id.graphSteps);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(d1, 1),
                new DataPoint(d2, 5),
                new DataPoint(d3, 3),
                new DataPoint(d4, 3),
                new DataPoint(d5, 7),
                new DataPoint(d6, 1)
        });
        graph.addSeries(series);
        series.setColor(Color.rgb(241, 104, 54));

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(StepActivity.this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);

        final DateFormat dateTimeFormatter = DateFormat.getDateTimeInstance();
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
        {
            @Override
            public String formatLabel(double value, boolean isValueX)
            {
                if (isValueX)
                {
                    SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
                    //Date date = formatDate.format((long) value);
                    String date = formatDate.format(new Date((long) value));
                    String time = formatTime.format(new Date((long) value));
                    return "\n" + time + "\n" + date;
                }
                return super.formatLabel(value, isValueX); // let graphview generate Y-axis label for us
            }
        });

        //graph.getViewport().setScrollable(true);

        // set manual x bounds to have nice steps

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        graph.getViewport().setMinX(cal.getTime().getTime());

        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        graph.getViewport().setMaxX(cal.getTime().getTime());
        graph.getViewport().setXAxisBoundsManual(true);


        graph.getViewport().setMinY((int) 0);
        graph.getViewport().setMaxY((int) 10);
        graph.getViewport().setYAxisBoundsManual(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
    }

    private DataPoint[] generateData() {
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }

    double mLastRandom = 2;
    Random mRand = new Random();
    private double getRandom() {
        return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
    }




}
