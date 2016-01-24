package com.silicongo.george.autotextmessage.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.silicongo.george.autotextmessage.DataSet.SimplePropertyCollection;
import com.silicongo.george.autotextmessage.DataSet.TextMsgInfo;

/**
 * Created by suxch on 2016/1/3.
 */
public class TextDbAdapter {
    private static final String TAG = "TextDbAdapter";
    private static boolean debugFlag = true;
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
        Cursor cursor = mDb.query(DatabaseHelper.TABLE_NAME_AUTO_TEXT,
                SimplePropertyCollection.getKeyArray(TextMsgInfo.TEXT_DEFAULTS_ALL),
                null, null, null, null, TextMsgInfo.ROW_ID);
        if (debugFlag) {
            if (cursor.moveToFirst()) {
                Log.d(TAG, "Database size is: " + cursor.getCount());
                do {
                    TextMsgInfo ai = new TextMsgInfo(cursor);
                    TextMsgInfo.dumpTextMsgInfo(ai, TAG);
                } while (cursor.moveToNext());
            } else {
                Log.d(TAG, "The data base is empty");
            }
        }
        return cursor;
    }

    public TextMsgInfo getAlarmById(long id) {
        if (id == 0) {
            return new TextMsgInfo();
        }

        Cursor cur = mDb.query(DatabaseHelper.TABLE_NAME_AUTO_TEXT,
                SimplePropertyCollection.getKeyArray(TextMsgInfo.TEXT_DEFAULTS_ALL),
                TextMsgInfo.ROW_ID + "=" + id, null, null, null, TextMsgInfo.ROW_ID);

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
        if (debugFlag == true) {
            if (ai.get(TextMsgInfo.ROW_ID).getInt() != 0) {
                Log.d(TAG, "Try to update id: " + ai.get(TextMsgInfo.ROW_ID).getInt());
                debugCheckItemId(ai.get(TextMsgInfo.ROW_ID).getInt());
            } else {
                Log.d(TAG, "Insert item");
            }
        }
        ai.saveItem(mDb, DatabaseHelper.TABLE_NAME_AUTO_TEXT, TextMsgInfo.ROW_ID);
    }

    public void deleteTextMsgInfo(long _id) {
        if (debugFlag == true) {
            Log.d(TAG, "Delete Item on id: " + _id);
            debugCheckItemId(_id);
        }
        mDb.delete(DatabaseHelper.TABLE_NAME_AUTO_TEXT, TextMsgInfo.ROW_ID + "=" + _id, null);
    }

    public void deleteAllTextMsgInfo() {
        mDb.delete(DatabaseHelper.TABLE_NAME_AUTO_TEXT, null, null);
    }

    public void debugCheckItemId(long _id) {
        if (_id != 0) {
            Log.d(TAG, "Going to find item on id: " + _id);
            Cursor cur = mDb.query(DatabaseHelper.TABLE_NAME_AUTO_TEXT,
                    SimplePropertyCollection.getKeyArray(TextMsgInfo.TEXT_DEFAULTS_ALL),
                    TextMsgInfo.ROW_ID + "=" + _id, null, null, null, TextMsgInfo.ROW_ID);
            cur.moveToFirst();
            if (cur.isAfterLast()) {
                Log.d(TAG, "Can't find the cursor");
            } else {
                TextMsgInfo ai = new TextMsgInfo(cur);
                TextMsgInfo.dumpTextMsgInfo(ai, TAG);
            }
            cur.close();
        } else {
            Log.d(TAG, "Item _id == 0x0");
        }
    }
}
