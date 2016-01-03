package com.silicongo.george.autotextmessage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by suxch on 2016/1/3.
 */
public class TextDbAdapter {
    private Context mCtx;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public TextDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public TextDbAdapter open() {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public Cursor fetchAllTextMessages() {
        return mDb.query(DatabaseHelper.TABLE_NAME_AUTO_TEXT,
                SimplePropertyCollection.getKeyArray(TextMsgInfo.TEXT_DEFAULTS_ALL),
                null, null, null, null, TextMsgInfo.ROW_ID);
    }

    public TextMsgInfo getAlarmById(long id) {
        if (id == 0) {
            return new TextMsgInfo();
        }

        Cursor cur = mDb.query(DatabaseHelper.TABLE_NAME_AUTO_TEXT,
                SimplePropertyCollection.getKeyArray(TextMsgInfo.TEXT_DEFAULTS_ALL),
                TextMsgInfo.ROW_ID+"="+id, null, null, null, TextMsgInfo.ROW_ID);

        if (cur == null) {
            return new TextMsgInfo();
        }

        cur.moveToFirst();
        if (cur.isAfterLast()) {
            cur.close();
            return new TextMsgInfo();
        } else {
            TextMsgInfo ai = new TextMsgInfo(cur);
            cur.close();
            return ai;
        }
    }

    public TextMsgInfo getNewTextInfo() {
        return new TextMsgInfo();
    }

    public void saveTextMsgInfo(TextMsgInfo ai) {
        ai.saveItem(mDb, DatabaseHelper.TABLE_NAME_AUTO_TEXT, TextMsgInfo.ROW_ID);
    }

    public void deleteTextMsgInfo(long _id) {
        mDb.delete(DatabaseHelper.TABLE_NAME_AUTO_TEXT, TextMsgInfo.ROW_ID+"="+_id, null);
    }
    public void deleteAllTextMsgInfo() {
        mDb.delete(DatabaseHelper.TABLE_NAME_AUTO_TEXT, null, null);
    }
}
