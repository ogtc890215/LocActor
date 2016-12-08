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
import android.widget.LinearLayout;
import android.widget.Toast;

public class WheelService extends Service {

    private static final String TAG = "MockLocation_Wheel";
    private static final double NOTMOVE = 0.000000f;

    private static final java.text.DecimalFormat DF4 = new java.text.DecimalFormat("#.0000");
    private static final Double DEFAULT_WAY_LEN = 0.01;
    private static final Double DEFAULT_WAY_WIDTH = 0.000004;
    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    Button mFloatView;
    Button mBtnE, mBtnS, mBtnW, mBtnN;

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
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.wheel, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮
        mFloatView = (Button) mFloatLayout.findViewById(R.id.btnss);
        mBtnE = (Button) mFloatLayout.findViewById(R.id.btne);
        mBtnS = (Button) mFloatLayout.findViewById(R.id.btns);
        mBtnW = (Button) mFloatLayout.findViewById(R.id.btnw);
        mBtnN = (Button) mFloatLayout.findViewById(R.id.btnn);
        //mSpeed = (Switch)mFloatLayout.findViewById(R.id.speed) ;

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);
        //设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth() * 2;
                Log.i(TAG, "RawX" + event.getRawX());
                Log.i(TAG, "X" + event.getX());
                //减25为状态栏的高度
                wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight() * 2;
                Log.i(TAG, "RawY" + event.getRawY());
                Log.i(TAG, "Y" + event.getY());
                //刷新
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                return false;  //此处必须返回false，否则OnClickListener获取不到监听
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
        Integer i = 500;
        try {
            i = Integer.parseInt(s);
        } catch (Exception e) {
        }
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
            while (isRuning) {
                switch (direction) {
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
