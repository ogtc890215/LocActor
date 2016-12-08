package com.shomf.mcbb.locactor.locactor;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

public class WheelService extends Service {
    private static final String TAG = "MockLocation_Wheel";
    private static final double NOTMOVE = 0.000000f;

    private static final java.text.DecimalFormat DF4 = new java.text.DecimalFormat("#.0000");
    private static final Double DEFAULT_WAY_LEN = 0.01;
    private static final Double DEFAULT_WAY_WIDTH = 0.000004;
    //.��..�.�.�.��.��..�..�
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    //.�...�.�.�.��.��.�...�..�..�.���..��.�...�
    WindowManager mWindowManager;
    Button mFloatView;
    Button mBtnE, mBtnS,mBtnW,mBtnN;

    private MyRun mRun;

    public WheelService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
        Log.d(TAG, "-------onCreate-------------");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
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
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.wheel, null);
        //�...��mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        //�.�.�.��.�����.ƫ
        mFloatView = (Button) mFloatLayout.findViewById(R.id.btnss);
        mBtnE = (Button) mFloatLayout.findViewById(R.id.btne);
        mBtnS = (Button) mFloatLayout.findViewById(R.id.btns);
        mBtnW = (Button) mFloatLayout.findViewById(R.id.btnw);
        mBtnN = (Button) mFloatLayout.findViewById(R.id.btnn);
        //mSpeed = (Switch)mFloatLayout.findViewById(R.id.speed) ;

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);
        //.�...�.��.ɼ�.�.�.��.��.��.����..�..�
        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //getRawX���.����...�..�.�..�...�..�..�.��.�ɵ��..�getX���.�..�...ĵ��.ƫ.��.�ɵ��
                wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth()*2 ;
                Log.i(TAG, "RawX" + event.getRawX());
                Log.i(TAG, "X" + event.getX());
                //.��25....�.������.��.��..�
                wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight()*2;
                Log.i(TAG, "RawY" + event.getRawY());
                Log.i(TAG, "Y" + event.getY());
                //.�.��.
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                return false;  //���.��..�.�...�.�.false..�.ɪ.��OnClickListener.�..��..�.�..��.ɼ
            }
        });

        mFloatView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                STOPWALK();
                Toast.makeText(WheelService.this, "StopWalk", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                STOPWALK();
                mRun = new MyRun(DE);
                new Thread(mRun).start();
                Toast.makeText(WheelService.this, "Go E", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnS.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                STOPWALK();
                mRun = new MyRun(DS);
                new Thread(mRun).start();
                Toast.makeText(WheelService.this, "Go S", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnW.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                STOPWALK();
                mRun = new MyRun(DW);
                new Thread(mRun).start();
                Toast.makeText(WheelService.this, "Go W", Toast.LENGTH_SHORT).show();
            }
        });

        mBtnN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                STOPWALK();
                mRun = new MyRun(DN);
                new Thread(mRun).start();
                Toast.makeText(WheelService.this, "Go N", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void STOPWALK() {
        if (mRun != null) {
            mRun.isRuning = false;
        }
    }

    private static String dToS6(Double d) {
        return String.format("%.6f", d);
    }

    private static Integer sToI(String s) {
        Integer i  = 500;
        try {
            i = Integer.parseInt(s);
        }catch (Exception e){}
        return i;
    }

    private final static int DE = 0;
    private final static int DS = 1;
    private final static int DW = 2;
    private final static int DN = 3;

    private class MyRun implements Runnable {

        boolean isRuning = true;
        int direction = 0;

        public MyRun(int d) {
            direction = d;
        }
        @Override
        public void run() {
            double tmpStep = 0.0;
            while(isRuning) {
                switch(direction) {
                    case DE:
                        tmpStep = Util.mLoc.speed;
                        Util.move(WheelService.this, NOTMOVE, tmpStep);
                        break;
                    case DS:
                        tmpStep = 0 - Util.mLoc.speed;
                        Util.move(WheelService.this, tmpStep, NOTMOVE);
                        break;
                    case DW:
                        tmpStep = 0 - Util.mLoc.speed;
                        Util.move(WheelService.this, NOTMOVE, tmpStep);
                        break;
                    case DN:
                        tmpStep = Util.mLoc.speed;
                        Util.move(WheelService.this, tmpStep, NOTMOVE);
                        break;
                }
                SystemClock.sleep(500);
            }
        }
    }

}
