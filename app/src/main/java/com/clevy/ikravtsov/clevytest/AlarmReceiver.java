package com.clevy.ikravtsov.clevytest;

/**
 * Created by ikravtsov on 08/02/2018.
 */

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        // For our recurring task, we'll just display a message

        Intent intentNew = new Intent(context, EducationActivity.class);
        context.startActivity(intentNew);

    }
}
