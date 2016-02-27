package com.silicongo.george.autotextmessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.silicongo.george.autotextmessage.Debug.FileLog;

public class BootReciever extends BroadcastReceiver {
	private static final String TAG = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		FileLog.d(TAG, "Boot Event, Query for the latest alarm");

		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AppAlarmReceiver");
		wl.acquire(120000);

		if(BuildConfig.DEBUG){
			Intent i = new Intent(context, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}else {
			Intent i = new Intent(context, AutoTextMsgService.class);
			intent.setAction(AutoTextMsgService.SERVICE_QUERY_TEXT_MESSAGE);
			context.startService(i);
		}
	}
}
