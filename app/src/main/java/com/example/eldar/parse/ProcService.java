package com.example.eldar.parse;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Eldar on 19.09.2015.
 */
public class ProcService extends Service {
    private ActivityManager am;
    private List<ActivityManager.RunningAppProcessInfo> list;
    private ArrayList<String> procNames;
    private asyncTask task;
    private int size;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Blin", "Proc service started");
        procNames = new ArrayList<String>();
        task = new asyncTask();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        am=(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
           list=am.getRunningAppProcesses();
        for (int i = 0; i < list.size(); i++){
            procNames.add(list.get(i).processName);
        }
           size=list.size();
         Timer timer=new Timer();
        timer.schedule(new MyTask(),0,1000);

        return START_NOT_STICKY;
    }
    class  MyTask extends TimerTask
    {

        @Override
        public void run() {
            new asyncTask().execute();
        }
    }
    class  asyncTask extends AsyncTask<Void,Void,Void>
    {int delta = 0;
        String name = "";

        @Override
        protected Void doInBackground(Void... params) {
             delta=am.getRunningAppProcesses().size()-procNames.size();
            int count = 0;
            if (delta>0)
            {int i;
                list = am.getRunningAppProcesses();
                for (i = 0; i < procNames.size(); i++){
                    for (int j = 0; j < list.size(); j++){
                        if (!procNames.get(i).equals(list.get(j).processName)){
                            count ++;
                        }
                    }
                    if (count == list.size()) {
                        name = procNames.get(i);
                        break;
                    }
                    count = 0;
                }

            }
            count = 0;
            if (delta < 0){
                int i;
                list = am.getRunningAppProcesses();
                for (i = 0; i < procNames.size(); i++){
                    for (int j = 0; j < list.size(); j++){
                        if (!procNames.get(i).equals(list.get(j).processName)){
                            count ++;
                        }
                    }
                    if (count == list.size()) {
                        name = procNames.get(i);
                        break;
                    }
                    count = 0;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (name == "") return;
            if (delta > 0){
                SystemInfo.getSystemInfo(getApplicationContext(), "proc_start:" + name);
                procNames.add(name);
            }
            if (delta < 0){
                SystemInfo.getSystemInfo(getApplicationContext(), "proc_stop:" + name);
                for (int i = 0; i < procNames.size(); i++){
                    if (procNames.get(i).equals(name))
                        procNames.remove(i);
                }
            }
            delta = 0;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Blin", "Proc service stop");
    }
}
