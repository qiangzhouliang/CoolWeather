package com.qzl.coolweather.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.qzl.coolweather.R;
import com.qzl.coolweather.db.CoolWeatherDB;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(this);
    }
}
