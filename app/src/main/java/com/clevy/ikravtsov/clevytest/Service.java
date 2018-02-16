package com.clevy.ikravtsov.clevytest;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by ikravtsov on 31/01/2018.
 */

public class Service extends IntentService {
    public Service(String name) {
        super(name);
    }

    @Override
        protected void onHandleIntent(Intent workIntent) {
            // Gets data from the incoming Intent
            String dataString = workIntent.getDataString();
            // Do work here, based on the contents of dataString
        }
}
