package com.silicongo.george.autotextmessage.DataSet;

import android.database.Cursor;

import java.io.BufferedReader;

/**
 * Created by suxch on 2016/1/2.
 */
public class TextMsgInfo extends SimplePropertyCollection {
    private static final String TAG = "TextMsgInfo";

    public TextMsgInfo() {
        super(TEXT_DEFAULTS_ALL);
    }

    public TextMsgInfo(BufferedReader reader, boolean skipId) throws Exception {
        super(TEXT_DEFAULTS_ALL, reader, skipId, ROW_ID);
    }

    public TextMsgInfo(SimpleProperty[] defaults, Cursor cur) {
        super(defaults, cur);
    }

    public TextMsgInfo(Cursor cur) {
        super(TEXT_DEFAULTS_ALL, cur);
    }

    /* Used for sql */
    public static final String ROW_ID = "_id";
    public static final String ROW_PHONE_NUMBER = "PhoneNumber";
    public static final String ROW_WEEK_SUNDAY = "SundayEnable";
    public static final String ROW_WEEK_MONDAY = "MondayEnable";
    public static final String ROW_WEEK_TUESDAY = "TuesdayEnable";
    public static final String ROW_WEEK_WEDNESDAY = "WednesdayEnable";
    public static final String ROW_WEEK_THURSDAY = "ThursdayEnable";
    public static final String ROW_WEEK_FRIDAY = "FridayEnable";
    public static final String ROW_WEEK_SATURDAY = "SaturdayEnable";
    public static final String ROW_TIME_HOUR = "Hour";
    public static final String ROW_TIME_MINUTE = "Minute";
    public static final String ROW_ID_REPEATABLE = "Repeatable";
    public static final String ROW_AVAIL_TEXT_MESSAGE = "AvailTextMessage";
    public static final String ROW_SIM_CARD = "SimCard";
    public static final String ROW_TEXT_TAG = "TextTag";
    public static final String ROW_ENABLE = "Enalbe";

    public static final SimpleProperty[] TEXT_DEFAULTS_ALL = new SimpleProperty[]{
            new SimpleProperty(ROW_ID, 0),
            new SimpleProperty(ROW_PHONE_NUMBER, ""),
            new SimpleProperty(ROW_WEEK_SUNDAY, false),
            new SimpleProperty(ROW_WEEK_MONDAY, false),
            new SimpleProperty(ROW_WEEK_TUESDAY, false),
            new SimpleProperty(ROW_WEEK_WEDNESDAY, false),
            new SimpleProperty(ROW_WEEK_THURSDAY, false),
            new SimpleProperty(ROW_WEEK_FRIDAY, false),
            new SimpleProperty(ROW_WEEK_SATURDAY, false),
            new SimpleProperty(ROW_TIME_HOUR, 12),
            new SimpleProperty(ROW_TIME_MINUTE, 0),
            new SimpleProperty(ROW_ID_REPEATABLE, false),
            new SimpleProperty(ROW_AVAIL_TEXT_MESSAGE + "0", ""),
            new SimpleProperty(ROW_AVAIL_TEXT_MESSAGE + "1", ""),
            new SimpleProperty(ROW_AVAIL_TEXT_MESSAGE + "2", ""),
            new SimpleProperty(ROW_AVAIL_TEXT_MESSAGE + "3", ""),
            new SimpleProperty(ROW_AVAIL_TEXT_MESSAGE + "4", ""),
            new SimpleProperty(ROW_AVAIL_TEXT_MESSAGE + "5", ""),
            new SimpleProperty(ROW_AVAIL_TEXT_MESSAGE + "6", ""),
            new SimpleProperty(ROW_AVAIL_TEXT_MESSAGE + "7", ""),
            new SimpleProperty(ROW_AVAIL_TEXT_MESSAGE + "8", ""),
            new SimpleProperty(ROW_SIM_CARD, 0),
            new SimpleProperty(ROW_TEXT_TAG, ""),
            new SimpleProperty(ROW_ENABLE, false)
    };

    public static String checkTextMsgInfoValid(TextMsgInfo info) {
        if(info.get(ROW_PHONE_NUMBER).getString() == null){
            return "Phone Number has not been set";
        }
        if((info.get(ROW_WEEK_SUNDAY).getBool() == false) &&
                (info.get(ROW_WEEK_MONDAY).getBool() == false) &&
                (info.get(ROW_WEEK_TUESDAY).getBool() == false) &&
                (info.get(ROW_WEEK_WEDNESDAY).getBool() == false) &&
                (info.get(ROW_WEEK_THURSDAY).getBool() == false) &&
                (info.get(ROW_WEEK_FRIDAY).getBool() == false) &&
                (info.get(ROW_WEEK_SATURDAY).getBool() == false)){
            return "No day of week has been set";
        }
        if(info.get(ROW_AVAIL_TEXT_MESSAGE+"0") == null){
            return "message has not been set";
        }
        return null;
    }
}
