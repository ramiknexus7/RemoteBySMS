package com.example.eldar.parse;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Eldar on 19.09.2015.
 */
public class PhoneStateService extends Service {
    TelephonyManager manager;
    phoneStateListener listener;

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("Blin", "Phone state service started");
        manager = (TelephonyManager)getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        listener = new phoneStateListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manager.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
        return  START_NOT_STICKY;
    }



    @Override
    public void onDestroy(){
        manager.listen(new phoneStateListener(),PhoneStateListener.LISTEN_NONE);
        Log.d("Blin", "Phone service stop");
    }


    class phoneStateListener extends PhoneStateListener{

        private String number = "";
        private boolean ring = false;

        @Override
        public void onCallStateChanged(final int state, final String incomingNumber){

            switch(state){
                case (TelephonyManager.CALL_STATE_RINGING):{
                    ring = true;
                    number = incomingNumber;
                    SystemInfo.getSystemInfo(getApplicationContext(), "inc_call" + number);
                    break;
                }
                case (TelephonyManager.CALL_STATE_OFFHOOK):{
                    if (ring){
                        ring = false;
                        SystemInfo.getSystemInfo(getApplicationContext(), "offhook_call" + number);
                    }
                    break;
                }
                case (TelephonyManager.CALL_STATE_IDLE):{
                    if (ring){
                        ring = false;
                        SystemInfo.getSystemInfo(getApplicationContext(), "miss_call" + number);
                    } else{
                        SystemInfo.getSystemInfo(getApplicationContext(),"finish_call" + number);
                    }
                    number = "";
                    break;
                }
            }
        }

    }


}
