package com.elite.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.SystemProperties;

import com.elite.R;
import com.elite.TimeLockApplication;
import com.elite.constant.TimeLockConstant;


import java.util.Optional;

/**
 * @author MR.ZHANG
 * @create 2019-01-16 18:38
 */
public class TimeLockDialogUtil extends Dialog{
    private final String TAG = "TimeLockDialogUtil";

    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    private Context context;
    private View view;
    private int title;
    private int message;
    private boolean isEnable = false;
    private String mTimeLockPassword;
    private boolean isOnCreate;
    private boolean isLocking;
    public TimeLockDialogUtil(Context context, View view, boolean isOnCreate, boolean isLocking) {
        super(context, R.style.Transparent);
        this.context = context;
        this.view = view;
        //setOwnerActivity((Activity)context);
        mTimeLockPassword = TimeLockApplication.dataCache.getTimeLockPassword();
        this.isOnCreate = isOnCreate;
        this.isLocking = isLocking;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 测试禁用home
        //getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvMessage = view.findViewById(R.id.tv_to_unlock_tip);
        RelativeLayout topView = view.findViewById(R.id.rl_top_view);
        if (isLocking) {
            topView.setVisibility(View.GONE);
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            SystemProperties.set(TimeLockConstant.HOME_KEY_INTERCEPT, TimeLockConstant.HOME_VALUE_INTERCEPT);
        } else {
            topView.setVisibility(View.VISIBLE);
        }
        Optional.ofNullable(tvTitle).ifPresent(x -> {
            Integer mTitle = Optional.ofNullable(title).orElseGet(() -> R.string.system_time_lock_summary);
            x.setText(mTitle);
        });
        Optional.ofNullable(tvMessage).ifPresent(x -> {
            Integer mMessage = Optional.ofNullable(message).orElseGet(() -> R.string.text_entry_password);
            x.setText(mMessage);
        });
        ImageView backButton = view.findViewById(R.id.iv_title_back);
        Optional.ofNullable(backButton).ifPresent(x -> {
            x.setEnabled(isEnable);
            x.setOnClickListener(v -> {
                if (isShowing() ) {
                    if (dialogListener != null) {
                        dialogListener.onCancel(isOnCreate);
                    }
                }
            });
        });
        EditText etPassword = view.findViewById(R.id.et_unlock);
        Optional.ofNullable(etPassword).ifPresent(x -> x.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isCorrectPassword(x)) {
                    if (isShowing()) {
                        if (dialogListener != null) {
                            dialogListener.onSuccess(isOnCreate);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        }));
        setPromptWin(this);
        setContentView(view);
        setCanceledOnTouchOutside(false);
    }
    public void setDialogTitle(int title) {
        this.title = title;
    }
    public void setDialogMessage(int message) {
        this.message = message;
    }
    public void setBackButtonEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }
    public void setPromptWin(TimeLockDialogUtil dia) {
        Optional<Window> win = Optional.ofNullable(dia.getWindow());
        win.ifPresent(x -> {
            WindowManager.LayoutParams lp = x.getAttributes();
            x.setGravity(Gravity.LEFT | Gravity.TOP);
            lp.x = (int) (DensityUtils.getW(context)* 0.141333);
            lp.y = (int) (DensityUtils.getH(context)* 0.293663);
            x.setAttributes(lp);
        });

    }
    private boolean isCorrectPassword(EditText x) {
       return !TimeLockConstant.DEFAULT.equals(mTimeLockPassword) && mTimeLockPassword.equals(x.getText().toString());
    }

    private DialogListener dialogListener;
    public interface DialogListener {
       void onCancel(boolean isDismiss);
        void onSuccess(boolean isDismiss);
    }
    public void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_HOME) {
            Log.d(TAG, "home key press");
        }
        return super.onKeyDown(keyCode, event);
    }

}
