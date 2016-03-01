package com.example.eldar.parse;

import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Eldar on 18.09.2015.
 */
public class Executor {
    static final String CAMERALOCK="camlock";
    static final String LOCK = "lock";
    static final String CHANGEPASS = "chngpass";
    static final String WIFI = "wifi";
    static final String BLUETOOTH = "bluetooth";
    static final String MOBILE_DATA = "data";
    static final String SHUTDOWN = "shutdown";
    static final String STARTSERVICES = "start";
    static final String STOPSERVICES = "stop";
    public static final String WIPE = "wipe";

    static final void switchWifi(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int state = manager.getWifiState();
        if (state == WifiManager.WIFI_STATE_DISABLED) {
            manager.setWifiEnabled(true);
        } else if (state == WifiManager.WIFI_STATE_ENABLED) {
            manager.setWifiEnabled(false);
        }
    }

    static final void switchBluetooth() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter.isEnabled()) {
            adapter.disable();
        } else if (!adapter.isEnabled()) {
            adapter.enable();
        }


    }


    static void startServices(Context context) {
        context.startService(new Intent(context, GPSService.class));
        context.startService(new Intent(context, SmsService.class));
        context.startService(new Intent(context, ProcService.class));
        context.startService(new Intent(context, PhoneStateService.class));
        context.startService(new Intent(context, ApplicationService.class));
    }

    static void stopServices(Context context) {
        context.stopService(new Intent(context, ApplicationService.class));
        context.stopService(new Intent(context, PhoneStateService.class));
        context.stopService(new Intent(context, ProcService.class));
        context.stopService(new Intent(context, SmsService.class));
        context.stopService(new Intent(context, GPSService.class));
        context.startService(new Intent(context, SendService.class));


    }

    static void shutDown() {
            try {
                Process proc = Runtime.getRuntime()
                        .exec(new String[]{"su -c am start -a android.intent.action.ACTION_REQUEST_SHUTDOWN"});
                proc.waitFor();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

    }



    static final void switchMobileData(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {

            final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class conmanClass = null;
            try {
                conmanClass = Class.forName(conman.getClass().getName());
                final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
                iConnectivityManagerField.setAccessible(true);
                final Object iConnectivityManager = iConnectivityManagerField.get(conman);
                final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
                final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);

                boolean mobileDataAllowed = Settings.Secure.getInt(context.getContentResolver(), "mobile_data", 1) == 1;

                setMobileDataEnabledMethod.setAccessible(true);

                if (mobileDataAllowed)
                    setMobileDataEnabledMethod.invoke(iConnectivityManager, false);
                else
                    setMobileDataEnabledMethod.invoke(iConnectivityManager, true);

            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    public static void admin(Context context, String str) {
         String[] pass=str.split(" ");
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        ComponentName componentName = new ComponentName(context, Admin.class);
        boolean isAdminActive = manager.isAdminActive(componentName);
        switch (str) {
            case Executor.LOCK:
                if (isAdminActive)
                    manager.lockNow();
                break;
            case Executor.CAMERALOCK:
                if(isAdminActive)
                {
                    if(manager.getCameraDisabled(componentName))
            manager.setCameraDisabled(componentName,false);
                     else
                        manager.setCameraDisabled(componentName,true);
                }
                break;
            case Executor.WIPE:
                if (isAdminActive)
                { Toast.makeText(context,"wipe",Toast.LENGTH_LONG).show();
                    manager.wipeData(0);

                }
                break;
        }

        if (pass[0].equals(Executor.CHANGEPASS)&&pass[1].length()>=5)
        {boolean result=false;
            if(isAdminActive)
               result=  manager.resetPassword(pass[1].trim(), DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
            if(result)
            { Toast.makeText(context,"Пароль успешно изменен",Toast.LENGTH_LONG).show();
             manager.lockNow();
            }
        }

    }






}


