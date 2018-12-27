package com.sam.mkobandroiddep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.sam.mkobandroiddep.activity.SamInitialActivity;

import static android.content.Context.MODE_PRIVATE;

public class BootReciever extends  BroadcastReceiver {

    private static final String TAG = "MyBroadcastReceiver";
    SharedPreferences prefs;
    boolean aBooleanSAMActive=false;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("boot_boot","Received BOOT COMPLETED");
        Log.w("boot_broadcast_poc", "starting ser...");

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            prefs               = context.getApplicationContext().getSharedPreferences("SAMPref", MODE_PRIVATE);
            aBooleanSAMActive   = prefs.getBoolean("SAMactive", false);

            Log.d(TAG, "onCreate: "+aBooleanSAMActive);
            if (aBooleanSAMActive){
                Intent activityIntent = new Intent(context, SamInitialActivity.class);
                activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activityIntent);

            }else {
                Toast.makeText(context, "SAM Deactivate", Toast.LENGTH_SHORT).show();
            }

        }
    }
}