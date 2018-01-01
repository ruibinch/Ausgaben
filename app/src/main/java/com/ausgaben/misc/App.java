package com.ausgaben.misc;

import android.app.Application;
import android.content.Context;

/**
 * Created by Ruibin on 1/1/2018.
 */

public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }

}
