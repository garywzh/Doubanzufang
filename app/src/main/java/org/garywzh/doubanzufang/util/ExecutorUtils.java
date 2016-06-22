package org.garywzh.doubanzufang.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorUtils {
    private static final Handler mUiHandler;
    private static final ExecutorService mCachePool;

    static {
        mUiHandler = new Handler(Looper.getMainLooper());
        mCachePool = Executors.newCachedThreadPool();
    }

    /**
     * @see ExecutorService#execute(Runnable)
     */
    public static void execute(Runnable command) {
        mCachePool.execute(command);
    }

    public static void runOnUiThread(Runnable runnable) {
        mUiHandler.post(runnable);
    }
}
