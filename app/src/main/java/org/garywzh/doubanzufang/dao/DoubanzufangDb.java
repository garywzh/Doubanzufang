package org.garywzh.doubanzufang.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.common.base.Preconditions;

import org.garywzh.doubanzufang.MyApplication;

/**
 * Created by garywzh on 2016/2/24.
 */
public class DoubanzufangDb extends SQLiteOpenHelper {
    private static final String TAG = DoubanzufangDb.class.getSimpleName();
    private static final String DB_NAME = "Doubanzufang.db";
    private static final int CURRENT_VERSION = 1;

    private static final DoubanzufangDb INSTANCE;

    static {
        INSTANCE = new DoubanzufangDb(MyApplication.getInstance());
    }

    public DoubanzufangDb(Context context) {
        super(context, DB_NAME, null, CURRENT_VERSION);
    }

    public static DoubanzufangDb getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ItemDao.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Preconditions.checkState(oldVersion == newVersion, "old version not match new version");
    }

    /**
     * it may use a lot of time
     */
    public void init() {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.close();
        }
    }

}
