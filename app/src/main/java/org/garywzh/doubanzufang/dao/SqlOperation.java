package org.garywzh.doubanzufang.dao;

import android.database.sqlite.SQLiteDatabase;

public abstract class SqlOperation<T> {
    public abstract T execute(SQLiteDatabase db);
}
