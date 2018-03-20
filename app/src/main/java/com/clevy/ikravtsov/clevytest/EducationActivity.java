package com.clevy.ikravtsov.clevytest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static com.clevy.ikravtsov.clevytest.AppConstants.*;

public final class EducationActivity extends AppCompatActivity {

    Button btnNext, btnSound;

    ImageView image;
    TextView textHint;
    ProgressBar progressBar;

    Integer resID, currentProgress;
    SharedPreferences sharedPref;
    Context context;
    Boolean isTest;

    private AssetManager mAssetManager;
    private SoundPool mSoundPool;
    int fooSound;

    JsonLessonsHelper structHelper;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_educate);

        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_name), Context.MODE_PRIVATE);

            Integer lessonNumber = sharedPref.getInt("lessonNumb", 0);
        if (lessonNumber == 0) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                v.vibrate(300);
            }
        }

        btnNext = findViewById(R.id.next);
        textHint = findViewById(R.id.educateText);
        btnSound = findViewById(R.id.play_sound);

        image = findViewById(R.id.educateImage);
        progressBar = findViewById(R.id.progress_in_educate);

        currentProgress = sharedPref.getInt(testProgress, 0);

        progressBar.setMax(TEST_PACKAGE_SIZE);
        progressBar.setProgress(currentProgress);


        int savedBlockIndex = sharedPref.getInt(keyCurrentBlockIndex, 0);
        int savedWordIndex = sharedPref.getInt(keyCurrentWordIndex, DEFAULT_WORD_INDEX);

        isTest = sharedPref.getBoolean(isTestKey, false);

        structHelper = JsonLessonsHelper.getInstance(this, savedBlockIndex, savedWordIndex);


        mAssetManager = getAssets();
        createNewSoundPool();
        String enWord = structHelper.getCorrectEnWord().replaceAll(" ", "_");
        fooSound = loadSound(enWord + ".mp3");

        resID = structHelper.getResId(this);

        if (isTest) {
            btnSound.setVisibility(View.INVISIBLE);
            textHint.setText("Пора проверять знания");
            btnNext.setText("НАЧАТЬ!");
            image.setBackground(ContextCompat.getDrawable(this, R.drawable.system_lets_start));
        }
        else {
            textHint.setText(structHelper.getCorrectRuWord() + " - " + structHelper.getCorrectEnWord());
            image.setBackgroundResource(resID);
        }

        context = this;

        View.OnClickListener startTest = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentTest = new Intent(context, TestActivity.class);

                if(!isTest){
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(keyCurrentBlockIndex, structHelper.getNewWordTypePos());
                    editor.putInt(keyCurrentWordIndex, structHelper.getNewWordPosition());
                    editor.apply();
                }

                context.startActivity(intentTest);
                finalizeActivity();
            }
        };

        View.OnClickListener makeSound = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    playSound(fooSound);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        btnNext.setOnClickListener(startTest);
        btnSound.setOnClickListener(makeSound);

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

    private int playSound(int sound) throws InterruptedException {
        int mStreamID = 0;
        if (sound > 0) {
            mStreamID = mSoundPool.play(sound, 1, 1, 1, 0, 1);
            Log.println(Log.INFO, "sound", "play sound");
        }
        else {
            Log.println(Log.INFO, "sound", "no sound");
        }
        return mStreamID;
    }

    private void stopSound(int stramId){
        mSoundPool.stop(stramId);
    }

    private void createNewSoundPool() {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    }

    public void finalizeActivity() {
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
