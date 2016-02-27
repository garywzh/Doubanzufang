package org.garywzh.doubanzufang.ui.loader;

import android.content.Context;
import android.util.Log;

import org.garywzh.doubanzufang.model.Item;
import org.garywzh.doubanzufang.model.ResponseBean;
import org.garywzh.doubanzufang.network.RequestHelper;
import org.garywzh.doubanzufang.ui.ResultActivity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ItemListLoader extends AsyncTaskLoader<ResponseBean> {
    private String mLocation = "海淀";
    private String mSp = "0";
    private List<Item> mItems;
    private ResultActivity mContext;

    public ItemListLoader(Context context, String location) {
        super(context);
        mContext = (ResultActivity) context;
        mLocation = location;
        mItems = new ArrayList<>();
    }

    public void setParams(String location, String sp) {
        mLocation = location;
        mSp = sp;
        onContentChanged();
    }

    @Override
    public ResponseBean loadInBackgroundWithException() throws Exception {
        Log.d("itemloader", "loadinback pre");
        final ResponseBean responseBean = RequestHelper.getItemsList(mLocation, mSp);
        Log.d("itemloader", "loadinback end");

        if (mSp.equals("0")) {
            mItems.clear();
            mContext.requireScrollToTop = true;
        }

        final List<Item> items = responseBean.items;

        if (items.size() == 0) {
            mContext.noMore = true;
        } else {
            if (items.size() < 100) {
                mContext.noMore = true;
            }
            mContext.mSp = items.get(items.size() - 1).tid;
            mItems.addAll(items);

            /*remove duplicates*/
            final Set<Item> setItems = new LinkedHashSet<>(mItems);
            mItems.clear();
            mItems.addAll(setItems);

            responseBean.items = mItems;
        }

        return responseBean;
    }
}