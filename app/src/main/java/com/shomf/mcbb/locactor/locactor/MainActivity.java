package com.shomf.mcbb.locactor.locactor;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;

public class MainActivity extends Activity {

    EditText etStartLat, etStartLon, etSpeed;
    Button btnSet;
    Switch swtGPS, swtWheel;

    MapView mMapView = null;
    AMap mAMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etStartLat = (EditText)this.findViewById(R.id.start_lat);
        etStartLon = (EditText)this.findViewById(R.id.start_lon);
        etSpeed = (EditText)this.findViewById(R.id.speed);
        btnSet = (Button)this.findViewById(R.id.btn_set);
        swtGPS = (Switch)this.findViewById(R.id.mockgps);
        swtWheel = (Switch)this.findViewById(R.id.showwheel);

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.setLocationToSharePreference(MainActivity.this, etStartLat.getText().toString(),
                        etStartLon.getText().toString(), etSpeed.getText().toString());
                mAMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        new LatLng(Util.mLoc.lat,Util.mLoc.lon),//��..��..�..�.�..�ɵ��
                        15, //��..��...��...�.�
                        5, //..�....��0..~45....�..�.�...�.�..�.��....0..�
                        0  ////.��.�.�� 0~360.. (���.����....0)
                )));
            }
        });

        swtWheel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(MainActivity.this, MiniMapService.class);
                    MainActivity.this.startService(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, MiniMapService.class);
                    MainActivity.this.stopService(intent);
                }

//                if (isChecked) {
//                    Intent intent = new Intent(MainActivity.this, WheelService.class);
//                    MainActivity.this.startService(intent);
//                } else {
//                    Intent intent = new Intent(MainActivity.this, WheelService.class);
//                    MainActivity.this.stopService(intent);
//                }
            }
        });
        swtGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(MainActivity.this, MockLocationService.class);
                    intent.putExtra("CMD", "ON");
                    MainActivity.this.startService(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, MockLocationService.class);
                    intent.putExtra("CMD", "OFF");
                    MainActivity.this.startService(intent);
                }
            }
        });

        //.�..��.�..�.�ĺ.....�.��
        mMapView = (MapView) findViewById(R.id.map);
        //.��activity��.��onCreate��.��.��mMapView.onCreate(savedInstanceState)..�.�..�..�..�..�..�..濵�..��.��
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.setOnMapClickListener(new AMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    etStartLat.setText(Util.dToS6(latLng.latitude));
                    etStartLon.setText(Util.dToS6(latLng.longitude));
                }
            });

            mAMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                @Override
                public void onMapLoaded() {
                    mAMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                            new LatLng(Util.mLoc.lat,Util.mLoc.lon),//��..��..�..�.�..�ɵ��
                            15, //��..��...��...�.�
                            5, //..�....��0..~45....�..�.�...�.�..�.��....0..�
                            0  ////.��.�.�� 0~360.. (���.����....0)
                    )));
                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.Loc loc = Util.getLocationFromSharePreference(this);
        etStartLat.setText(Util.dToS6(loc.lat));
        etStartLon.setText(Util.dToS6(loc.lon));
        etSpeed.setText(Util.dToS6(loc.speed));
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        //.��activity��.��onResume��.��.��mMapView.onResume ()..�.�..�..�..�..�..�..濵�..��.��
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //.��activity��.��onDestroy��.��.��mMapView.onDestroy()..�.�..�..�..�..�..�..濵�..��.��
        mMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //.��activity��.��onPause��.��.��mMapView.onPause ()..�.�..�..�..�..�..�..濵�..��.��
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //.��activity��.��onSaveInstanceState��.��.��mMapView.onSaveInstanceState (outState)..�.�..�..�..�..�..�..濵�..��.��
        mMapView.onSaveInstanceState(outState);
    }
}
