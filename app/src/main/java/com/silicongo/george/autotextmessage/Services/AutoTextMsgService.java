package com.silicongo.george.autotextmessage.Services;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.silicongo.george.autotextmessage.BuildConfig;
import com.silicongo.george.autotextmessage.DataSet.TextMsgInfo;
import com.silicongo.george.autotextmessage.Database.TextDbAdapter;
import com.silicongo.george.autotextmessage.Debug.FileLog;
import com.silicongo.george.autotextmessage.MainActivity;

import java.util.ArrayList;

/**
 * Created by suxch on 2016/1/2.
 */
public class AutoTextMsgService extends Service {
    private static final String TAG = "AutoTextMsgService";

    public static final String SERVICE_SEND_TEXT_MESSAGE = "com.silicongo.george.SEND_TEXT_MESSAGE";
    public static final String SERVICE_QUERY_TEXT_MESSAGE = "com.silicongo.george.QUERY_TEXT_MESSAGE";

    public static final String NEXT_AVAIL_TEXT_MSG_ID = "nextAvailTextMsgId";
    public static final String SENDING_MSG_COUNT = "sendingMessageCount";

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private TextDbAdapter adapter;
    private ArrayList<TextMsgInfo> mTextMsgInfoList = new ArrayList<>();

    private AutoTextMsgService msgService = this;

    private int sendMsgCount;
    private Integer smsSendStatus;
    private int smsDeliver;

    /* dynamic create receiver */
    private BroadcastReceiver sendReciver;
    private BroadcastReceiver deliverReciver;

    private PendingIntent sentPI;
    private PendingIntent deliveredPI;

