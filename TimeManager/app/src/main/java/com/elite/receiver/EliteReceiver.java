package com.elite.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.elite.service.Service2;

/**
 * @author MR.ZHANG
 * @create 2018-12-27 14:54
 */
public class EliteReceiver extends BroadcastReceiver {
    private static final String TAG = "EliteReceiver";
    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent mIntent) {
        Log.e(TAG, "Receive Action --> " + mIntent.getAction());
        if (mIntent.getAction().equals(BOOT_COMPLETED)) {
            Intent i = new Intent(context, Service2.class);
            context.startService(i);
        }
    }
}
