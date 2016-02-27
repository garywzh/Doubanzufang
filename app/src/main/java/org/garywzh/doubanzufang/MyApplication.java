package org.garywzh.doubanzufang;

import android.app.Application;

import org.garywzh.doubanzufang.dao.DoubanzufangDb;

/**
 * Created by garywzh on 2016/2/24.
 */
public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        init();
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DoubanzufangDb.getInstance().init();

            }
        }).start();
    }

}