package com.silicongo.george.autotextmessage.DataSet;

import android.os.Parcel;
import android.os.Parcelable;

import org.w3c.dom.Text;

/**
 * Created by suxch on 2016/1/16.
 */
public class TextMsgInfoParcelable implements Parcelable {
    public TextMsgInfo mData;

    public int describeContents() {
        return 0;
    }

    // 写数据进行保存
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData.get(TextMsgInfo.ROW_ID).getInt());
        out.writeString(mData.get(TextMsgInfo.ROW_PHONE_NUMBER).getString());

        out.writeInt(mData.get(TextMsgInfo.ROW_WEEK).getInt());
        out.writeInt(mData.get(TextMsgInfo.ROW_TIME_HOUR).getInt());
        out.writeInt(mData.get(TextMsgInfo.ROW_TIME_MINUTE).getInt());

        out.writeBooleanArray(new boolean[]{mData.get(TextMsgInfo.ROW_ID_REPEATABLE).getBool()});
        out.writeString(mData.get(TextMsgInfo.ROW_AVAIL_TEXT_MESSAGE + "0").getString());
        out.writeInt(mData.get(TextMsgInfo.ROW_SIM_CARD).getInt());
        out.writeString(mData.get(TextMsgInfo.ROW_TEXT_TAG).getString());
        out.writeBooleanArray(new boolean[]{mData.get(TextMsgInfo.ROW_ENABLE).getBool()});
    }

    // 用来创建自定义的Parcelable的对象
    public static final Parcelable.Creator<TextMsgInfoParcelable> CREATOR
            = new Parcelable.Creator<TextMsgInfoParcelable>() {
        public TextMsgInfoParcelable createFromParcel(Parcel in) {
            return new TextMsgInfoParcelable(in);
        }

        public TextMsgInfoParcelable[] newArray(int size) {
            return new TextMsgInfoParcelable[size];
        }
    };

    public TextMsgInfoParcelable(TextMsgInfo info){
        mData = info;
    }

    // 读数据进行恢复
    private TextMsgInfoParcelable(Parcel in) {
        boolean tmp[];
        mData = new TextMsgInfo();
        mData.get(TextMsgInfo.ROW_ID).set(in.readInt());
        mData.get(TextMsgInfo.ROW_PHONE_NUMBER).set(in.readString());

        mData.get(TextMsgInfo.ROW_WEEK).set(in.readInt());

        mData.get(TextMsgInfo.ROW_TIME_HOUR).set(in.readInt());
        mData.get(TextMsgInfo.ROW_TIME_MINUTE).set(in.readInt());

        tmp = new boolean[1];
        in.readBooleanArray(tmp);
        mData.get(TextMsgInfo.ROW_ID_REPEATABLE).set(tmp[0]);
        mData.get(TextMsgInfo.ROW_AVAIL_TEXT_MESSAGE + "0").set(in.readString());
        mData.get(TextMsgInfo.ROW_SIM_CARD).set(in.readInt());
        mData.get(TextMsgInfo.ROW_TEXT_TAG).set(in.readString());
        in.readBooleanArray(tmp);
        mData.get(TextMsgInfo.ROW_ENABLE).set(tmp[0]);
    }
}
