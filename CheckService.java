package com.gps.detect;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class CheckService {

    static void checkNStartService (Context context){
        if (isServiceNotRunning(System.class, context)) {
            context.startService(new Intent(context, System.class));
        }
            context.startService(new Intent(context,SentSms.class));
    }

  public static boolean isServiceNotRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d("tag1CheckService16", serviceClass.getName() + " service running");
                return false;
            }
        }
        Log.d("tag1CheckService16", serviceClass.getName() + " service not running");
        return true;
    }
}
