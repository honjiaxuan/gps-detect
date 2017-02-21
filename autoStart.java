package com.gps.detect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;


public class autoStart extends BroadcastReceiver {
    public autoStart() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        
        
// check is it the correct intent action,gps started
        if (intent.getAction() != null) {
            String intentAction = intent.getAction();
            Log.d("tag1autostart1", intent.getAction() + ".");
            if (intentAction.equals("android.location.PROVIDERS_CHANGED")) {
                GpsChanged.startGpsChangedService(context);
            } 
        }
    }
}

