package org.garywzh.doubanzufang.ui.loader;

import android.content.Context;

import org.garywzh.doubanzufang.dao.ItemDao;
import org.garywzh.doubanzufang.model.Item;

import java.util.List;

/**
 * Created by garywzh on 2016/2/24.
 */
public class FavItemListLoader extends AsyncTaskLoader<List<Item>> {

    public FavItemListLoader(Context context) {
        super(context);
    }

    @Override
    public List<Item> loadInBackgroundWithException() throws Exception {
        return ItemDao.getItems();
    }
}
