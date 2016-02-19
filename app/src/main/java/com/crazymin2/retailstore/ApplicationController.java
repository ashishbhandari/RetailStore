package com.crazymin2.retailstore;

import android.app.Application;

/**
 * Created by ashish (Min2) on 05/02/16.
 */
public class ApplicationController extends Application {

    private static ApplicationController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized ApplicationController getInstance() {
        return mInstance;
    }
}
