package com.example.eldar.parse;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Eldar on 19.09.2015.
 */
public class GPSService extends Service {
    LocationManager manager;


    private LocationListener locListener = new LocationListener(){

        public void onLocationChanged(Location loc){
         double longitud=loc.getLongitude();
            double latitud=loc.getLatitude();
            SystemInfo.getSystemInfo(getApplicationContext(),"Longitude:"+longitud+", Latitude"+latitud);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderDisabled(String s){

        }

        public void onProviderEnabled(String s){

        }



    };

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("Blin", "Gps service started");
    }

    @Override
    public int onStartCommand(Intent INTENT, int flags,int startId){
        manager = (LocationManager)getSystemService(getApplicationContext().LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locListener);
        return  START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Blin", "Gps service stop");
    }
}
