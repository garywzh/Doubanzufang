package org.garywzh.doubanzufang.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.garywzh.doubanzufang.model.Item;

import java.util.List;

/**
 * Created by garywzh on 2016/2/24.
 */
public class ItemDao extends DaoBase {
    private static final String TABLE_NAME = "item";

    private static final String KEY_TOPIC_ID = "tid";
    private static final String KEY_AUTHOR_ID = "aid";
    private static final String KEY_TITLE = "ttl";
    private static final String KEY_TOPIC_CREATED_TIME = "tcr";
    private static final String KEY_AUTHOR_NAME = "anm";
    private static final String KEY_GROUP_NAME = "gnm";
    private static final String KEY_DOUBAN_GROUP_NAME = "dgd";


    private static final String SQL_GET_HISTORY = SQLiteQueryBuilder.buildQueryString(false,
            TABLE_NAME, new String[]{KEY_TOPIC_ID, KEY_AUTHOR_ID, KEY_TITLE, KEY_TOPIC_CREATED_TIME, KEY_AUTHOR_NAME, KEY_GROUP_NAME, KEY_DOUBAN_GROUP_NAME},
            null, null, null, null, null);

    static void createTable(SQLiteDatabase db) {
        Preconditions.checkState(db.inTransaction());

        String sql = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_TOPIC_ID + " TEXT PRIMARY KEY," +
                KEY_AUTHOR_ID + " TEXT NOT NULL," +
                KEY_TITLE + " TEXT NOT NULL," +
                KEY_TOPIC_CREATED_TIME + " TEXT NOT NULL," +
                KEY_AUTHOR_NAME + " TEXT NOT NULL," +
                KEY_GROUP_NAME + " TEXT NOT NULL," +
                KEY_DOUBAN_GROUP_NAME + " TEXT NOT NULL" +
                ")";
        db.execSQL(sql);
    }

    public static void put(final Item item) {
        execute(new SqlOperation<Void>() {
            @Override
            public Void execute(SQLiteDatabase db) {
                put(db, item);
                return null;
            }
        }, true);
    }

    private static void put(SQLiteDatabase db, Item item) {
        final ContentValues values = new ContentValues(7);
        values.put(KEY_TOPIC_ID, item.tid);
        values.put(KEY_AUTHOR_ID, item.aid);
        values.put(KEY_TITLE, item.ttl);
        values.put(KEY_TOPIC_CREATED_TIME, item.tcr);
        values.put(KEY_AUTHOR_NAME, item.anm);
        values.put(KEY_GROUP_NAME, item.gnm);
        values.put(KEY_DOUBAN_GROUP_NAME, item.dgd);

        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

    }

    public static void remove(final Item item) {
        execute(new SqlOperation<Void>() {
            @Override
            public Void execute(SQLiteDatabase db) {
                db.delete(TABLE_NAME, KEY_TOPIC_ID + "=" + item.tid, null);
                return null;
            }
        }, true);
    }

    public static List<Item> getItems() {
        return execute(new SqlOperation<List<Item>>() {
            @Override
            public List<Item> execute(SQLiteDatabase db) {
                List<Item> result = Lists.newArrayList();

                Cursor cursor = null;
                try {
                    cursor = db.rawQuery(SQL_GET_HISTORY, null);

                    while (cursor.moveToNext()) {
                        final String tid = cursor.getString(0);
                        final String aid = cursor.getString(1);
                        final String ttl = cursor.getString(2);
                        final String tcr = cursor.getString(3);
                        final String anm = cursor.getString(4);
                        final String gnm = cursor.getString(5);
                        final String dgd = cursor.getString(6);

                        final Item item = new Item(tid, aid, ttl, tcr, anm, gnm, dgd);
                        result.add(item);
                    }

                    return result;
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        });
    }

}
