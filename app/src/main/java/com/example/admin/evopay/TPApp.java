package com.example.admin.evopay;

import android.app.Application;

import timber.log.Timber;

public class TPApp extends Application {

    public TPApp() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        // to tender pos developer: create your own Timber.Tree instead of writing your own file logger.
        // if you want to write logs to a file (not smart to do) then put that functionality into a Timber.Tree
    }
}
