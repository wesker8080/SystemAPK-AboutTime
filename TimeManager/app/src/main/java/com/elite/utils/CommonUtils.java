package com.elite.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.elite.R;
import com.elite.TimeLockApplication;
import com.elite.callback.TimeLockSettingCallBack;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static android.content.Context.BATTERY_SERVICE;

/**
 * Created by wesker on 2018/6/13 17:44.
 */

public class CommonUtils {
    private static final String TAG = "CommonUtils";
    public static boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }
    
    public static void showToast(Context mContext, int str) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }

    public static void showToastInService(final Context mContext, final int str) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(() -> Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show());
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：com.beidian.test.service.BasicInfoService ）
     * @return
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    public static int getBatteryPercent(Context mContext) {
        BatteryManager batteryManager = (BatteryManager) mContext.getSystemService(BATTERY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }
        return 0;
    }

    public static long readSDCard() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            long availCount = sf.getAvailableBlocks();
            return availCount * blockSize;
        }
        return 0;
    }


    //判断文件是否存在
    public static File fileIsExists(String path, String strFile) {
        try {
            File f = new File(path, strFile);
            if (f.exists()) {
                return f;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public void showEntryPasswordDialog(final Context context, int tips) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Download Fail");
//        builder.setMessage("Make sure your network is already connected?");
//        builder.setPositiveButton("OK", null);
        View mInflate = View.inflate(context, R.layout.dialog_locking, null);
        EditText etPassword = mInflate.findViewById(R.id.et_enter_password);
        TextView tvTip = mInflate.findViewById(R.id.tv_entry_password_tip);
        tvTip.setText(tips);
        builder.setView(mInflate);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TimeLockApplication.dataCache.getTimeLockPassword().equals(etPassword.getText().toString())) {
                    if (alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        alertDialog.show();
    }
    public void showMessageDialog(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.message_dialog_title);
        builder.setMessage(R.string.message_dialog_content);
        builder.setNegativeButton(R.string.message_dialog_cancel, null);
        builder.setPositiveButton(R.string.message_dialog_ok, (v,w) -> {
            Optional<TimeLockSettingCallBack> mBack = Optional.ofNullable(mTimeLockSettingCallBack);
            mBack.ifPresent(TimeLockSettingCallBack::SetTimeLockSecretCode);
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }

    /**
     * 隐藏状态栏
     * @param mActivity mActivity
     */
    private void applyCompat(Activity mActivity) {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        mActivity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private static TimeLockSettingCallBack mTimeLockSettingCallBack;
    public void setTimeLockCallBackListener(TimeLockSettingCallBack mTimeLockSettingCallBack) {
        this.mTimeLockSettingCallBack = mTimeLockSettingCallBack;
    }

}
