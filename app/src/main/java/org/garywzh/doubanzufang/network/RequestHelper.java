package org.garywzh.doubanzufang.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.garywzh.doubanzufang.common.exception.ConnectionException;
import org.garywzh.doubanzufang.common.exception.RemoteException;
import org.garywzh.doubanzufang.common.exception.RequestException;
import org.garywzh.doubanzufang.model.Item;
import org.garywzh.doubanzufang.model.ResponseBean;
import org.garywzh.doubanzufang.util.LogUtils;
import org.garywzh.doubanzufang.util.UTF8EncoderUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by WZH on 2015/10/22.
 */
public class RequestHelper {
    private static final String TAG = RequestHelper.class.getSimpleName();

    public static final String BASE_URL = "http://www.bpzufang.com/douban/";

    private static final OkHttpClient CLIENT;

    private static final Gson GSON;

    static {
        CLIENT = new OkHttpClient();
        CLIENT.setConnectTimeout(10, TimeUnit.SECONDS);
        CLIENT.setWriteTimeout(10, TimeUnit.SECONDS);
        CLIENT.setReadTimeout(30, TimeUnit.SECONDS);
        CLIENT.setFollowRedirects(false);

        GSON = new Gson();
    }

    private static Gson getGson(){
        return GSON;
    }

    public static ResponseBean getItemsList(String location) throws ConnectionException, RemoteException {

        final String searchUrl = "http://www.bpzufang.com/douban/search.php?kw=" + UTF8EncoderUtil.encode(location) + "&sp=0";

        final Request request = new Request.Builder()
                .url(searchUrl)
                .build();

        final Response response = sendRequest(request);

        final ResponseBean responseBean;
        try {
            final String json = response.body().string();

            responseBean = getGson().fromJson(json, new TypeToken<ResponseBean>(){}.getType());

        } catch (IOException e) {
            throw new ConnectionException(e);
        }

        /*remove duplicates*/
        final List<Item> items = responseBean.items;
        final Set<Item> setItems = new LinkedHashSet<>(items);
        items.clear();
        items.addAll(setItems);

        LogUtils.d(TAG, "items count: "+items.size());

        return responseBean;
    }

    static Response sendRequest(Request request) throws ConnectionException, RemoteException {

        final Response response;
        try {
            LogUtils.d(TAG, request.toString());
            response = CLIENT.newCall(request).execute();
            LogUtils.d(TAG, response.toString());
        } catch (IOException e) {
            throw new ConnectionException(e);
        }

        checkResponse(response);
        return response;
    }

    private static void checkResponse(Response response) throws RemoteException, RequestException, ConnectionException {
        if (response.isSuccessful()) {
            return;
        }

        final int code = response.code();

        if (code == 302) {
            return;
        }
        if (code >= 500) {
            throw new RemoteException(response);
        }

        if (code == 403 || code == 404) {
            try {
                final String body = response.body().string();
                LogUtils.d(TAG, "404 : " + body);
            } catch (IOException e) {
                throw new ConnectionException(e);
            }
        }
        throw new RequestException(response);
    }

}
