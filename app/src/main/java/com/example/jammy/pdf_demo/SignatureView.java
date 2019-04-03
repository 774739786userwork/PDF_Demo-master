package com.example.jammy.pdf_demo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.example.jammy.pdf_demo.paintutil.BasePenExtend;
import com.example.jammy.pdf_demo.paintutil.BrushPen;
import com.example.jammy.pdf_demo.paintutil.IPenConfig;
import com.example.jammy.pdf_demo.paintutil.SteelPen;

import static android.R.attr.width;
import static com.example.jammy.pdf_demo.R.attr.height;
import static com.example.jammy.pdf_demo.paintutil.IPenConfig.PEN_WIDTH;


/**
 * 画笔设置
 * Created by bangware
 */
public class SignatureView extends View {
    private static final String TAG = "DrawPenView";
    private Paint mPaint = null;//画笔
    private Canvas canvas = null;//画布
    private Context mContext;
    public static int mCanvasCode = IPenConfig.STROKE_TYPE_PEN;
    private BasePenExtend mStokeBrushPen;
    private boolean mIsCanvasDraw;
    private int mPenconfig;

    private Bitmap mBitmap = null;
    public static Bitmap saveImage = null;

    /**监听签名完成、清除接口*/
    private boolean mIsEmpty;
    private OnSignedListener mOnSignedListener;
    public interface OnSignedListener {
        public void onSigned();

        public void onClear();
    }

