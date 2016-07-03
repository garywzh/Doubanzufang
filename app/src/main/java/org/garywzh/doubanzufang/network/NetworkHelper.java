package org.garywzh.doubanzufang.network;

import org.garywzh.doubanzufang.util.LogUtils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by WZH on 2016/7/3.
 */
public class NetworkHelper {
    public static String TAG = NetworkHelper.class.getSimpleName();
    public static String BASE_URL = "http://www.bpzufang.com/douban/";

    public static ItemsService itemsService = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor(new SimpleLogger())
                            .setLevel(HttpLoggingInterceptor.Level.BASIC))
                    .build())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItemsService.class);

    public static ItemsService getItemsService() {
        return itemsService;
    }

    static class SimpleLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            LogUtils.d(TAG, message);
        }
    }
}
