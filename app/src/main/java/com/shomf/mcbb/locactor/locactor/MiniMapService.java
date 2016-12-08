package com.shomf.mcbb.locactor.locactor;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;

/**
 * Created by AHao on 2016/12/8.
 */

public class MiniMapService extends Service {
    private static final String TAG = "MockLocation_MiniMap";

    //.«Ü..ëµ.«.è¿.¬ù.Åú..â..Ç
    FrameLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    //.ê¢...µ.«.è¿.¬ù.Åú.«...«..â..Ç.Åéµò..Üä.»...í
    WindowManager mWindowManager;
    MapView mMapView;

    AMap mAMap;

    public MiniMapService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
        if (mMapView != null) {
            mMapView.onResume();
        }
        Log.d(TAG, "-------onCreate-------------");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
        if (mMapView != null) {
            mMapView.onPause();
            mMapView.onDestroy();
            mMapView = null;
        }
        Log.d(TAG, "-------onDestroy-------------");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        //.Ä..Åû.Üäµÿ»WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //.«...«window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //.«...«.¢..ëçµá...Å..îµòêµ.£....âîµÖ».ÇÅµÿÄ
        wmParams.format = PixelFormat.RGBA_8888;
        //.«...«µ.«.è¿.¬ù.Åú..ì.Å».üÜ.äª..ê.«..Ä.µôì..£.Öñµ.«.è¿.¬ù.Åú.ñû.Üä.à...û.Å».ºü.¬ù.Åú.Üäµôì..£..ë
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //..âµò.µé¼µ.«.¬ùµÿ..ñ..Üä.ü£.¥á..ì..«.....ª..º..«.í.
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // ..Ñ..Å..ò..ª..è.ºÆ....Ä..é...î.«...«x.Çüy.ê¥.ºï.Ç...î.¢..»...Ägravity
        wmParams.x = 0;
        wmParams.y = 0;

        //.«...«µé¼µ.«.¬ù.Åú.ò..«.µò.µì«
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;


        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //.Ä..Åûµ.«.è¿.¬ù.Åú.ºå.¢.µëÇ.£¿..â..Ç
        mFloatLayout = (FrameLayout) inflater.inflate(R.layout.minimap, null);
        //µ...èámFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        //µ.«.è¿.£..¢.
        mMapView = (MapView) mFloatLayout.findViewById(R.id.map);
        mMapView.onCreate(null);
        mAMap = mMapView.getMap();
        mAMap.getUiSettings().setZoomControlsEnabled(false);
        mAMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                mAMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        new LatLng(Util.mLoc.lat,Util.mLoc.lon),//µû..Üä..¡..â.é..¥Éµáç
                        18, //µû..Üä...µö...º.ê½
                        5, //..»....ºÆ0..~45....ê..é.¢...Ä.£..¢.µù....0..ë
                        0  ////.üÅ.ê¬.ºÆ 0~360.. (µ¡ú.îùµû....0)
                )));
            }
        });

        mAMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                double latoff = latLng.latitude - Util.mLoc.lat;
                double lonoff = latLng.longitude - Util.mLoc.lon;
                Util.move(MiniMapService.this, latoff, lonoff);
                mAMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        new LatLng(Util.mLoc.lat,Util.mLoc.lon),//µû..Üä..¡..â.é..¥Éµáç
                        18, //µû..Üä...µö...º.ê½
                        5, //..»....ºÆ0..~45....ê..é.¢...Ä.£..¢.µù....0..ë
                        0  ////.üÅ.ê¬.ºÆ 0~360.. (µ¡ú.îùµû....0)
                )));
            }
        });

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //.«...«.¢æ.É¼µ.«.è¿.¬ù.Åú.Üä.ºªµæ..º..è¿
        mMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //getRawXµÿ».ºªµæ...ì..«.¢..»...Ä..Å..ò.Üä.¥Éµáç..îgetXµÿ».¢..»...Äµîë.Æ«.Üä.¥Éµáç
                wmParams.x = (int) event.getRawX() - mMapView.getMeasuredWidth()*2 ;
                Log.i(TAG, "RawX" + event.getRawX());
                Log.i(TAG, "X" + event.getX());
                //.çÅ25....è.µÇüµáÅ.Üä.½ÿ..ª
                wmParams.y = (int) event.getRawY() - mMapView.getMeasuredHeight()*2;
                Log.i(TAG, "RawY" + event.getRawY());
                Log.i(TAG, "Y" + event.getY());
                //.ê.µû.
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                return false;  //µ¡ñ.ñä..à.í...ö.¢.false..î.Éª.êÖOnClickListener.Ä..Åû..ì.ê..¢æ.É¼
            }
        });

    }

}