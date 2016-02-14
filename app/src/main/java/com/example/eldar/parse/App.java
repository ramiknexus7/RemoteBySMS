package com.example.eldar.parse;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Eldar on 16.09.2015.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "MSUvkrZE5oxNuk5NKX1bgqCuQMVETvxy3W4CYSNp", "jXy7Y3iXxFzlnz0vOFbim6QXjw7W9hPxj3MFt5Sn");
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
