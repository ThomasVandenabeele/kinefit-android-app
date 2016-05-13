package com.example.myfirstapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Thomas on 22/04/16.
 */
public class DashboardActivity extends Activity {

    Button btnSteps;
    Button btnTasks;
    Button btnLoggings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        launchTestService();
        btnSteps = (Button) findViewById(R.id.btnSteps);
        btnTasks = (Button) findViewById(R.id.btnTasks);
        btnLoggings = (Button) findViewById(R.id.btnLoggings);

        btnSteps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), StepActivity.class);
                startActivity(i);

            }
        });

        btnTasks.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), TaskActivity.class);
                startActivity(i);

            }

        });

        btnLoggings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), DiaryActivity.class);
                startActivity(i);

            }

        });

    }

    public void launchTestService() {
        // Construct our Intent specifying the Service
        Intent i = new Intent(this, RegisterStepsService.class);
        // Add extras to the bundle
        i.putExtra("foo", "bar");
        // Start the service
        startService(i);
    }

}
