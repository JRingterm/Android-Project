package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapp.Financial.FinancialMainActivity;
import com.example.myapp.Schedule.ScheduleMainActivity;
import com.example.myapp.Timer.TimerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //권한을 동적으로 요청
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CALENDAR}, 1);
        //ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_CALENDAR}, 1);
    }

    public void schedule_btn(View view) {
        Intent intent = new Intent(getApplicationContext(), ScheduleMainActivity.class);
        startActivity(intent);
    }

    public void financial_btn(View view) {
        Intent intent = new Intent(getApplicationContext(), FinancialMainActivity.class);
        startActivity(intent);
    }

    public void timer_btn(View view){
        Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
        startActivity(intent);
    }
}