    public SignatureView(Context context) {
        super(context);
        initParameter(context);
    }

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initParameter(context);
    }

    public SignatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParameter(context);
    }

    private void initParameter(Context context) {
        mContext = context;
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        mBitmap = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.ARGB_8888);
        mStokeBrushPen = new SteelPen(context);
        initPaint();
        initCanvas();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(IPenConfig.PEN_CORLOUR);
        mPaint.setStrokeWidth(PEN_WIDTH);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);//结束的笔画为圆心
        mPaint.setStrokeJoin(Paint.Join.ROUND);//连接处元
        mPaint.setAlpha(0xFF);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeMiter(1.0f);
        mStokeBrushPen.setPaint(mPaint);
        setPenconfig(IPenConfig.STROKE_TYPE_BRUSH);
    }

    private void initCanvas() {
        canvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        switch (mCanvasCode) {
            case IPenConfig.STROKE_TYPE_PEN:
            case IPenConfig.STROKE_TYPE_BRUSH:
                mStokeBrushPen.draw(canvas);
                break;
            case IPenConfig.STROKE_TYPE_ERASER:
                reset();
                break;
            default:
                Log.e(TAG, "onDraw" + Integer.toString(mCanvasCode));
                break;
        }
    }

    public void setCanvasCode(int canvasCode) {
        mCanvasCode = canvasCode;
        switch (mCanvasCode) {
            case IPenConfig.STROKE_TYPE_PEN:
                mStokeBrushPen = new SteelPen(mContext);
                break;
            case IPenConfig.STROKE_TYPE_BRUSH:
                mStokeBrushPen = new BrushPen(mContext);
                break;

        }
        //设置
        if (mStokeBrushPen.isNull()){
            mStokeBrushPen.setPaint(mPaint);
        }
        invalidate();
    }

    public boolean isEmpty() {
        return mIsEmpty;
    }

    public Bitmap getSignatureBitmap() {
        Bitmap originalBitmap = getTransparentSignatureBitmap();
        Bitmap whiteBgBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(whiteBgBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(originalBitmap, 0, 0, null);
        return whiteBgBitmap;
    }

    public Bitmap getTransparentSignatureBitmap() {
        ensureSignatureBitmap();
        return mBitmap;
    }

    public void ensureSignatureBitmap() {
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(mBitmap);

            //设置背景图图片  要指定图片大小  否则下面注释的方法会放大
            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.qianming),
                    new Rect(0, 0, BitmapFactory.decodeResource(getResources(), R.drawable.qianming).getWidth(),
                            BitmapFactory.decodeResource(getResources(), R.drawable.qianming).getHeight()),
                    new Rect(0, 0, width, height), mPaint);

        }
    }

    public void setOnSignedListener(OnSignedListener listener) {
        mOnSignedListener = listener;
    }

    private void setIsEmpty(boolean newValue) {
        mIsEmpty = newValue;
        if (mOnSignedListener != null) {
            if (mIsEmpty) {
                mOnSignedListener.onClear();
            } else {
                mOnSignedListener.onSigned();
            }
        }
    }

    /**
     * event.getAction() //获取触控动作比如ACTION_DOWN
     * event.getPointerCount(); //获取触控点的数量，比如2则可能是两个手指同时按压屏幕
     * event.getPointerId(nID); //对于每个触控的点的细节，我们可以通过一个循环执行getPointerId方法获取索引
     * event.getX(nID); //获取第nID个触控点的x位置,记录的第一个点为getX，getY
     * event.getY(nID); //获取第nID个点触控的y位置
     * event.getPressure(nID); //LCD可以感应出用户的手指压力，当然具体的级别由驱动和物理硬件决定的
     * event.getDownTime() //按下开始时间
     * event.getEventTime() // 事件结束时间
     * event.getEventTime()-event.getDownTime()); //总共按下时花费时间
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mIsCanvasDraw = true;
        MotionEvent event2 = MotionEvent.obtain(event);
        mStokeBrushPen.onTouchEvent(event2, canvas);
        switch (event2.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mGetTimeListner!=null)
                    mGetTimeListner.stopTime();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mGetTimeListner!=null)
                    mGetTimeListner.stopTime();
                break;
            case MotionEvent.ACTION_UP:
                long time = System.currentTimeMillis();
                if (mGetTimeListner!=null)
                    mGetTimeListner.getTime(time);
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }
    /**
     *
     * @return 判断是否有绘制内容在画布上
     */
    public boolean getHasDraw(){
        return mIsCanvasDraw;
    }
    /**
     * 清除画布，记得清除点的集合
     */
    public void reset() {
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(mPaint);
        mPaint.setXfermode(null);
        mIsCanvasDraw = false;
        mStokeBrushPen.clear();
        //这里处理的不太好 需要优化
        mCanvasCode = mPenconfig;
    }

    public TimeListener mGetTimeListner;

    public void setGetTimeListener(TimeListener l) {
        mGetTimeListner = l;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setPenconfig(int penconfig) {
        mPenconfig = penconfig;
    }

    public int getPenConfig() {
        return mPenconfig;
    }

    public interface TimeListener {
        void getTime(long l);

        void stopTime();
    }
    private int mBackColor = Color.TRANSPARENT;
    /**
     * 逐行扫描 清楚边界空白。功能是生成一张bitmap位于正中间，不是位于顶部，此关键的是我们画布需要
     * 成透明色才能生效
     * @param blank 边距留多少个像素
     * @return tks github E-signature
     */
    public Bitmap clearBlank(int blank) {
        if (mBitmap != null) {
            int HEIGHT = mBitmap.getHeight();//1794
            int WIDTH = mBitmap.getWidth();//1080
            int top = 0, left = 0, right = 0, bottom = 0;
            int[] pixs = new int[WIDTH];
            boolean isStop;
            for (int y = 0; y < HEIGHT; y++) {
                mBitmap.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
                isStop = false;
                for (int pix : pixs) {
                    if (pix != mBackColor) {

                        top = y;
                        isStop = true;
                        break;
                    }
                }
                if (isStop) {
                    break;
                }
            }
            for (int y = HEIGHT - 1; y >= 0; y--) {
                mBitmap.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
                isStop = false;
                for (int pix : pixs) {
                    if (pix != mBackColor) {
                        bottom = y;
                        isStop = true;
                        break;
                    }
                }
                if (isStop) {
                    break;
                }
            }
            pixs = new int[HEIGHT];
            for (int x = 0; x < WIDTH; x++) {
                mBitmap.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
                isStop = false;
                for (int pix : pixs) {
                    if (pix != mBackColor) {
                        left = x;
                        isStop = true;
                        break;
                    }
                }
                if (isStop) {
                    break;
                }
            }
            for (int x = WIDTH - 1; x > 0; x--) {
                mBitmap.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
                isStop = false;
                for (int pix : pixs) {
                    if (pix != mBackColor) {
                        right = x;
                        isStop = true;
                        break;
                    }
                }
                if (isStop) {
                    break;
                }
            }
            if (blank < 0) {
                blank = 0;
            }
            left = left - blank > 0 ? left - blank : 0;
            top = top - blank > 0 ? top - blank : 0;
            right = right + blank > WIDTH - 1 ? WIDTH - 1 : right + blank;
            bottom = bottom + blank > HEIGHT - 1 ? HEIGHT - 1 : bottom + blank;
            return Bitmap.createBitmap(mBitmap, left, top, right - left, bottom - top);
        } else {
            return null;
        }
    }

    /**
     * 清空画板
     */
    public void clear() {
        reset();
        setIsEmpty(true);
        invalidate();
    }
}
