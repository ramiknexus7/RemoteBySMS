package com.example.eldar.parse;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Eldar on 19.09.2015.
 */
public class ApplicationService extends Service {
    private ActivityManager am;
    private asyncTask task;

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("Blin", "App service started");
        task = new asyncTask();

    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        Timer t = new Timer();
        t.schedule(new myTask(),0,1000);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        Log.d("Blin", "App service stop");
    }

    class myTask extends TimerTask {

        @Override
        public void run(){
            new asyncTask().execute();
        }

    }

    String name = "";

    class asyncTask extends AsyncTask<Void,Void,Void> {

        boolean newTask = false;
        List<ActivityManager.RunningTaskInfo> list;

        @Override
        public Void doInBackground(Void... params){
            list = am.getRunningTasks(1);
            if (!name.equals(list.get(0).topActivity.getPackageName())){
                newTask = true;
                name = list.get(0).topActivity.getPackageName();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if (newTask == true){
                newTask = false;
                SystemInfo.getSystemInfo(getApplicationContext(), "act_app:" + name);
            }
        }

    }

}
