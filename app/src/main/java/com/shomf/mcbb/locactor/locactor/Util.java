package com.shomf.mcbb.locactor.locactor;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by AHao on 2016/12/8.
 */

public class Util {
    private final static String SPF = "locactor";
    private final static double DLAT = 29.856933;
    private final static double DLON = 121.688004;
    private final static double DSPEED = 0.00001;
    private final static String KLAT = "LAT";
    private final static String KLON = "LON";
    private final static String KSPEED = "SPEED";

    static class Loc {
        double lat = DLAT;
        double lon = DLON;
        double speed = DSPEED;
    }

    public static Loc mLoc = new Loc();
    private static int mRefCount = 0;


    public static Loc getLocationFromSharePreference(Context mContext) {
        try {
            if (mContext != null) {
                SharedPreferences sp = mContext.getSharedPreferences(SPF, 0);
                mLoc.lat = Double.parseDouble(sp.getString(KLAT,dToS6(DLAT)));
                mLoc.lon = Double.parseDouble(sp.getString(KLON, dToS6(DLON)));
                mLoc.speed = Double.parseDouble(sp.getString(KSPEED, dToS6(DSPEED)));
            }
        }catch(Exception e){}
        return mLoc;
    }

    public static void setLocationToSharePreference(Context mContext, String lat, String lon, String speed) {
        try {
            if (mContext != null) {
                SharedPreferences sp = mContext.getSharedPreferences(SPF, 0);
                SharedPreferences.Editor ed = sp.edit();
                ed.putString(KLAT, lat);
                ed.putString(KLON, lon);
                ed.putString(KSPEED, speed);
                ed.commit();
            }
        }catch(Exception e){}
        getLocationFromSharePreference(mContext);
    }

    public static void move(Context mContext, double latoff, double lonoff) {
        mLoc.lat = mLoc.lat + latoff;
        mLoc.lon = mLoc.lon + lonoff;
        mRefCount++;
        if (mRefCount > 40) {
            mRefCount = 0;
            try {
                if (mContext != null) {
                    SharedPreferences sp = mContext.getSharedPreferences(SPF, 0);
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putString(KLAT, dToS6(mLoc.lat));
                    ed.putString(KLON, dToS6(mLoc.lon));
                    ed.commit();
                }
            }catch(Exception e){}
        }
    }

    public static void moveTo(Context mContext, double lat, double lon) {

    }


    public static String dToS6(Double d) {
        return String.format("%.6f", d);
    }


}
