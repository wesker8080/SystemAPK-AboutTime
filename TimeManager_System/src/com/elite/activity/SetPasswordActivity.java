package com.elite.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.elite.R;
import com.elite.TimeLockApplication;
import com.elite.constant.TimeLockConstant;
import com.elite.utils.CommonUtils;

import java.util.Optional;

/**
 * @author MR.ZHANG
 * @create 2019-01-16 15:13
 */
public class SetPasswordActivity extends Activity {
    private static final String TAG = "SetPasswordActivity";
    private EditText etOldPassword;
    private EditText etPassword;
    private EditText etPasswordAgain;
    private TextView tvTip;
    private TextView tvTitle;
    private Button btnCancel;
    private Button btnEnsure;
    private ImageView ivTitleBack;
    private String preferenceFlag;

    private final int MIN_PASSWORD_COUNT = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_set_password);
        Optional.ofNullable(getActionBar()).ifPresent(x -> {
            x.setDisplayHomeAsUpEnabled(true);
            x.setTitle(R.string.entry_password_to_unlock_title);
            x.setHomeButtonEnabled(true);
        });
        Intent mIntent = getIntent();
        preferenceFlag = mIntent.getStringExtra(TimeLockConstant.ACTIVITY);
        initView();
        initViewController();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "home key");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViewController() {
        if (isModifyPasswordPreferenceEntry()) {
            etOldPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!passwordIsCorrect()) {
                        tvTip.setText(R.string.entry_old_password_error);
                        tvTip.setTextColor(getResources().getColor(R.color.colorPasswordRed, null));
                        btnEnsure.setEnabled(false);
                    } else {
                        tvTip.setText("");
                        btnEnsure.setEnabled(true);
                        tvTip.setTextColor(getResources().getColor(R.color.colorPasswordRed, null));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int textCounts = s.length();
                if (textCounts == 0 && etPasswordAgain.getText().length() == 0) {
                    tvTip.setText(R.string.entry_password_tip);
                    tvTip.setTextColor(getResources().getColor(R.color.colorPasswordTip, null));
                } else if(textCounts >= MIN_PASSWORD_COUNT && etPasswordAgain.getText().length() == 0){
                    tvTip.setText(R.string.entry_password_again);
                    tvTip.setTextColor(getResources().getColor(R.color.colorPasswordTip, null));
                } else if (textCounts == 0 && etPasswordAgain.getText().length() != 0) {
                    if (isModifyPasswordPreferenceEntry()) {
                        tvTip.setText(R.string.entry_new_password_inconsistent);
                    } else {
                        tvTip.setText(R.string.entry_password_inconsistent);
                    }
                    tvTip.setTextColor(getResources().getColor(R.color.colorPasswordRed, null));
                } else if(textCounts >= MIN_PASSWORD_COUNT && etPasswordAgain.getText().length() != 0){
                    if (isModifyPasswordPreferenceEntry()) {
                        tvTip.setText(R.string.entry_new_password_inconsistent);
                    } else {
                        tvTip.setText(R.string.entry_password_inconsistent);
                    }
                    tvTip.setTextColor(getResources().getColor(R.color.colorPasswordRed, null));
                } else {
                    tvTip.setText(R.string.entry_password_tip);
                    tvTip.setTextColor(getResources().getColor(R.color.colorPasswordRed, null));
                }
                if (isConsistentPassword()) {
                    tvTip.setText("");
                    btnEnsure.setEnabled(true);
                } else {
                    btnEnsure.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        showKeyBoard();
        etPasswordAgain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isConsistentPassword()) {
                    tvTip.setText("");
                    btnEnsure.setEnabled(true);
                } else if(etPassword.getText().length() < MIN_PASSWORD_COUNT) {
                    btnEnsure.setEnabled(false);
                    tvTip.setText(R.string.entry_password_tip);
                    tvTip.setTextColor(getResources().getColor(R.color.colorPasswordRed, null));
                } else {
                    btnEnsure.setEnabled(false);
                    if (isModifyPasswordPreferenceEntry()) {
                        tvTip.setText(R.string.entry_new_password_inconsistent);
                    } else {
                        tvTip.setText(R.string.entry_password_inconsistent);
                    }
                    tvTip.setTextColor(getResources().getColor(R.color.colorPasswordRed, null));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tvTitle.setText(R.string.entry_password_to_unlock_title);
        ivTitleBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        btnEnsure.setOnClickListener(v -> {
            if (isModifyPasswordPreferenceEntry() && isConsistentPassword() && passwordIsCorrect()) {
                TimeLockApplication.dataCache.setTimeLockPassword(etPassword.getText().toString());
            } else {
                if (isConsistentPassword()) {
                    TimeLockApplication.dataCache.setTimeLockPassword(etPassword.getText().toString());
                }
            }
            CommonUtils.showToastInService(SetPasswordActivity.this, R.string.toast_success);
            Intent mIntent = new Intent();
            setResult(RESULT_OK, mIntent);
            finish();
        });
    }

    private boolean isConsistentPassword() {
        return etPassword.getText().length() >= 4 && etPasswordAgain.getText().length() >= 4 && etPassword.getText().toString().equals(etPasswordAgain.getText().toString());
    }
    private void initView() {
        etOldPassword = findViewById(R.id.et_old_password);
        etPassword = findViewById(R.id.et_password);
        etPasswordAgain = findViewById(R.id.et_password_again);
        tvTip = findViewById(R.id.tv_tip);
        btnCancel = findViewById(R.id.btn_cancel);
        btnEnsure = findViewById(R.id.btn_ensure);
        tvTitle = findViewById(R.id.tv_title);
        ivTitleBack = findViewById(R.id.iv_title_back);
        if (isModifyPasswordPreferenceEntry()) {
            etOldPassword.setVisibility(View.VISIBLE);
            etOldPassword.setHint(R.string.entry_old_password);
        }
    }

    /**
     *  显示键盘
     */
    private void showKeyBoard() {
        if (isModifyPasswordPreferenceEntry()) {
            etOldPassword.setFocusable(true);
            etOldPassword.setFocusableInTouchMode(true);
            etOldPassword.requestFocus();
        } else {
            etPassword.setFocusable(true);
            etPassword.setFocusableInTouchMode(true);
            etPassword.requestFocus();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * 是否是从修改密码进入的
     * @return true为从修改密码进入 false为未设置密码进入
     */
    private boolean isModifyPasswordPreferenceEntry() {
        if (preferenceFlag != null) {
            return preferenceFlag.equals(TimeLockConstant.MODIFY_PASSWORD);
        }
        return false;
    }

    /**
     * 检验密码是否正确
     * @return
     */
    private boolean passwordIsCorrect() {
       return etOldPassword.getText().toString().equals(TimeLockApplication.dataCache.getTimeLockPassword());
    }
}
