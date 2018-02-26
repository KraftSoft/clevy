package com.clevy.ikravtsov.clevytest;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by ikravtsov on 24/02/2018.
 */

public class AlarmNotifyReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        // For our recurring task, we'll just display a message

        Toast.makeText(context, "Занятие начнется через 10 секунд", Toast.LENGTH_SHORT).show();
    }
}
