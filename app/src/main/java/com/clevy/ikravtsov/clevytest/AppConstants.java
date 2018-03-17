package com.clevy.ikravtsov.clevytest;

import android.content.Context;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ikravtsov on 10/02/2018.
 */

public class AppConstants {

    final static String keyCurrentBlockIndex = "word_type";
    final static String keyCurrentWordIndex = "index";

    final static String isTestKey = "isTest";
    final static String testBlockPosKey = "test_word_type_index";
    final static String testWordPosKey = "test_word_index";

    final static String testProgress = "progress";

    final static String typeSubs = "subs";
    final static String typeInApp = "inapp";

    final static String UNLIMITED_SUBSCRIBE_ID = "clevy_unlimit";
    final static String MONTHLY_SUBSCRIBE_ID = "com.clevy.ikravtsov.mounthly_subscription";
    final static String YEAR_SUBSCRIBE_ID = "com.clevy.ikravtsov.yearly_subscription";

    final static String PROMO_1_ID = "com.clevy.ikravtsov.promo_1";
    final static String PROMO_2_ID = "com.clevy.ikravtsov.promo_2";
    final static String PROMO_3_ID = "com.clevy.ikravtsov.promo_3";

    final static long TRIAL_DAYS_COUNT = 2;

    final static Integer DEFAULT_WORD_INDEX = 0;
    final static Integer TEST_PACKAGE_SIZE = 6;

    static String[] wordTypes = new String[]{"nouns", "verbs", "months", "adverb", "numbers", "prepositions"};
    static ArrayList errorsSound = new ArrayList<>(Arrays.asList("error_1","error_2","error_3","error_4","error_5","error_6","error_7","error_8"));
    static ArrayList successSound = new ArrayList<>(Arrays.asList("success_1","success_2","success_3","success_4","success_5","success_6","success_7","success_8"));
    static ArrayList<String> finalMove = new ArrayList<>(Arrays.asList(
            "final_move0",
            "final_move0",
            "final_move1",
            "final_move2",
            "final_move3",
            "final_move5",
            "final_move6",
            "final_move7",
            "final_move8",
            "final_move9",
            "final_move10",
            "final_move11",
            "final_move12",
            "final_move13",
            "final_move14",
            "final_move15",
            "final_move16"
    ));


    static JSONObject loadJSONFromAsset(Context conext) {
        String json = null;
        try {
            InputStream is = conext.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        JSONObject obj = null;
        try {

            obj = new JSONObject(json);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }
}
