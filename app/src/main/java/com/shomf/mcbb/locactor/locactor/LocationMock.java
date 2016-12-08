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
                // µ¿íµï...ì..«.Å».ö¿
                return true;
            } catch (SecurityException e) {
                String info = e.toString();
                if(info.contains("permission")){
                    Toast.makeText(mContext,"µúÇµ.Ñµ¥â.ÖÉ.òè...",Toast.LENGTH_LONG).show();
                } else if(info.contains("not allowed")){
                    Toast.makeText(mContext,"..Ç.Åæ.Çà.Çë.í..çî.Üäµ¥â.ÖÉ.òè...",Toast.LENGTH_LONG).show();
                }
                Log.e(TAG,e.toString());
                e.printStackTrace();
            }
        }
        return false;
    }

    public void updateLocation(double lat, double lon) {
        try {
            // µ¿íµï...ì..«..êaddTestProviderµêÉ.è..Üä.ëìµÅÉ..ï..ë
            Location mockLocation = new Location(mProvider);
            mockLocation.setLatitude(lat + random.nextInt(5)/1000000.0);  // .....ª..ê..ª..ë
            mockLocation.setLongitude(lon+ random.nextInt(5)/1000000.0); // ..Å..ª..ê..ª..ë
            mockLocation.setAltitude(30);  // .½ÿ.¿ï..ê.....ë
            //mockLocation.setBearing(180);  // µû..Éæ..ê..ª..ë
            mockLocation.setSpeed(3+random.nextFloat());  //.Ç...ª..ê.../.ºÆ..ë
            mockLocation.setAccuracy(1.0f);  // .....ª..ê.....ë
            mockLocation.setTime(new Date().getTime());  // µ£¼.£.µù..ù.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            }
            mLocationManager.setTestProviderLocation(mProvider, mockLocation);
            Log.d(TAG, "updateLocation:" +mockLocation );
        } catch (Exception e) {
            e.printStackTrace();
            // .ÿ.µ¡ó.ö¿µê..£¿..».....É.íî..ç.¿ï..¡.à..ù¡µ¿íµï...ì..«µêû.Çëµï..à...û..ö.ö¿
            Toast.makeText(mContext,"µ¢.µû...ì..«.ñ...Ñ" + e,Toast.LENGTH_SHORT).show();
        }
    }
}
