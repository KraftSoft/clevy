package com.clevy.ikravtsov.clevytest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.clevy.ikravtsov.clevytest.AppConstants.MONTHLY_SUBSCRIBE_ID;
import static com.clevy.ikravtsov.clevytest.AppConstants.typeInApp;
import static com.clevy.ikravtsov.clevytest.AppConstants.typeSubs;
import static com.clevy.ikravtsov.clevytest.AppConstants.UNLIMITED_SUBSCRIBE_ID;
import static com.clevy.ikravtsov.clevytest.AppConstants.YEAR_SUBSCRIBE_ID;
import static com.clevy.ikravtsov.clevytest.InAppProduct.BILLING_RESPONSE_RESULT_OK;
import static com.clevy.ikravtsov.clevytest.InAppProduct.REQUEST_CODE_BUY;

public class ShowCaseActivity extends AppCompatActivity {

    IInAppBillingService inAppBillingService;
    ServiceConnection serviceConnection;

    HashMap<String, InAppProduct> possibleProductsMap;


    Button buyMounthly, buyYearly, buyUnlimited, getFree, buttonDialogOk;
    private Context context;
    private Dialog dialog;
    private View.OnClickListener onClickOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_case);


        buyMounthly = findViewById(R.id.buyMonthly);
        buyYearly = findViewById(R.id.buyYearly);
        buyUnlimited = findViewById(R.id.buyUnlimited);

        getFree = findViewById(R.id.b_get_free);

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

        context = this;
        dialog = new Dialog(context);

        View.OnClickListener buySomething = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                buildShowCase();
                switch (b.getId()) {
                    case R.id.buyMonthly:
                        purchaseProduct(possibleProductsMap.get(MONTHLY_SUBSCRIBE_ID));
                        break;
                    case R.id.buyYearly:
                        purchaseProduct(possibleProductsMap.get(YEAR_SUBSCRIBE_ID));
                        break;
                    case R.id.buyUnlimited:
                        purchaseProduct(possibleProductsMap.get(UNLIMITED_SUBSCRIBE_ID));
                        break;
                    default:
                        break;
                }
            }
        };

        onClickOk = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };

        View.OnClickListener showPopup = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // Get the layout inflater

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder.setView(R.layout.get_free_popup)
                            // Add action buttons
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    dialog.setContentView(R.layout.get_free_popup);
                    dialog.show();
                }


            }
        };

        buyMounthly.setOnClickListener(buySomething);
        buyYearly.setOnClickListener(buySomething);
        buyUnlimited.setOnClickListener(buySomething);

        getFree.setOnClickListener(showPopup);


    }

    public void buildShowCase(){

        String[] subscriptionsIds = new String[]{MONTHLY_SUBSCRIBE_ID, YEAR_SUBSCRIBE_ID};
        String[] inAppIds = new String[]{UNLIMITED_SUBSCRIBE_ID};

        ArrayList<String> subsList = new ArrayList<>(Arrays.asList(subscriptionsIds));
        ArrayList<String> inAppsList = new ArrayList<>(Arrays.asList(inAppIds));

        Bundle querySubs = new Bundle();
        Bundle queryInApp = new Bundle();

        querySubs.putStringArrayList("ITEM_ID_LIST", subsList);
        queryInApp.putStringArrayList("ITEM_ID_LIST", inAppsList);

        possibleProductsMap = new HashMap<>();

        for (InAppProduct product: getPossibleProducts(typeSubs, querySubs)) {
            possibleProductsMap.put(product.getId(), product);
        }

        for (InAppProduct product: getPossibleProducts(typeInApp, queryInApp)) {
            possibleProductsMap.put(product.getId(), product);
        }
    }

    public ArrayList<InAppProduct> getPossibleProducts(String productType, Bundle query){

        Bundle skuDetails = null;
        ArrayList<InAppProduct> result = new ArrayList<>();

        if(inAppBillingService != null){
            try {
                skuDetails = inAppBillingService.getSkuDetails(5, this.getPackageName(), productType, query);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            Integer code = skuDetails.getInt("RESPONSE_CODE");

            if (code != BILLING_RESPONSE_RESULT_OK) {
                Log.println(Log.ERROR, "GoogleAPI", "Billing service response code = " + code);
                finish();
            }

            ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

            for (String responseItem : responseList) {

                InAppProduct product = null;

                try {
                    product = parseProduct(responseItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                result.add(product);

            }
        }

        return result;
    }

    public InAppProduct parseProduct(String responseItem) throws JSONException {

        JSONObject jsonObject = new JSONObject(responseItem);
        InAppProduct product = new InAppProduct();
        // "com.example.myapp_testing_inapp1"
        product.productId = jsonObject.getString("productId");
        // Покупка
        product.storeName = jsonObject.getString("title");
        // Детали покупки
        product.storeDescription = jsonObject.getString("description");
        // "0.99USD"
        product.price = jsonObject.getString("price");
        // "true/false"
        product.isSubscription = jsonObject.getString("type").equals("subs");
        // "990000" = цена x 1000000
        product.priceAmountMicros =
                Integer.parseInt(jsonObject.getString("price_amount_micros"));
        // USD
        product.currencyIsoCode = jsonObject.getString("price_currency_code");
        return product;
    }

    @SuppressLint("RestrictedApi")
    public void purchaseProduct(InAppProduct product) {

        String productId = product.getId();
        String type = product.getType();

        // сюда вы можете добавить произвольные данные
        // потом вы сможете получить их вместе с покупкой

        String developerPayload = "todo_add_some_payload";
        Bundle buyIntentBundle = null;

        try {
            buyIntentBundle = inAppBillingService.getBuyIntent(3, this.getPackageName(), productId, type, developerPayload);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        PendingIntent pendingIntent = null;
        if (buyIntentBundle != null) {
            pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            try {
                startIntentSenderForResult(pendingIntent.getIntentSender(),
                        REQUEST_CODE_BUY, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                        Integer.valueOf(0), null);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(this, "Произощла ошибка, попробуйте позже", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
    }
}
