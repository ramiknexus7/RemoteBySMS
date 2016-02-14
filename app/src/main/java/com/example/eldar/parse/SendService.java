package com.example.eldar.parse;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import  android.util.Log;

import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

/**
 * Created by Eldar on 20.09.2015.
 */
public class SendService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SendService() {
        super("sendService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("Blin","Send service started");
        File file = new File(Environment.getExternalStorageDirectory() + "/log/sysInfo.log");
         FileInputStream inputStream=null;
        StringBuilder builder=new StringBuilder();
        try {
            String str="";
             inputStream=new FileInputStream(file);
            BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
           try {
               while ((str = reader.readLine()) != null) {
                   builder.append(str + "\n");
               }
           }  finally
           {
               inputStream.getFD().sync();
               inputStream.close();
           }


            ParseFile parseFile=new ParseFile(builder.toString().getBytes(),"sysInfo.txt");
            parseFile.save();
            ParseObject object=ParseObject.create("Log");
            object.put("LogFile",parseFile);
            object.saveEventually();
           file.delete();
        } catch (Exception e) {
            Log.d("Blin",e.toString());
        }

    }
}
