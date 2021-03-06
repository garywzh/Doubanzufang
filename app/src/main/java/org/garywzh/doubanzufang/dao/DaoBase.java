package org.garywzh.doubanzufang.dao;

import android.database.sqlite.SQLiteDatabase;

public abstract class DaoBase {
    protected static <T> T execute(SqlOperation<T> operation) {
        return execute(operation, false);
    }

    protected static synchronized <T> T execute(SqlOperation<T> operation, boolean isWrite) {
        SQLiteDatabase db = null;
        try {
            final DoubanzufangDb instance = DoubanzufangDb.getInstance();
            db = isWrite ? instance.getWritableDatabase() : instance.getReadableDatabase();
            return operation.execute(db);
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
