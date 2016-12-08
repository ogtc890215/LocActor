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
    //.«Ü..ëµ.«.è¿.¬ù.Åú..â..Ç
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    //.ê¢...µ.«.è¿.¬ù.Åú.«...«..â..Ç.Åéµò..Üä.»...í
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
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.wheel, null);
        //µ...èámFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        //µ.«.è¿.¬ù.Åúµîë.Æ«
        mFloatView = (Button) mFloatLayout.findViewById(R.id.btnss);
        mBtnE = (Button) mFloatLayout.findViewById(R.id.btne);
        mBtnS = (Button) mFloatLayout.findViewById(R.id.btns);
        mBtnW = (Button) mFloatLayout.findViewById(R.id.btnw);
        mBtnN = (Button) mFloatLayout.findViewById(R.id.btnn);
        //mSpeed = (Switch)mFloatLayout.findViewById(R.id.speed) ;

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);
        //.«...«.¢æ.É¼µ.«.è¿.¬ù.Åú.Üä.ºªµæ..º..è¿
        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //getRawXµÿ».ºªµæ...ì..«.¢..»...Ä..Å..ò.Üä.¥Éµáç..îgetXµÿ».¢..»...Äµîë.Æ«.Üä.¥Éµáç
                wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth()*2 ;
                Log.i(TAG, "RawX" + event.getRawX());
                Log.i(TAG, "X" + event.getX());
                //.çÅ25....è.µÇüµáÅ.Üä.½ÿ..ª
                wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight()*2;
                Log.i(TAG, "RawY" + event.getRawY());
                Log.i(TAG, "Y" + event.getY());
                //.ê.µû.
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                return false;  //µ¡ñ.ñä..à.í...ö.¢.false..î.Éª.êÖOnClickListener.Ä..Åû..ì.ê..¢æ.É¼
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
