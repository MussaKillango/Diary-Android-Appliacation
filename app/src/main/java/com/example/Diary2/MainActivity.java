package com.example.Diary2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;

import com.example.Diary2.login.Login;
import com.example.Diary2.login.Register;


public class MainActivity extends AppCompatActivity {

    CalendarView calendarView;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                SharedPreferences prefs = getSharedPreferences(Register.MY_PREFS_NAME, MODE_PRIVATE);
                boolean isUserRegistered = prefs.getBoolean("userRegistered", false);
                if (isUserRegistered) {
                    startActivity(new Intent(getApplicationContext(),Login.class));
                }
                else{
                    startActivity(new Intent(getApplicationContext(),Register.class));
                }
            }

        });

    }
}

