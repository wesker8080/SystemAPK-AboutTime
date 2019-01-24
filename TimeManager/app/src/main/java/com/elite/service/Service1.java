package com.elite.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.elite.constant.ServiceConstant;
import com.elite.receiver.TimeChangeReceiver;
import com.elite.utils.CommonUtils;

/**
 *
 * @author wesker
 * @date 2018/6/14 11:34
 */

public class Service1 extends Service {

    private static final String TAG = "Service1";



    /**
     * 使用aidl 启动Service2
     */
    private StrongService startS2 = new StrongService.Stub() {
        @Override
        public void stopService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), Service2.class);
            getBaseContext().stopService(i);
        }

        @Override
        public void startService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), Service2.class);
            getBaseContext().startService(i);
        }
    };

    /**
     * 在内存紧张的时候，系统回收内存时，会回调OnTrimMemory， 重写onTrimMemory当系统清理内存时从新启动Service2
     */
    @Override
    public void onTrimMemory(int level) {
		/*
		 * 启动service2
		 */
        startService2();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG,"service1 destory");
        unregisterReceiver(mTimeChangeReceiver);
        super.onDestroy();
        startService2();
    }
    TimeChangeReceiver mTimeChangeReceiver;
    @Override
    public void onCreate() {
        IntentFilter intentFilter = new IntentFilter();
        //每分钟变化
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        //设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        //设置了系统时间
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        mTimeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(mTimeChangeReceiver, intentFilter);

        /* 注册屏幕唤醒时的广播 */
        IntentFilter mScreenOnFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        registerReceiver(mTimeChangeReceiver, mScreenOnFilter);
        /* 注册机器锁屏时的广播 */
        IntentFilter mScreenOffFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        registerReceiver(mTimeChangeReceiver, mScreenOffFilter);
        startService2();
    }

    /**
     * 判断Service2是否还在运行，如果不是则启动Service2
     */
    private void startService2() {
        Log.d(TAG, "startService2");
        boolean isRun = CommonUtils.isServiceWork(Service1.this,
                ServiceConstant.SERVICE2);
        if (!isRun) {
            try {
                startS2.startService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) startS2;
    }
}
