package com.clevy.ikravtsov.clevytest;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.clevy.ikravtsov.clevytest.AppConstants.PROMO_1_ID;
import static com.clevy.ikravtsov.clevytest.AppConstants.PROMO_2_ID;
import static com.clevy.ikravtsov.clevytest.AppConstants.PROMO_3_ID;
import static com.clevy.ikravtsov.clevytest.AppConstants.TRIAL_DAYS_COUNT;
import static com.clevy.ikravtsov.clevytest.AppConstants.MONTHLY_SUBSCRIBE_ID;
import static com.clevy.ikravtsov.clevytest.AppConstants.isTestKey;
import static com.clevy.ikravtsov.clevytest.AppConstants.keyCurrentBlockIndex;
import static com.clevy.ikravtsov.clevytest.AppConstants.keyCurrentWordIndex;
import static com.clevy.ikravtsov.clevytest.AppConstants.testBlockPosKey;
import static com.clevy.ikravtsov.clevytest.AppConstants.testProgress;
import static com.clevy.ikravtsov.clevytest.AppConstants.testWordPosKey;
import static com.clevy.ikravtsov.clevytest.AppConstants.typeInApp;
import static com.clevy.ikravtsov.clevytest.AppConstants.typeSubs;
import static com.clevy.ikravtsov.clevytest.AppConstants.UNLIMITED_SUBSCRIBE_ID;
import static com.clevy.ikravtsov.clevytest.AppConstants.YEAR_SUBSCRIBE_ID;


public class MainActivity extends AppCompatActivity {

    Boolean isAlreadyBought = false;
    TextToSpeech t1;


    public PendingIntent pendingEducateIntent, pendingNotifyIntent;
    SharedPreferences sharedPref;

    IInAppBillingService inAppBillingService;

    ServiceConnection serviceConnection;
    private AssetManager mAssetManager;
    private SoundPool mSoundPool;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_name), Context.MODE_PRIVATE);


//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putInt(keyCurrentBlockIndex, 0);
//        editor.putInt(keyCurrentWordIndex, 0);
//        editor.putInt(testProgress, 0);
//        editor.putInt(testBlockPosKey, 0);
//        editor.putInt(testWordPosKey, 0);
//        editor.putBoolean(isTestKey, false);
//        editor.putInt("lessonNumb", 0);
//        editor.apply();

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                inAppBillingService = IInAppBillingService.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                inAppBillingService = null;
            }
        };

        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);


        TextView mTextMessage = (TextView) findViewById(R.id.message);
        Button btnStart = (Button) findViewById(R.id.button);

        Intent alarmIntent = new Intent(MainActivity.this, AlarmEducationReceiver.class);
        Intent notifyIntent = new Intent(MainActivity.this, AlarmNotifyReceiver.class);

        pendingEducateIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);
        pendingNotifyIntent = PendingIntent.getBroadcast(MainActivity.this, 0, notifyIntent, 0);

        View.OnClickListener startAction = new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                doBillingMagic();
            }
        };

        btnStart.setOnClickListener(startAction);

    }

    public void doBillingMagic(){

        ArrayList<String> possibleProductIds = new ArrayList<>(Arrays.asList(
                MONTHLY_SUBSCRIBE_ID,
                YEAR_SUBSCRIBE_ID,
                UNLIMITED_SUBSCRIBE_ID,
                PROMO_1_ID,
                PROMO_2_ID,
                PROMO_3_ID));

        try {

            if (isTrialActive() || isAlreadyBought(possibleProductIds)){
                Intent intentEducate = new Intent(this, EducationActivity.class);

                this.startActivity(intentEducate);
            }
            else {

                Intent intentShowCase = new Intent(this, ShowCaseActivity.class);

                intentShowCase.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                this.startActivity(intentShowCase);
            }

            this.finish();

        }
        catch (NullPointerException e) {
            Toast.makeText(this, "Для начала работы авторизуйтесь в Google Play", Toast.LENGTH_SHORT).show();
            finish();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasPurchases(String type, ArrayList<String> possibleProductsIds) throws Exception {
        String continuationToken = null;
        Boolean has = false;
        do {
            Bundle result = inAppBillingService.getPurchases(
                    3, this.getPackageName(), type, continuationToken);

            if (result.getInt("RESPONSE_CODE", -1) != 0) {
                throw new Exception("Invalid response code");
            }

            List<String> responseList = result.getStringArrayList("INAPP_PURCHASE_DATA_LIST");

            for (String purchaseData : responseList) {

                JSONObject jsonObject = new JSONObject(purchaseData);
                String boughtProduct = jsonObject.getString("productId");

                has = possibleProductsIds.contains(boughtProduct);

                if (has){
                    isAlreadyBought = true;
                    break;
                }
            }
            continuationToken = result.getString("INAPP_CONTINUATION_TOKEN");
        } while (continuationToken != null);

        return has;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
    }

    public Boolean isTrialActive(){

        Date date = new Date();
        String satartDate = sharedPref.getString("start_date", null);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Boolean isActive = false;

        if (satartDate == null) {

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("start_date", dateFormat.format(date));
            editor.apply();
            isActive = true;

        }
        else {
            try {
                date = dateFormat.parse(satartDate);

                Date currentDate = new Date();

                long diffInDays = (date.getTime() - currentDate.getTime()) / (24 * 60 * 60 * 1000);

                if (diffInDays < TRIAL_DAYS_COUNT) {
                    isActive = true;
                }

            } catch (ParseException e) {

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("start_date", dateFormat.format(date));
                editor.apply();

                isActive = true;

                e.printStackTrace();

            }
        }

        return isActive;
    }

    public Boolean isAlreadyBought(ArrayList<String> possibleProductIds) throws Exception {
        return hasPurchases(typeSubs, possibleProductIds) || hasPurchases(typeInApp, possibleProductIds);
    }
}
