package com.sam.mkobandroiddep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public  class SystemDialogReceiver extends BroadcastReceiver {
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String
            SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "1", Toast.LENGTH_SHORT).show();

        if(intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){

            Toast.makeText(context, "2", Toast.LENGTH_SHORT).show();
            String dialogType = intent.
                    getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if(dialogType != null && dialogType.
                    equals(SYSTEM_DIALOG_REASON_RECENT_APPS)){
                Toast.makeText(context, "2", Toast.LENGTH_SHORT).show();
                Intent closeDialog =
                        new Intent(Intent.
                                ACTION_CLOSE_SYSTEM_DIALOGS);
                context.sendBroadcast(closeDialog);

            }
        }
    }
}