    private String SENT = "SMS_SENT";
    private String DELIVERED = "SMS_DELIVERED";

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            TextMsgInfo info = (TextMsgInfo) msg.obj;
            if (info != null) {
                // Normally we would do some work here, like download a file.
                Toast.makeText(msgService, "Send Message: " +
                        info.get(TextMsgInfo.ROW_AVAIL_TEXT_MESSAGE + "0").getString(), Toast.LENGTH_SHORT).show();
                FileLog.d(TAG, "Sending Message: " +
                        info.get(TextMsgInfo.ROW_AVAIL_TEXT_MESSAGE + "0").getString());

                SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                sendMsgCount = settings.getInt(SENDING_MSG_COUNT, 0x0);
                sendMsgCount++;
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(SENDING_MSG_COUNT, sendMsgCount);
                editor.commit();

                startInfoService(null, 0);

                smsSendStatus = -1;
                smsDeliver = -1;
                sendSMS(info.get(TextMsgInfo.ROW_PHONE_NUMBER).getString(),
                        info.get(TextMsgInfo.ROW_AVAIL_TEXT_MESSAGE + "0").getString());
                // Wait for the message sent or fail
                while (true) {
                    synchronized ( smsSendStatus) {
                        if (smsSendStatus != -1) {
                            break;
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                startInfoService(smsSendStatus + ": " + info.get(TextMsgInfo.ROW_PHONE_NUMBER).getString() + "->" +
                                ((smsSendStatus == 0) ? "Success" : "Fail") +
                                ". Msg: " +
                                info.get(TextMsgInfo.ROW_AVAIL_TEXT_MESSAGE + "0").getString() + "->",
                        3600000);

                FileLog.d(TAG, "Sending Message Count: " + sendMsgCount);
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job

            stopSelf(msg.arg2);
        }
    }

    @Override
    public void onCreate() {

        adapter = new TextDbAdapter(this);
        adapter.open();

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        sendReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                final String strSendStatus[] = {"OK", "Generic failure", "Radio off",
                        "Null PDU", "No service", "Unknown"};
                int result = getResultCode();
                if (result == Activity.RESULT_OK) {
                    result = 0x0;
                }
                if ((result < 0) || (result > strSendStatus.length)) {
                    result = strSendStatus.length - 1;
                }
                Toast.makeText(getBaseContext(), strSendStatus[result],
                        Toast.LENGTH_SHORT).show();
                synchronized (smsSendStatus) {
                    smsSendStatus = result;
                }
            }
        };
        registerReceiver(sendReciver, new IntentFilter(SENT));

        deliverReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                final String strDeliverStatus[] = {"SMS delivered", "SMS not delivered"};
                int result = getResultCode();
                switch (result) {
                    case Activity.RESULT_OK:
                        result = 0x0;
                        break;
                    case Activity.RESULT_CANCELED:
                        result = 0x1;
                        break;
                }
                Toast.makeText(getBaseContext(), strDeliverStatus[result],
                        Toast.LENGTH_SHORT).show();
                synchronized ((Object) smsDeliver) {
                    smsDeliver = result;
                }
            }
        };
        registerReceiver(deliverReciver, new IntentFilter(DELIVERED));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);

        updateDataSet();

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = 0x0;
        msg.arg2 = startId;
        msg.obj = null;

        // Debug only, record the action invoke the services.
        if (BuildConfig.DEBUG) {
            if (intent != null) {
                String action = intent.getAction();
                if (action == null) {
                    action = "NULL";
                }
                FileLog.d(TAG, "Start Activity By Action: " + action);
            } else {
                FileLog.d(TAG, "Start Activity By Null intent");
            }
        }

        if ((intent != null) && (intent.getAction() == SERVICE_SEND_TEXT_MESSAGE)) {
            // For each start request, send a message to start a job and deliver the
            // start ID so we know which request we're stopping when we finish the job
            msg.arg1 = settings.getInt(NEXT_AVAIL_TEXT_MSG_ID, 0x0);
            if (msg.arg1 != 0x0) {
                msg.obj = getByID(msg.arg1);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(NEXT_AVAIL_TEXT_MSG_ID, 0x0);
                editor.commit();
            }
        }
        Intent broadcastIntent = new Intent(SERVICE_SEND_TEXT_MESSAGE);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, broadcastIntent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(pi);

        TextMsgInfo info = getNextMsgSendPos();
        if (info != null) {
            long offset = System.currentTimeMillis();
            long relative_offset = TextMsgInfo.getOffsetOfCurrentTime(info);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(NEXT_AVAIL_TEXT_MSG_ID, info.get(TextMsgInfo.ROW_ID).getInt());
            editor.commit();

            offset += relative_offset * 1000;

            am.set(AlarmManager.RTC_WAKEUP, offset, pi);

            FileLog.d(TAG, "Alarm time is set to: " + (relative_offset / 3600)
                    + ":" + ((relative_offset / 60) % 60) +
                    ", Offset in second: " + relative_offset);
        }
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        adapter.close();
        unregisterReceiver(sendReciver);
        unregisterReceiver(deliverReciver);
        Toast.makeText(this, "Service Done", Toast.LENGTH_SHORT).show();
        if (BuildConfig.DEBUG) {
            FileLog.d(TAG, "Service Done");
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    public void updateDataSet() {
        mTextMsgInfoList.clear();
        Cursor cursor = adapter.fetchAllTextMessages();
        if (cursor.moveToFirst()) {
            mTextMsgInfoList.add(new TextMsgInfo(cursor));
            while (cursor.moveToNext()) {
                mTextMsgInfoList.add(new TextMsgInfo(cursor));
            }
        }
        cursor.close();
    }

    public TextMsgInfo getByID(int id) {
        TextMsgInfo info = null;
        int i;

        for (i = 0; i < mTextMsgInfoList.size(); i++) {
            info = mTextMsgInfoList.get(i);
            if (info.get(TextMsgInfo.ROW_ID).getInt() == id) {
                break;
            }
        }

        if (i == mTextMsgInfoList.size()) {
            info = null;
        }

        return info;
    }

    public TextMsgInfo getNextMsgSendPos() {
        TextMsgInfo availTextMsgInfo = null;
        long current_offset = 0, last_offset = 0;
        long match_item_count = 0;

        if (mTextMsgInfoList.size() > 0) {
            TextMsgInfo compareTextMsgInfo;
            for (int i = 0; i < mTextMsgInfoList.size(); i++) {
                compareTextMsgInfo = mTextMsgInfoList.get(i);
                current_offset = TextMsgInfo.getOffsetOfCurrentTime(compareTextMsgInfo);
                if (last_offset == 0) {
                    last_offset = current_offset;
                    availTextMsgInfo = compareTextMsgInfo;
                    match_item_count = 0x0;
                } else {
                    if (last_offset > current_offset) {
                        last_offset = current_offset;
                        availTextMsgInfo = compareTextMsgInfo;
                        match_item_count = 0x0;
                    } else if (last_offset == current_offset) {
                        match_item_count++;
                    }
                }
                //Log.d(TAG, "Item: " + i + ", Offset: " + current_offset);
            }
        }

        if (availTextMsgInfo != null) {
            FileLog.d(TAG, "Find Valid Message at: " + availTextMsgInfo.get(TextMsgInfo.ROW_TIME_HOUR).getInt()
                    + ":" + availTextMsgInfo.get(TextMsgInfo.ROW_TIME_MINUTE).getInt()
                    + ", Data: " + availTextMsgInfo.get(TextMsgInfo.ROW_AVAIL_TEXT_MESSAGE + "0").getString());
        }

        return availTextMsgInfo;
    }

    public void startInfoService(String info, int timeToDisplay) {
        Intent intent = new Intent(this, InfoService.class);
        if (info == null) {
            intent.setAction(InfoService.ACTION_PLAY);
        } else {
            intent.setAction(InfoService.ACTION_INFO);
            intent.putExtra(InfoService.ACTION_INFO_MSG, info);
            intent.putExtra(InfoService.ACTION_INFO_TIME, timeToDisplay);
        }
        startService(intent);
    }
}
