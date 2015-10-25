package org.garywzh.doubanzufang.ui.loader;

import android.content.Context;

import org.garywzh.doubanzufang.model.ResponseBean;
import org.garywzh.doubanzufang.network.RequestHelper;

public class ItemListLoader extends AsyncTaskLoader<ResponseBean> {
    private String mLocation = "海淀";

    public ItemListLoader(Context context) {
        super(context);
    }

    public void setPage(String location){
        mLocation = location;
        onContentChanged();
    }

    @Override
    public ResponseBean loadInBackgroundWithException() throws Exception {
        return RequestHelper.getItemsList(mLocation);
    }
}
