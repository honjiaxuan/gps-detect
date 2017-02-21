package com.gps.detect;



import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;





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
    private String stringOfAccuracy;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String content;


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
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                //4min
                .setInterval(4 *60 * 1000)
                //2 min
                .setFastestInterval(2*60 * 1000);
        mGoogleApiClient.connect();
        return START_REDELIVER_INTENT;
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("tag1detectLocation4", "onConnected");
        //check is the permission granted or not
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== 
            PackageManager.PERMISSION_GRANTED  &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)== 
            PackageManager.PERMISSION_GRANTED) {
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
        Log.d("tag1detectLocation12", "onConnectionFailed: " + connectionResult.getErrorCode() + "," 
              + connectionResult.getErrorMessage());
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



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
        Log.d("tag1DetectLocation7","service stopped ");
        super.onDestroy();
    }
}
