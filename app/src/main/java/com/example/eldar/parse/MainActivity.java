package com.example.eldar.parse;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;



public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this,"App is started",Toast.LENGTH_LONG).show();
        DevicePolicyManager manager =(DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);

        ComponentName componentName=new ComponentName(this,Admin.class);
        if (!manager.isAdminActive(componentName)){
            Intent intent=new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Эта программа удаляет старый пароль и ставит новый");
        startActivity(intent);}


       finish();
    }
}
