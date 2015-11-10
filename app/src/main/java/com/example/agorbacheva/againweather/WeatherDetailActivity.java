package com.example.agorbacheva.againweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class WeatherDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String weekday = intent.getStringExtra("WEEKDAY");
        String main = intent.getStringExtra("MAIN");
        int max = intent.getIntExtra("MAX", 0);
        int min = intent.getIntExtra("MIN", 0);
        double speed = intent.getDoubleExtra("speed", 0);

        TextView weatherText = (TextView) findViewById(R.id.toDaytextView);
        weatherText.setText(weekday);

        TextView weatherMain = (TextView) findViewById(R.id.Clouds);
        weatherMain.setText(main);

        TextView weatherMax = (TextView) findViewById(R.id.tempratureToday);
        weatherMax.setText(max + " / "+ min);

    }

}