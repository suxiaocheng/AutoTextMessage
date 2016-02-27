package com.silicongo.george.autotextmessage.Misc;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.silicongo.george.autotextmessage.DataSet.TextMsgInfo;
import com.silicongo.george.autotextmessage.Database.TextDbAdapter;
import com.silicongo.george.autotextmessage.Debug.FileLog;
import com.silicongo.george.autotextmessage.MainActivity;
import com.silicongo.george.autotextmessage.R;

public class InfoService extends Service implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    private static final String TAG = "InfoService";

    /* Action */
    public static final String ACTION_PLAY = "com.silicongo.george.autotextmessage.PLAY";
    public static final String ACTION_INFO = "com.silicongo.george.autotextmessage.INFO";

    /**/
    public static final String ACTION_INFO_TIME = "com.silicongo.george.autotextmessage.INFO.TIME";
    public static final String ACTION_INFO_MSG = "com.silicongo.george.autotextmessage.INFO.MSG";

    MediaPlayer mMediaPlayer = null;
    private int playStartId = 0;

    private HandlerThread thread;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private Object lockObj = new Object();
    private Object handlerNeedExit = false;

    public InfoService() {
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            String displayMessage = (String) msg.obj;
            long endTimeMillis = System.currentTimeMillis() + msg.arg1;

            synchronized (handlerNeedExit) {
                handlerNeedExit = false;
            }

            startForeground(displayMessage, msg.arg2);

            while (endTimeMillis > System.currentTimeMillis()) {
                synchronized (handlerNeedExit) {
                    if (handlerNeedExit == true) {
                        handlerNeedExit = false;
                        break;
                    }
                }
                synchronized (lockObj) {
                    try {
                        lockObj.wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }

            stopForeground(true);

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg2);
        }
    }

    public void startForeground(String info, int id) {
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("AutoSendMessage")
                .setContentText(info)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.info))
                .build();

        startForeground(id, notification);
    }

    @Override
    public void onCreate() {

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        thread = new HandlerThread(TAG);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY)) {
            if (playStartId != 0x0) {
                releaseMediaPlayer();
            }
            mMediaPlayer = MediaPlayer.create(this, R.raw.sendmsg);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.start();

            playStartId = startId;

            Log.d(TAG, "Play send msg sound");
        } else if (intent.getAction().equals(ACTION_INFO)) {
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = intent.getIntExtra(ACTION_INFO_TIME, 0);
            msg.arg2 = startId;
            msg.obj = intent.getStringExtra(ACTION_INFO_MSG);

            if ((msg.obj != null) && (((String) msg.obj).compareTo("") != 0) && (msg.arg1 > 0)) {
                mServiceHandler.removeCallbacksAndMessages(null);
                synchronized (handlerNeedExit) {
                    handlerNeedExit = true;
                }

                mServiceHandler.sendMessage(msg);

                Log.d(TAG, "dispaly msg->" + (String) msg.obj);
            } else {
                stopSelf(msg.arg2);
            }
        } else {
            stopSelf(startId);
        }
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // ... react appropriately ...
        // The MediaPlayer has moved to the Error state, must be reset!

        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (playStartId != 0) {
            int id = playStartId;
            releaseMediaPlayer();
            playStartId = 0x0;
            stopSelf(id);
        }
    }

    @Override
    public void onDestroy() {
        releaseMediaPlayer();
        /* Empty the message queue and quit the thread */
        mServiceHandler.removeCallbacksAndMessages(null);
        thread.quit();
        Log.d(TAG, "Destroy");
    }

    public void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
