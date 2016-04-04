package com.silicongo.george.autotextmessage.Reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.silicongo.george.autotextmessage.BuildConfig;
import com.silicongo.george.autotextmessage.Debug.FileLog;
import com.silicongo.george.autotextmessage.Services.AutoTextMsgService;

/**
 * Created by suxch on 2016/2/14.
 */
public class SendingMsgReciver extends BroadcastReceiver {
    private static final String TAG = "SendingMsgReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        FileLog.d(TAG, "Wakeup Event, Sending Message Receiver");

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AppAlarmReceiver");
        wl.acquire(120000);

        if(BuildConfig.DEBUG){
            if(intent != null){
                FileLog.d(TAG, intent.getAction());
            }else{
                FileLog.d(TAG, "null intent");
            }
        }

        Intent i = new Intent(context, AutoTextMsgService.class);
        i.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        if((intent != null) &&
                (intent.getAction() == AutoTextMsgService.SERVICE_SEND_TEXT_MESSAGE)){
            i.setAction(AutoTextMsgService.SERVICE_SEND_TEXT_MESSAGE);
        }else{
            i.setAction(AutoTextMsgService.SERVICE_QUERY_TEXT_MESSAGE);
        }
        context.startService(i);
    }
}
