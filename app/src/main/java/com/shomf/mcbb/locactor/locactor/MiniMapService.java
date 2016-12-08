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

    //.��..�.�.�.��.��..�..�
    FrameLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    //.�...�.�.�.��.��.�...�..�..�.���..��.�...�
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
        //.�..��.����WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //.�...�window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //.�...�.�..���...�..���.�....��ֻ.�ŵ��
        wmParams.format = PixelFormat.RGBA_8888;
        //.�...��.�.�.��.��..�.Ż.��.�..�.�..�.���..�.��.�.�.��.��.��.��.�...�.Ż.��.��.��.����..�..�
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //..��.�鼵.�.����..�..��.��.��..�..�.....�..�..�.�.
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // ..�..�..�..�..�.��....�..�...�.�...�x.��y.�.��.�...�.�..�...�gravity
        wmParams.x = 0;
        wmParams.y = 0;

        //.�...��鼵.�.��.��.�..�.��.��
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;


        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //.�..���.�.�.��.��.��.�.���.��..�..�
        mFloatLayout = (FrameLayout) inflater.inflate(R.layout.minimap, null);
        //�...��mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        //�.�.�.�..�.
        mMapView = (MapView) mFloatLayout.findViewById(R.id.map);
        mMapView.onCreate(null);
        mAMap = mMapView.getMap();
        mAMap.getUiSettings().setZoomControlsEnabled(false);
        mAMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                mAMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        new LatLng(Util.mLoc.lat,Util.mLoc.lon),//��..��..�..�.�..�ɵ��
                        18, //��..��...��...�.�
                        5, //..�....��0..~45....�..�.�...�.�..�.��....0..�
                        0  ////.��.�.�� 0~360.. (���.����....0)
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
                        new LatLng(Util.mLoc.lat,Util.mLoc.lon),//��..��..�..�.�..�ɵ��
                        18, //��..��...��...�.�
                        5, //..�....��0..~45....�..�.�...�.�..�.��....0..�
                        0  ////.��.�.�� 0~360.. (���.����....0)
                )));
            }
        });

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //.�...�.��.ɼ�.�.�.��.��.��.����..�..�
        mMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //getRawX���.����...�..�.�..�...�..�..�.��.�ɵ��..�getX���.�..�...ĵ��.ƫ.��.�ɵ��
                wmParams.x = (int) event.getRawX() - mMapView.getMeasuredWidth()*2 ;
                Log.i(TAG, "RawX" + event.getRawX());
                Log.i(TAG, "X" + event.getX());
                //.��25....�.������.��.��..�
                wmParams.y = (int) event.getRawY() - mMapView.getMeasuredHeight()*2;
                Log.i(TAG, "RawY" + event.getRawY());
                Log.i(TAG, "Y" + event.getY());
                //.�.��.
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                return false;  //���.��..�.�...�.�.false..�.ɪ.��OnClickListener.�..��..�.�..��.ɼ
            }
        });

    }

}