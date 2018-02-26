package com.clevy.ikravtsov.clevytest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import static com.clevy.ikravtsov.clevytest.AppConstants.keyCurrentIndex;


public class TestActivity extends AppCompatActivity {

    ImageView testImg;

    Button firstBtn;
    Button secondBtn;
    Button thirdBtn;
    Button fourthBtn;
    Integer currentWordIndex, wordTypeIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_test);

        final Context context = this;
        Intent intent = getIntent();

        final SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_name), Context.MODE_PRIVATE);

        final String correctWordEn = intent.getStringExtra("correctAnswer");
        final String correctWordRu = intent.getStringExtra("correctAnswerRu");

        ArrayList<String> wordsList = intent.getStringArrayListExtra("wordsList");
        ArrayList<String> chooseList = new ArrayList<String>();

        currentWordIndex = intent.getIntExtra("currentWordIndex", 0);
        wordTypeIndex = intent.getIntExtra("wordTypeIndex", 0);

        wordsList.remove(correctWordEn);

        Collections.shuffle(wordsList);

        if (wordsList.size() < 3) {
            Toast.makeText(context, "Ошибка :(", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(keyCurrentIndex, ++wordTypeIndex);
            editor.apply();
            this.finish();
        }

        for (int i=0; i < 3; ++i) {
            chooseList.add(wordsList.get(i));
        }

        chooseList.add(correctWordEn);

        Collections.shuffle(chooseList);

        testImg = (ImageView) findViewById(R.id.testImage);
        testImg.setBackgroundResource(intent.getIntExtra("imageResId", 0));

        TextView testText = (TextView) findViewById(R.id.testText);
        testText.setText(correctWordRu);

        firstBtn = (Button) findViewById(R.id.first);
        firstBtn.setText(chooseList.get(0));

        secondBtn = (Button) findViewById(R.id.second);
        secondBtn.setText(chooseList.get(1));

        thirdBtn = (Button) findViewById(R.id.third);
        thirdBtn.setText(chooseList.get(2));

        fourthBtn = (Button) findViewById(R.id.fourth);
        fourthBtn.setText(chooseList.get(3));

        View.OnClickListener startAction = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button b = (Button) v;

                if (b.getText().equals(correctWordEn)) {

                    b.setBackgroundResource(R.color.CustomcAccent);
                    b.setTextColor(Color.WHITE);

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(keyCurrentIndex, ++currentWordIndex);
                    editor.apply();

                    Intent intentFinal = new Intent(context, FinalActivity.class);
                    intentFinal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(intentFinal);
                    finalizeActivity();
                }
                else {
                    Toast.makeText(context, "Попробуй еще раз", Toast.LENGTH_SHORT).show();
                }
            }
        };


        firstBtn.setOnClickListener(startAction);
        secondBtn.setOnClickListener(startAction);
        thirdBtn.setOnClickListener(startAction);
        fourthBtn.setOnClickListener(startAction);

    }

    public void finalizeActivity() {
        this.finish();
    }

}
