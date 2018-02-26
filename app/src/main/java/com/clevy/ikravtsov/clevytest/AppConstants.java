package com.clevy.ikravtsov.clevytest;

import android.content.Context;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ikravtsov on 10/02/2018.
 */

public class AppConstants {

    final static String keyCurrentWordTypeIndex = "word_type";
    final static String keyCurrentIndex = "index";

    final static String keyPaid = "isAlreadyPaid";


    final static String typeSubs = "subs";
    final static String typeInApp = "inapp";

    final static String unlimitSubId = "clevy_unlimit";

    final static String mounthlySubId = "com.clevy.ikravtsov.mounthly_subscription";
    final static String yearlySubId = "com.clevy.ikravtsov.yearly_subscription";

    final static long TRIAL_DAYS_COUNT = 3;

    public static Integer defaultWordTypeIndex = 0;
    public static Integer defaultWordIndex = 0;

    public static String[] wordTypes = new String[]{"nouns", "verbs"};

    public static JSONObject loadJSONFromAsset(Context conext) {
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
