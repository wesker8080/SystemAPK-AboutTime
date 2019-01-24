package com.elite.utils;

import android.app.AlertDialog;
import android.content.Context;

import com.elite.R;

import java.util.Optional;

/**
 * @author Wesker
 * @create 2019-01-17 14:46
 */
public class CommonDialog {
    public static void showDialog(Context context, Integer title, int message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Optional.ofNullable(title).ifPresent(x -> builder.setTitle(title));
        builder.setMessage(message);
        builder.setPositiveButton(R.string.message_dialog_ok, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    public static AlertDialog.Builder createDialogBuilder(Context context, Integer title, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Optional.ofNullable(title).ifPresent(x -> builder.setTitle(title));
        builder.setMessage(message);
        return builder;
    }
    public static void createDialog(AlertDialog.Builder builder, boolean outsideCancel) {
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(outsideCancel);
        alertDialog.show();
    }
}
