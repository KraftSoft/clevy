package com.clevy.ikravtsov.clevytest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.Collections;

import static com.clevy.ikravtsov.clevytest.AppConstants.TEST_PACKAGE_SIZE;
import static com.clevy.ikravtsov.clevytest.AppConstants.finalMove;
import static com.clevy.ikravtsov.clevytest.AppConstants.successSound;
import static com.clevy.ikravtsov.clevytest.AppConstants.testProgress;

public class FinalActivity extends AppCompatActivity {

    private PendingIntent pendingEducateIntent, pendingNotifyIntent;
    private Integer currentLessonNumb, currentProgress;

    Button btnStartNow, btnStartWait;
    ProgressBar progressBar;
    Boolean isFinishedTest;

    private AssetManager mAssetManager;
    private SoundPool mSoundPool;
    int congratulationsSound;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_name), Context.MODE_PRIVATE);

        currentLessonNumb = sharedPref.getInt("lessonNumb", 0);
        currentProgress = sharedPref.getInt(testProgress, 0);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("lessonNumb", ++currentLessonNumb);
        editor.apply();


        btnStartNow = findViewById(R.id.nextTest);
        btnStartWait = findViewById(R.id.waitForStart);
        progressBar = findViewById(R.id.progress_in_finish);
        progressBar.setMax(TEST_PACKAGE_SIZE);
        progressBar.setProgress(currentProgress);

        mAssetManager = getAssets();


        Collections.shuffle(finalMove);

        VideoView videoView = findViewById(R.id.videoView);

        int moveId = getResources().getIdentifier(finalMove.get(0),
                "raw", getPackageName());

        String a = "android.resource://" + getPackageName() + "/" + moveId;

        Log.println(Log.INFO, "raw_path", a);
        videoView.setVideoURI(Uri.parse(a));

        videoView.setMediaController(null);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        videoView.requestFocus();
        videoView.start();

        context = this;

        TextView textCongratulate = findViewById(R.id.textCongratulate);

        createNewSoundPool();
        Collections.shuffle(successSound);
        congratulationsSound = loadSound(successSound.get(0) + ".mp3");

        Intent previousActivityIntent = getIntent();
        isFinishedTest = previousActivityIntent.getBooleanExtra("finishJustNow", false);

        if (currentLessonNumb >= 3) {
            editor = sharedPref.edit();
            editor.putInt("lessonNumb", 0);
            editor.apply();
        }

        if (isFinishedTest) {
            textCongratulate.setText("ТЕСТ ПРОЙДЕН!");
        }
        else {
            textCongratulate.setText("МОЛОДЕЦ, ПРАВИЛЬНО!");
        }

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


        if (manager != null && manager2 != null) {
            manager.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval,
                    pendingEducateIntent
            );

            manager2.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval - 10000,
                    pendingNotifyIntent
            );


            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Не удалось запустить таймер", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleStartNow() {

        Intent intentEducate = new Intent(this, EducationActivity.class);

        this.startActivity(intentEducate);

        this.finish();
    }


    public void handleFinish() {
        this.finish();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isFinishedTest || currentLessonNumb >= 3){
            try {

                playSound(congratulationsSound);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(currentLessonNumb >= 3){
            btnStartWait.setVisibility(View.VISIBLE);

        }
        else {
            btnStartWait.setVisibility(View.INVISIBLE);
            btnStartNow.setText("ПРОДОЛЖИТЬ");
        }

    }

    private int loadSound(String fileName) {
        AssetFileDescriptor afd;
        try {
            afd = mAssetManager.openFd(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Не могу загрузить файл " + fileName,
                    Toast.LENGTH_SHORT).show();
            return -1;
        }
        return mSoundPool.load(afd, 1);
    }

    private int playSound(final int sound) throws InterruptedException {
        final int[] mStreamID = {0};
        if (sound > 0) {
            SoundPool.OnLoadCompleteListener listener = new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    mStreamID[0] = mSoundPool.play(sound, 1, 1, 1, 0, 1);
                }
            };
            mSoundPool.setOnLoadCompleteListener(listener);
            Log.println(Log.INFO, "sound", "play sound");
        }
        else {
            Log.println(Log.INFO, "sound", "no sound");
        }
        return mStreamID[0];
    }

    private void stopSound(int stramId){
        mSoundPool.stop(stramId);
    }

    private void createNewSoundPool() {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    }


}
