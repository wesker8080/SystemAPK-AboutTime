package com.elite.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elite.R;
import com.elite.customview.WheelView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author MR.ZHANG
 * @create 2019-01-17 11:45
 */
public class TimeSetDialogUtil implements View.OnClickListener {


    private Context mContext;

    private Dialog dialog;

    private onClickCallBack onClickCallBack = null;
    private TextView tvEnsure;
    private TextView tvCancel;
    private WheelView wvHour;
    private WheelView wvMin;
    private Calendar calendar;

    private List<String> minList = new ArrayList<>();
    private List<String> hourList = new ArrayList<>();

    public TimeSetDialogUtil(Context context) {
        mContext = context;
        calendar = Calendar.getInstance();
        init();
    }


    private void init() {
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                hourList.add("0" + i);
            } else {
                hourList.add(i + "");
            }
        }
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                minList.add("0" + i);
            } else {
                minList.add(i + "");
            }
        }

        dialog = new Dialog(mContext, R.style.translucentDialogStyle);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_time_set, null, false);

        tvEnsure =  view.findViewById(R.id.tv_ensure);
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvEnsure.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        wvHour = view.findViewById(R.id.wv_hour);
        wvMin = view.findViewById(R.id.wv_min);

        wvHour.setOffset(4);
        wvHour.setSelectColor(mContext.getResources().getColor(R.color.black, null));
        wvHour.setItems(hourList);
        wvHour.setSeletion(0);

        wvMin.setOffset(4);
        wvMin.setSelectColor(mContext.getResources().getColor(R.color.black, null));
        wvMin.setItems(minList);
        wvMin.setSeletion(0);

        int w = (int) (DensityUtils.getW(mContext) / 1.2);
        dialog.setContentView(view, new LinearLayout.LayoutParams(w, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.setCanceledOnTouchOutside(true);
//        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
//                if(i == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0){
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        dialog.dismiss();
        switch (view.getId()) {
            case R.id.tv_cancel:
                disMiss();
                break;
            case R.id.tv_ensure:
                disMiss();
                if (onClickCallBack != null) {
                    String hour = wvHour.getSeletedItem();
                    String min = wvMin.getSeletedItem();
                    onClickCallBack.onClick(hour + ":" + min);
                }
                break;
            default:
                break;
        }

    }


    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * 根据年月日，跳转到相应的日期
     *
     * @param selectedTime "02:00"
     */
    public void setSelectedTime(String selectedTime) {
        if (TextUtils.isEmpty(selectedTime) || "\"null\"".equals(selectedTime)) {
            return;
        }
        try {
            String[] str = selectedTime.split(":");
            int hour = Integer.parseInt(str[0]);
            wvHour.setSeletion(hour);
            int min = Integer.parseInt(str[1]);
            wvMin.setSeletion(min);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disMiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public interface onClickCallBack {
        void onClick(String time);
    }

    public void setOnClickCallBack(TimeSetDialogUtil.onClickCallBack onClickCallBack) {
        this.onClickCallBack = onClickCallBack;
    }

}
