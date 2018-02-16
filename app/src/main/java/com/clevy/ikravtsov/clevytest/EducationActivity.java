package com.clevy.ikravtsov.clevytest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.clevy.ikravtsov.clevytest.TestConstants.*;

public class EducationActivity extends AppCompatActivity {

    Button btnNext;

    String wordEn, wordRu;
    Integer currentWordIndex, wordTypeIndex;
    ImageView image;
    JSONArray jsonWordsObjectsList;
    String imageName;
    TextView textHint;
    JSONObject jsonData;
    ArrayList<String> enWordsList = new ArrayList<>();
    JSONObject wordObj;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main2);

        // TODO use this
        final Context context = this.getApplicationContext();

        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_name), Context.MODE_PRIVATE);

        Integer lessonNumber = sharedPref.getInt("lessonNumb", 0);

        if (lessonNumber == 0) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                v.vibrate(300);
            }
        }

        wordTypeIndex = sharedPref.getInt(keyCurrentWordTypeIndex, 0);

        if (wordTypeIndex >= wordTypes.length) {
            wordTypeIndex = 0;
            currentWordIndex = 0;
        }

        else {
            jsonData = loadJSONFromAsset(this);

            try {
                assert jsonData != null;
                jsonWordsObjectsList = (JSONArray) jsonData.get(wordTypes[wordTypeIndex]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            currentWordIndex = sharedPref.getInt(keyCurrentIndex, defaultWordIndex);

            assert jsonWordsObjectsList != null;
            if (currentWordIndex < jsonWordsObjectsList.length() - 1) {
                currentWordIndex++;
            }
            else {
                currentWordIndex = 0;
                wordTypeIndex++;
            }

        }

        if (wordTypeIndex >= wordTypes.length) {
            wordTypeIndex = 0;
            currentWordIndex = 0;
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(keyCurrentWordTypeIndex, wordTypeIndex);
        editor.putInt(keyCurrentIndex, currentWordIndex);
        editor.apply();


        btnNext = (Button) findViewById(R.id.next);
        textHint = (TextView) findViewById(R.id.educateText);
        image = (ImageView) findViewById(R.id.educateImage);


        try {
            // Use actual words package
            jsonWordsObjectsList = (JSONArray) jsonData.get(wordTypes[wordTypeIndex]);

            JSONObject wordObj = (JSONObject) jsonWordsObjectsList.get(currentWordIndex);

            wordEn = wordObj.getString("en");
            wordRu = wordObj.getString("ru");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        imageName = wordTypes[wordTypeIndex] + "_" + wordEn;


        final int resID = getResources().getIdentifier(imageName,
                "drawable", getApplicationContext().getPackageName());

        image.setBackgroundResource(resID);
        textHint.setText(wordRu + " - " + wordEn);


        for (int i=0; i < jsonWordsObjectsList.length(); ++i) {
            try {
                wordObj = (JSONObject) jsonWordsObjectsList.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                enWordsList.add((String) wordObj.get("en"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        View.OnClickListener startTest = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentTest = new Intent(context, TestActivity.class);

                intentTest.putExtra("correctAnswer", wordEn);
                intentTest.putExtra("imageResId", resID);
                intentTest.putExtra("currentWordIndex", currentWordIndex);
                intentTest.putExtra("wordTypeIndex", wordTypeIndex);
                intentTest.putExtra("correctAnswerRu", wordRu);

                intentTest.putStringArrayListExtra("wordsList", enWordsList);

                context.startActivity(intentTest);
                finalizeActivity();
            }
        };

        btnNext.setOnClickListener(startTest);

        Button closeBtn = (Button) findViewById(R.id.closeCircle);
        View.OnClickListener closeWin = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finalizeActivity();
            }
        };

        closeBtn.setOnClickListener(closeWin);


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
}
