package com.silicongo.george.autotextmessage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by suxch on 2016/1/3.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    public static final String TABLE_NAME_AUTO_TEXT = "auto_text";
    public static final String DATABASE_NAME = "dbAutoTextMsgInfo";
    public static final int DATABASE_VERSION = 1;
    public static final String[] DATABASE_DROP = {
            "DROP TABLE IF EXISTS " + TABLE_NAME_AUTO_TEXT
    };

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SimplePropertyCollection.getCreateTableStatement(TextMsgInfo.TEXT_DEFAULTS_ALL, TABLE_NAME_AUTO_TEXT));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        for (int i = 0; i < DATABASE_DROP.length; i++) {
            db.execSQL(DATABASE_DROP[i]);
        }
        onCreate(db);
    }
}
