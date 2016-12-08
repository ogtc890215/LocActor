package com.shomf.mcbb.locactor.locactor;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.Random;

public class LocationMock {
    private static final String TAG = "MockLocation";
    private Random random;
    private Context mContext;
    private String mProvider;
    private LocationManager mLocationManager;

    public LocationMock(Context context, String provider, LocationManager locationManager){
        mContext = context;
        mProvider = provider;
        mLocationManager = locationManager;
        addProvider();
        random = new Random();
    }

    public String getProvider() {
        return mProvider;
    }

    public boolean addProvider(){
        boolean canMockPosition = (Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0)
                || Build.VERSION.SDK_INT > 22;
        if (canMockPosition) {
            Log.i(TAG, "start add mock provider");
            try {
                LocationProvider locationProvider = mLocationManager.getProvider(mProvider);
                if (locationProvider != null) {
                    mLocationManager.addTestProvider(
                              locationProvider.getName()
                            , locationProvider.requiresNetwork()
                            , locationProvider.requiresSatellite()
                            , locationProvider.requiresCell()
                            , locationProvider.hasMonetaryCost()
                            , locationProvider.supportsAltitude()
                            , locationProvider.supportsSpeed()
                            , locationProvider.supportsBearing()
                            , locationProvider.getPowerRequirement()
                            , locationProvider.getAccuracy());
                } else {
                    mLocationManager.addTestProvider(
                             mProvider
                            , true, true, false, false, true, true, true
                            , Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
                }
                mLocationManager.setTestProviderEnabled(mProvider, true);
                mLocationManager.setTestProviderStatus(mProvider, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
                // 模拟位置可用
                return true;
            } catch (SecurityException e) {
                String info = e.toString();
                if(info.contains("permission")){
                    Toast.makeText(mContext,"检查权限啊亲",Toast.LENGTH_LONG).show();
                } else if(info.contains("not allowed")){
                    Toast.makeText(mContext,"开发者选项里的权限啊亲",Toast.LENGTH_LONG).show();
                }
                Log.e(TAG,e.toString());
                e.printStackTrace();
            }
        }
        return false;
    }

    public void updateLocation(double lat, double lon) {
        try {
            // 模拟位置（addTestProvider成功的前提下）
            Location mockLocation = new Location(mProvider);
            mockLocation.setLatitude(lat + random.nextInt(5)/1000000.0);  // 维度（度）
            mockLocation.setLongitude(lon+ random.nextInt(5)/1000000.0); // 经度（度）
            mockLocation.setAltitude(30);  // 高程（米）
            //mockLocation.setBearing(180);  // 方向（度）
            mockLocation.setSpeed(3+random.nextFloat());  //速度（米/秒）
            mockLocation.setAccuracy(1.0f);  // 精度（米）
            mockLocation.setTime(new Date().getTime());  // 本地时间
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            }
            mLocationManager.setTestProviderLocation(mProvider, mockLocation);
            Log.d(TAG, "updateLocation:" +mockLocation );
        } catch (Exception e) {
            e.printStackTrace();
            // 防止用户在软件运行过程中关闭模拟位置或选择其他应用
            Toast.makeText(mContext,"更新位置失败" + e,Toast.LENGTH_SHORT).show();
        }
    }
}
