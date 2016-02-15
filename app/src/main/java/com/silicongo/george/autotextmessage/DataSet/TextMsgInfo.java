package com.silicongo.george.autotextmessage.DataSet;

import android.database.Cursor;
import android.util.Log;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.util.Calendar;

/**
 * Created by suxch on 2016/1/2.
 */
public class TextMsgInfo extends SimplePropertyCollection {
    private static final String TAG = "TextMsgInfo";

    public TextMsgInfo() {
        super(TEXT_DEFAULTS_ALL);

        Calendar c = Calendar.getInstance();
        get(ROW_TIME_HOUR).set(c.get(Calendar.HOUR_OF_DAY));
        get(ROW_TIME_MINUTE).set(c.get(Calendar.MINUTE));

        get(ROW_WEEK).set(1<<(c.get(Calendar.DAY_OF_WEEK)-1));
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
    public static final String ROW_WEEK = "Week";
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
            new SimpleProperty(ROW_WEEK, 0x0),
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
        if (info.get(ROW_PHONE_NUMBER).getString() == null) {
            return "Phone Number has not been set";
        }
        if (info.get(ROW_WEEK).getInt() == 0x0) {
            return "No day of week has been set";
        }
        if (info.get(ROW_AVAIL_TEXT_MESSAGE + "0") == null) {
            return "message has not been set";
        }
        return null;
    }

    public static void dumpTextMsgInfo(TextMsgInfo info, String tag) {
        Log.d(tag, ROW_ID + ": " + info.get(ROW_ID).getInt() + ", " +
                ROW_PHONE_NUMBER + ": " + info.get(ROW_PHONE_NUMBER).getString() + ", " +
                ROW_AVAIL_TEXT_MESSAGE + "0" + ": " + info.get(ROW_AVAIL_TEXT_MESSAGE + "0").getString() + ", " +
                ROW_TEXT_TAG + ": " + info.get(ROW_TEXT_TAG).getString());
    }

    public static long getOffsetOfCurrentTime(TextMsgInfo info) {
        final long minuteInOneWeek = 7*24*60;
        Calendar c = Calendar.getInstance();
        long offset = -1;
        if (info.get(TextMsgInfo.ROW_ENABLE).getBool() == false) {
            return -1;
        }
        long current_offset = ((c.get(Calendar.DAY_OF_WEEK) - 1) * 24 +
                c.get(Calendar.HOUR_OF_DAY)) * 60 + c.get(Calendar.MINUTE);
        int hour = info.get(TextMsgInfo.ROW_TIME_HOUR).getInt();
        int minute = info.get(TextMsgInfo.ROW_TIME_MINUTE).getInt();
        int dayOfWeek = info.get(TextMsgInfo.ROW_WEEK).getInt();
        if ((dayOfWeek & (0x1 << 7)) != 0x0) {
            /* One Time Event */
            int currentTimeOffset = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
            int targetTimeOffset = hour*60 + minute;
            dayOfWeek = (0x1<<c.get(Calendar.DAY_OF_WEEK));
            if(currentTimeOffset < targetTimeOffset){
                dayOfWeek >>= 1;
            }
        }else if((dayOfWeek & (0x1<<8)) != 0x0){
            dayOfWeek = (0x1f<<1);
        }else if((dayOfWeek & (0x1<<9)) != 0x0){
            dayOfWeek = (0x1) | (0x1<<7);
        }else if((dayOfWeek & (0x1<<10)) != 0x0){
            dayOfWeek = 0x7f;
        }
        long target_offset[] = new long[7];
        for (int i = 0; i < 7; i++) {
            if ((dayOfWeek & (0x1 << i)) != 0x0) {
                target_offset[i] = (i * 24 + hour) * 60 + minute;
            } else {
                target_offset[i] = -1;
            }
        }
        int pos = -1;
        int firstValid = -1;
        for(int i=0; i<7; i++){
            if(target_offset[i] != -1) {
                if(firstValid == -1) {
                    firstValid = i;
                }
                if (target_offset[i] - current_offset > 0) {
                    pos = i;
                    break;
                }
            }
        }
        if(pos != -1){
            offset = target_offset[pos] - current_offset;
        }else{
            offset = (target_offset[firstValid]) + minuteInOneWeek - current_offset;
        }
        return offset;
    }

    public static boolean compareTimeTextMsgInfo(TextMsgInfo src, TextMsgInfo des) {
        boolean boolRet = false;
        long srcOffset;
        long desOffset;

        srcOffset = getOffsetOfCurrentTime(src);
        desOffset = getOffsetOfCurrentTime(des);

        if(srcOffset < desOffset){
            boolRet = true;
        }

        return boolRet;
    }

    public static String getWeekEnableString(TextMsgInfo info) {
        int weekEnable = info.get(TextMsgInfo.ROW_WEEK).getInt();
        String strRet;
        if (weekEnable == 0) {
            strRet = "None";
        } else if ((weekEnable & (0x1 << 7)) != 0) {
            strRet = "One Time Event";
        } else if ((weekEnable & (0x1 << 8)) != 0) {
            strRet = "Weekday";
        } else if ((weekEnable & (0x1 << 9)) != 0) {
            strRet = "Weekend";
        } else if ((weekEnable & (0x1 << 10)) != 0) {
            strRet = "Everyday";
        } else {
            strRet = "";
            String strNameOfWeek[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
                    "Friday", "Saturday"};
            for (int i = 0; i < 7; i++) {
                if ((weekEnable & (0x1 << i)) != 0) {
                    if (strRet.compareTo("") != 0) {
                        strRet += " ";
                    }
                    strRet += strNameOfWeek[i];
                }
            }
        }
        return strRet;
    }
}
