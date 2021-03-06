package com.shomf.mcbb.locactor.locactor;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

public class MockLocationService extends Service {

    private static final String TAG = "MockLocation_Service";

    private LocationManager mLocationManager;
    private LocationMock mGpsMock;
    private LocationMock mNetworkMock;
    private LocationMock mFusedMock;
    private boolean isRunning = true;
    private boolean needUpdate = false;
    private double mLastLat, mLastLon;
    private long mLastUpdateGpsTime;

    public MockLocationService() {
        Log.d(TAG, "-------MockLocationService-------------");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "-------onCreate-------------");
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mGpsMock = new LocationMock(this, LocationManager.GPS_PROVIDER, mLocationManager);
        mNetworkMock = new LocationMock(this, LocationManager.NETWORK_PROVIDER, mLocationManager);
        mFusedMock = new LocationMock(this, "fused", mLocationManager);
        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
                while (isRunning) {
                    if (needUpdate) {
                        double tmpLat, tmpLon;
                        tmpLat = Util.mLoc.lat;
                        tmpLon = Util.mLoc.lon;
                        long now  = System.currentTimeMillis();
                        boolean force = (now - mLastUpdateGpsTime) > 3000;
                        if (force || !(double_equal(tmpLat, mLastLat) && double_equal(tmpLon, mLastLon))) {
                            mLastUpdateGpsTime = now;
                            try {
                                mGpsMock.updateLocation(tmpLat, tmpLon);
                            } catch (Exception e) {
                            }
                            try {
                                mNetworkMock.updateLocation(tmpLat, tmpLon);
                            } catch (Exception e) {
                            }
                            try {
                                mFusedMock.updateLocation(tmpLat, tmpLon);
                            } catch (Exception e) {
                            }
                            mLastLat = tmpLat;
                            mLastLon = tmpLon;
                        }
                    }
                    SystemClock.sleep(500);
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "-------onStartCommand-------------" + intent + flags + "startid:" + startId);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.mockloc)
                .setContentTitle("LocActor")
                .setWhen(System.currentTimeMillis())
                .setContentText("Mock Location Service Running.")
                .setContentIntent(PendingIntent.getService(this, 0, intent, 0));
        startForeground(startId, builder.build());
        parseCommand(intent);
        return START_STICKY;
    }

    private void parseCommand(Intent intent) {
        if (intent != null) {
            Bundle b = intent.getExtras();
            String cmd = b.getString("CMD");
            if (cmd != null && cmd.equals("ON")) {
                needUpdate = true;
                Log.d(TAG, "-------onStartCommand-------------CMD:ON");
            } else if (cmd != null && cmd.equals("OFF")) {
                needUpdate = false;
                Log.d(TAG, "-------onStartCommand-------------CMD:OFF");
            } else {
                //服务被重启
                needUpdate = true;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        Log.d(TAG, "-------onDestroy-------------");
        stopForeground(true);
        try {
            mLocationManager.removeTestProvider(mGpsMock.getProvider());
        } catch (Exception ex) {
        }
        try {
            mLocationManager.removeTestProvider(mNetworkMock.getProvider());
        } catch (Exception ex) {
        }
        try {
            mLocationManager.removeTestProvider(mFusedMock.getProvider());
        } catch (Exception ex) {
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private boolean double_equal(double a, double b) {
        double c = a - b;
        if (c > -0.000001 && c < 0.000001) {
            return true;
        } else {
            return false;
        }
    }
}
