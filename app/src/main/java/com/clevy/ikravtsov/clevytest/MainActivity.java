package com.clevy.ikravtsov.clevytest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Button btnStart;

    public final int NOTIFY_ID = 101;


    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        btnStart = (Button) findViewById(R.id.button);

        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);

        View.OnClickListener startAction = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        };

        btnStart.setOnClickListener(startAction);

    }

    public void start() {
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

        Toast.makeText(this, "Урок начнется через 15 минут", Toast.LENGTH_SHORT).show();

        this.finish();
    }

}
