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

    private PendingIntent pendingIntent;
    private Integer currentLessonNumb;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        Button btnFinish = (Button) findViewById(R.id.nextTest);
        Button btnClose = (Button) findViewById(R.id.closeOnFinish);
        TextView textCongratulate = (TextView) findViewById(R.id.textCongratulate);
        textCongratulate.setText("МОЛОДЕЦ ПРАВИЛЬНО!");

        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_name), Context.MODE_PRIVATE);

        currentLessonNumb = sharedPref.getInt("lessonNumb", 0);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("lessonNumb", ++currentLessonNumb);
        editor.apply();

        final Context context = this;

        Intent alarmIntent = new Intent(FinalActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(FinalActivity.this, 0, alarmIntent, 0);

        View.OnClickListener goNext = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNext();
            }
        };

        View.OnClickListener closeWin = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFinish();
            }
        };

        btnFinish.setOnClickListener(goNext);
        btnClose.setOnClickListener(closeWin);
    }

    public void handleNext() {
        if (currentLessonNumb >= 3){

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("lessonNumb", 0);
            editor.apply();

            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            int interval = 8000;

            // TODO Do something with KITKAT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                assert manager != null;
                manager.setExact(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES/15/6,
                        pendingIntent
                );
            }

            Toast.makeText(this, "Следующий урок через 15 минут", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intentEducate = new Intent(this, EducationActivity.class);
            this.startActivity(intentEducate);
        }

        this.finish();
    }

    public void handleFinish() {
        this.finish();
    }

}
