package com.example.marco.fiubados.commons;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class AlertDialogBuilder {

    public static AlertDialog generateAlertWithCustomBehavior(Context context, Intent intent, String alertName, String alertContent) {
        return AlertDialogBuilder.generateAlert(context, intent, alertName, alertContent);
    }

    public static AlertDialog generateAlert(Context context, String alertName, String alertContent) {
        return AlertDialogBuilder.generateAlert(context, null, alertName, alertContent);
    }

    private static AlertDialog generateAlert(final Context context, final Intent intent, String alertName, String alertContent) {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(context);
        helpBuilder.setTitle(alertName);
        helpBuilder.setMessage(alertContent);
        helpBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (intent != null){
                    context.startActivity(intent);
                }
            }
        });

        return helpBuilder.create();
    }

}
