package com.clevy.ikravtsov.clevytest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static com.clevy.ikravtsov.clevytest.AppConstants.TEST_PACKAGE_SIZE;
import static com.clevy.ikravtsov.clevytest.AppConstants.errorsSound;
import static com.clevy.ikravtsov.clevytest.AppConstants.isTestKey;
import static com.clevy.ikravtsov.clevytest.AppConstants.keyCurrentWordIndex;
import static com.clevy.ikravtsov.clevytest.AppConstants.keyCurrentBlockIndex;
import static com.clevy.ikravtsov.clevytest.AppConstants.testProgress;
import static com.clevy.ikravtsov.clevytest.AppConstants.testWordPosKey;
import static com.clevy.ikravtsov.clevytest.AppConstants.testBlockPosKey;


public class TestActivity extends AppCompatActivity {

    ImageView testImg;

    Button firstBtn;
    Button secondBtn;
    Button thirdBtn;
    Button fourthBtn;
    TextView testText;
    ProgressBar progressBar;

    Integer wordIndex, blockIndex, testBlockIndex, testWordIndex, currentProgress;
    SharedPreferences sharedPref;
    Boolean isTest;
    JsonLessonsHelper structHelper;

    private AssetManager mAssetManager;
    private SoundPool mSoundPool;
    int fooSound;


    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_test);

        final Context context = this;

        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_name), Context.MODE_PRIVATE);

        isTest = sharedPref.getBoolean(isTestKey, false);
        currentProgress = sharedPref.getInt(testProgress, 0);

        progressBar = findViewById(R.id.progress_in_test);
        progressBar.setMax(TEST_PACKAGE_SIZE);
        progressBar.setProgress(currentProgress);

        testBlockIndex = sharedPref.getInt(testBlockPosKey, 0);
        testWordIndex = sharedPref.getInt(testWordPosKey, 0);

        blockIndex = sharedPref.getInt(keyCurrentBlockIndex, 0);
        wordIndex = sharedPref.getInt(keyCurrentWordIndex, 0);

        if(isTest){
            structHelper = JsonLessonsHelper.getInstance(context, testBlockIndex, testWordIndex);
        }
        else {
            structHelper = JsonLessonsHelper.getInstance(context, blockIndex, wordIndex);
        }

        final String correctWordEn = structHelper.getCorrectEnWord();
        final String correctWordRu = structHelper.getCorrectRuWord();

        mAssetManager = getAssets();
        createNewSoundPool();
        Collections.shuffle(errorsSound);
        fooSound = loadSound(errorsSound.get(0) + ".mp3");

        final int resId = structHelper.getResId(this);


        ArrayList<String> wordsList = structHelper.getEnWordsList();


        ArrayList<String> chooseList = getVariants(wordsList, correctWordEn);

        testImg = findViewById(R.id.testImage);
        testImg.setBackgroundResource(resId);

        testText = findViewById(R.id.testText);
        testText.setText(correctWordRu);

        firstBtn = findViewById(R.id.first);
        firstBtn.setText(chooseList.get(0));

        secondBtn = findViewById(R.id.second);
        secondBtn.setText(chooseList.get(1));

        thirdBtn = findViewById(R.id.third);
        thirdBtn.setText(chooseList.get(2));

        fourthBtn = findViewById(R.id.fourth);
        fourthBtn.setText(chooseList.get(3));

        View.OnClickListener handleEducateMode = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                b.setEnabled(false);
                handleAnswerEducateMode(context, b, correctWordEn);
                b.setEnabled(true);
            }
        };

        View.OnClickListener handleTestMode = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                b.setEnabled(false);
                handleAnswerTestMode(context, b, correctWordEn);
                b.setEnabled(true);
            }
        };

        if (isTest) {
            firstBtn.setOnClickListener(handleTestMode);
            secondBtn.setOnClickListener(handleTestMode);
            thirdBtn.setOnClickListener(handleTestMode);
            fourthBtn.setOnClickListener(handleTestMode);
        }
        else {
            firstBtn.setOnClickListener(handleEducateMode);
            secondBtn.setOnClickListener(handleEducateMode);
            thirdBtn.setOnClickListener(handleEducateMode);
            fourthBtn.setOnClickListener(handleEducateMode);
        }
    }


    public void handleAnswerEducateMode(Context context, Button b, String correctWordEn){

        if (b.getText().equals(correctWordEn)) {

            b.setBackgroundResource(R.color.CustomcAccent);
            b.setTextColor(Color.WHITE);

            JsonLessonsHelper structHelper = JsonLessonsHelper.getInstance(context, blockIndex, wordIndex);
            structHelper.upgradePositions();

            blockIndex = structHelper.getNewWordTypePos();
            wordIndex = structHelper.getNewWordPosition();

            if (structHelper.isStartNewPack() || wordIndex >= testWordIndex + TEST_PACKAGE_SIZE) {
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putBoolean(isTestKey, true);
                editor.putInt(testProgress, 0);

                editor.putInt(keyCurrentBlockIndex, structHelper.getNewWordTypePos());
                editor.putInt(keyCurrentWordIndex, structHelper.getNewWordPosition());

                editor.apply();

                Intent intentStartTest = new Intent(context, EducationActivity.class);
                intentStartTest.putExtra("startJustNow", true);

                context.startActivity(intentStartTest);
                finalizeActivity();
            }

            else {

                saveEducateState(blockIndex, wordIndex, currentProgress);

                Intent intentFinal = new Intent(context, FinalActivity.class);
                context.startActivity(intentFinal);

                finalizeActivity();
            }
        }
        else {
            wrongAnswer(this);
        }
    }

    public void handleAnswerTestMode(Context context, Button b, String correctWordEn){
        if (b.getText().equals(correctWordEn)) {

            b.setBackgroundResource(R.color.CustomcAccent);
            b.setTextColor(Color.WHITE);

            structHelper.upgradePositions();

            testBlockIndex = structHelper.getNewWordTypePos();
            testWordIndex = structHelper.getNewWordPosition();

            saveTestState(structHelper.getNewWordTypePos(), structHelper.getNewWordPosition(), currentProgress);

            if (structHelper.isStartNewPack() || testWordIndex >= wordIndex) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(isTestKey, false);
                editor.putInt(testProgress, 0);
                editor.putInt(testBlockPosKey, structHelper.getNewWordTypePos());
                editor.putInt(testWordPosKey, structHelper.getNewWordPosition());
                editor.apply();

                Intent intentFinal = new Intent(context, FinalActivity.class);
                intentFinal.putExtra("finishJustNow", true);

                context.startActivity(intentFinal);
                finalizeActivity();
            }
            else {

                Intent intentNextTest = new Intent(context, TestActivity.class);

                context.startActivity(intentNextTest);

                finalizeActivity();
            }

        }
        else {
            wrongAnswer(this);
        }
    }

    private void saveEducateState(int wordTypeIndex, int currentWordIndex, int currentProgress) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(keyCurrentBlockIndex, wordTypeIndex);
        editor.putInt(keyCurrentWordIndex, currentWordIndex);
        editor.putInt(testProgress, ++currentProgress);
        editor.apply();
    }

    private void saveTestState(int wordTypeIndex, int currentWordIndex, int currentProgress) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(testBlockPosKey, wordTypeIndex);
        editor.putInt(testWordPosKey, currentWordIndex);
        editor.putInt(testProgress, ++currentProgress);
        editor.apply();
    }

    private ArrayList<String> getVariants(ArrayList<String> wordsList, String correctWordEn){

        ArrayList<String> chooseList = new ArrayList<>();


        if (wordsList.size() < 3) {

            ArrayList<String> testWords = new ArrayList<>();
            testWords.add("Apple");
            testWords.add("Banana");
            testWords.add("Berry");
            testWords.add("Book");

            wordsList = testWords;

        }

        wordsList.remove(correctWordEn);

        Collections.shuffle(wordsList);

        for (int i=0; i < 3; ++i) {
            chooseList.add(wordsList.get(i));
        }

        chooseList.add(correctWordEn);

        Collections.shuffle(chooseList);


        return chooseList;
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


    private void createNewSoundPool() {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    }

    private void wrongAnswer(Context context){
        try {
            playSound(fooSound);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "Попробуй еще раз", Toast.LENGTH_SHORT).show();
        Collections.shuffle(errorsSound);
        fooSound = loadSound(errorsSound.get(0) + ".mp3");
    }

    public void finalizeActivity() {
        this.finish();
    }

}
