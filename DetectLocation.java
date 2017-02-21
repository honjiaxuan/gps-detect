package com.gps.detect;



import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import static android.provider.Settings.Secure.LOCATION_MODE;
import static android.provider.Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
import static android.provider.Settings.Secure.LOCATION_MODE_OFF;



public class DetectLocation extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    /**
     Intent service to detect current location of the user
     */

        private android.location.Location mLastLocation;
        private String mLatitudeText;
        private String mLongitudeText;
        private float mOldAccuracy ;
        private String stringOfAccuracy;
        private GoogleApiClient mGoogleApiClient;
        private LocationRequest mLocationRequest;
        private final String date1 = "a";
        private String content;
        private boolean isTimerRunning;
        private boolean isSecondTimerRunning;
        private final Timer mTimer = new Timer();
        private final Timer mTimer1 = new Timer();
        private LocationManager locationManager;
        private ConnectivityManager connectivityManager;
       


        @Override
        public void onCreate() {
            super.onCreate();
            buildGoogleApiClient();

        }

    public static void startDetectLocationService(Context context) {
        Intent intent = new Intent(context, DetectLocation.class);
        context.startService(intent);
    }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            super.onStartCommand(intent, flags, startId);
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        // 5 min
                mLocationRequest.setInterval(5 *60 * 1000)
                        //2 min
                        .setFastestInterval(2*60 * 1000);
            

            connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            mGoogleApiClient.connect();
            return START_REDELIVER_INTENT;
        }

        private synchronized void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        @Override
        public void onConnected(Bundle bundle) {
            Log.d("tag1DetectLocation4", "onConnected");
            //check is the permission granted or not
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED  &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED) {


                //to get the last location available first
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    mLatitudeText = String.valueOf(mLastLocation.getLatitude());
                    mLongitudeText = String.valueOf(mLastLocation.getLongitude());
                    stringOfAccuracy = String.valueOf(mLastLocation.getAccuracy());
                    content = "last location: " + mLatitudeText + ", " + mLongitudeText + " " + stringOfAccuracy + "m";
                    Log.d("tag1DetectLocation1", content);
                }
                    startLocationUpdates();
            } else {
                Log.d("tag1detectLocation10", "permission not granted");
            }
        }



        @Override
        public void onConnectionSuspended(int i) {
            Log.d("tag1DetectLocation13", "connection suspended");
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d("tag1detectLocation12", "onConnectionFailed: " + connectionResult.getErrorCode() + "," + connectionResult.getErrorMessage());
        }

        @Override
        public void onLocationChanged(android.location.Location location) {
                mLastLocation = location;
                mLatitudeText = String.valueOf(mLastLocation.getLatitude());
                mLongitudeText = String.valueOf(mLastLocation.getLongitude());
                stringOfAccuracy = String.valueOf(mLastLocation.getAccuracy());
                content = mLatitudeText + ", " + mLongitudeText + " " + stringOfAccuracy + "m";
                Log.d("tag1DetectLocation2", content);          
        }

        private void startLocationUpdates() {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
               LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }

       

    //TimerTask that will cause the run() runnable to happen.
        private final TimerTask myTask = new TimerTask()
        {
            public void run()
            {
                stopSelf();
                isTimerRunning = false;
                Log.d("tag1DetectLocation24","timerStopService");
            }
        };

        private final TimerTask myTask1 = new TimerTask()
        {
            public void run()
            {
                stopSelf();
                isSecondTimerRunning = false;
                Log.d("tag1DetectLocation25","second timer StopService");
            }
        };

        private void stopTimer() {
            mTimer.cancel();
            mTimer.purge();
            isTimerRunning = false;
            mTimer1.cancel();
            mTimer1.purge();
            isSecondTimerRunning = false;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onDestroy(){
            isItReadyToSend = false;
            stopTimer();
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
            isGpsOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            
            if (mReceiver != null) {
                unregisterReceiver(mReceiver);
                mReceiver = null;
            }
            Log.d("tag1DetectLocation7","service stopped ");
            super.onDestroy();
        }
}



