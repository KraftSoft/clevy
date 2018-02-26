package com.clevy.ikravtsov.clevytest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FinalActivity extends AppCompatActivity {

    private PendingIntent pendingEducateIntent, pendingNotifyIntent;
    private Integer currentLessonNumb;
    Button btnStartNow, btnStartWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_name), Context.MODE_PRIVATE);

        currentLessonNumb = sharedPref.getInt("lessonNumb", 0);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("lessonNumb", ++currentLessonNumb);
        editor.apply();


        btnStartNow = (Button) findViewById(R.id.nextTest);
        btnStartWait = (Button) findViewById(R.id.waitForStart);

        if (currentLessonNumb >= 3) {
            editor = sharedPref.edit();
            editor.putInt("lessonNumb", 0);
            editor.apply();
        }

        TextView textCongratulate = (TextView) findViewById(R.id.textCongratulate);
        textCongratulate.setText("МОЛОДЕЦ, ПРАВИЛЬНО!");


        final Context context = this;

        Intent alarmIntent = new Intent(FinalActivity.this, AlarmEducationReceiver.class);
        Intent notifyIntent = new Intent(FinalActivity.this, AlarmNotifyReceiver.class);

        pendingEducateIntent = PendingIntent.getBroadcast(FinalActivity.this, 0, alarmIntent, 0);
        pendingNotifyIntent = PendingIntent.getBroadcast(FinalActivity.this, 0, notifyIntent, 0);

        View.OnClickListener goNext = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStartNow();
            }
        };

        View.OnClickListener delayedStart = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_alarms("Следущее занятие начнется через 15 минут");
                handleFinish();
            }
        };

        btnStartNow.setOnClickListener(goNext);
        btnStartWait.setOnClickListener(delayedStart);
    }

    public  void set_alarms(String message) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        AlarmManager manager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        long interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

        // TODO Do something with KITKAT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            assert manager != null;
            manager.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval,
                    pendingEducateIntent
            );

            assert manager2 != null;
            manager2.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval - 10000,
                    pendingNotifyIntent
            );
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();


    }

    public void handleStartNow() {

        Intent intentEducate = new Intent(this, EducationActivity.class);
        //intentEducate.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        this.startActivity(intentEducate);

        this.finish();
    }


    public void handleFinish() {
        this.finish();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(currentLessonNumb >= 3){
            btnStartWait.setVisibility(View.VISIBLE);

        }
        else {
            btnStartWait.setVisibility(View.INVISIBLE);
            btnStartNow.setText("ПРОДОЛЖИТЬ");
        }

    }

}
