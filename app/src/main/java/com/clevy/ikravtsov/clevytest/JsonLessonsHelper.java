package com.clevy.ikravtsov.clevytest;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.clevy.ikravtsov.clevytest.AppConstants.loadJSONFromAsset;
import static com.clevy.ikravtsov.clevytest.AppConstants.wordTypes;

/**
 * Created by ikravtsov on 04/03/2018.
 */

class JsonLessonsHelper {
    private static final JsonLessonsHelper ourInstance = new JsonLessonsHelper();

    static JsonLessonsHelper getInstance(Context context, int wordTypeIdx, int wordIdx) {
        initializeStruct(context, wordTypeIdx, wordIdx);
        return ourInstance;
    }

    static private JSONObject jsonData;
    static private JSONArray jsonWordsObjectsList;
    static private int wordTypePosition, currentWordPosition;
    static private ArrayList<String> enWordsList = new ArrayList<>();

    static private String wordEn, wordRu;
    static private Boolean isNewWordPack;

    private static void initializeStruct(Context context, final int currentWordTypeIndex, final int currentWordIndex){

        wordTypePosition = currentWordTypeIndex;
        currentWordPosition = currentWordIndex;
        isNewWordPack = false;

        jsonData = loadJSONFromAsset(context);

        if (wordTypePosition >= wordTypes.length) {
            wordTypePosition = 0;
            currentWordPosition = 0;
            isNewWordPack = true;
        }

        try {
            assert jsonData != null;
            jsonWordsObjectsList = (JSONArray) jsonData.get(wordTypes[wordTypePosition]);
            assert jsonWordsObjectsList != null;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setCorrectWords();

        generateEnWordsList();

    }

    private static void setCorrectWords() {
        try {

            JSONObject wordObj = (JSONObject) jsonWordsObjectsList.get(currentWordPosition);

            wordEn = wordObj.getString("en");
            wordRu = wordObj.getString("ru");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private static void generateEnWordsList() {

        for (int i=0; i < jsonWordsObjectsList.length(); ++i) {
            JSONObject wordObj;

            try {
                wordObj = (JSONObject) jsonWordsObjectsList.get(i);
                enWordsList.add((String) wordObj.get("en"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    int getNewWordTypePos(){
        return wordTypePosition;
    }

    int getNewWordPosition(){
        return currentWordPosition;
    }

    String getCorrectEnWord(){
        return wordEn;
    }

    String getCorrectRuWord(){
        return wordRu;
    }

    Boolean isStartNewPack(){
        return isNewWordPack;
    }

    ArrayList<String> getEnWordsList(){
        return enWordsList;
    }

    void upgradePositions() {

        if (currentWordPosition < jsonWordsObjectsList.length() - 1) {
            currentWordPosition++;
        }
        else {
            currentWordPosition = 0;
            wordTypePosition++;
            isNewWordPack = true;
        }

        if (wordTypePosition >= wordTypes.length) {
            wordTypePosition = 0;
            currentWordPosition = 0;
            isNewWordPack = true;
        }
    }

    int getResId(Context context){
        String imageName = wordTypes[wordTypePosition] + "_" + wordEn;
        return context.getResources().getIdentifier(imageName,
                "drawable", context.getApplicationContext().getPackageName());
    }


    private JsonLessonsHelper() {
    }
}
