package com.trainor.controlandmeasurement.HelperClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogManager {

    public static Dialog showDialog(Context context, String positiveText, String cancelText, String title, String message, boolean showNegativeButton, final IClickListener iClickListener) {
        Dialog alert = null;
        if (!((Activity) context).isFinishing()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(title);
            dialog.setCancelable(false);
            dialog.setMessage(message);

            dialog.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (iClickListener != null) {
                        iClickListener.onClick();
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                    }
                }
            });
            if (showNegativeButton) {
                dialog.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
            alert = dialog.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();
        }
        return alert;
    }
}
