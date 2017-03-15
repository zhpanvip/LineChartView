package com.example.zhpan.linechartview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhpan on 2017/3/14.
 */

public class LineChartView extends View {
    private float xori; //  x轴原点坐标
    private float yori;  //  y轴原点坐标
    private int mMargin10;  //  10dp的间距
    private int mWidth; //  控件宽度
    private int mHeight;  //  控件高度
    private int textWidth;  //  y轴文字的宽度
    private int max = 100, min = 0;  //  最大值、最小值
    private float yInterval;  //  y轴坐标间隔
    private float xInterval;  //  x轴坐标间隔

    private String startTime = "2017-03-15";
    private String endTime = "2017-03-24";
    private int timeWidth;  //  日期宽度
    //  折线数据
    private List<ItemBean> mItems1;
    private List<ItemBean> mItems2;

    private int mAxesColor; //  坐标轴颜色
    private float mAxesWidth; //  坐标轴宽度
    private int mTextColorX;  //  X轴字体颜色
    private int mTextColorY;  //  Y轴字体颜色
    private float mTextSizeX; //  X轴字体大小
    private float mTextSizeY; //  Y轴字体大小
    private int mLineOneColor;  //  第一条折线颜色
    private int mLineTwoColor;  //  第二条折线颜色
    private int mBgColor;       //  背景色

    private Paint mPaintText;     //  画文字的画笔
    private Paint mPaintX;  //  X轴画笔
    private Paint mPaintY;  //  Y轴画笔
    private Paint mPaintAxes;   //  坐标轴颜色
    private Paint mPaintLine;
    private Path mPath;
    private Paint mPaintShader;
    private Path mPathShader;
    private float mProgress;


    public LineChartView(Context context) {
        super(context);
        init();
    }


    public LineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineChart);
        mAxesColor = typedArray.getColor(R.styleable.LineChart_axesColor, Color.parseColor("#CCCCCC"));
        mAxesWidth = typedArray.getDimension(R.styleable.LineChart_axesWidth, 1);
        mTextColorX = typedArray.getColor(R.styleable.LineChart_textColorX, Color.parseColor("#ABABAB"));
        mTextColorY = typedArray.getColor(R.styleable.LineChart_textColorY, Color.parseColor("#ABABAB"));
        mTextSizeX = typedArray.getDimension(R.styleable.LineChart_textSizeX, 14);
        mTextSizeY = typedArray.getDimension(R.styleable.LineChart_textSizeY, 32);
        mLineOneColor = typedArray.getColor(R.styleable.LineChart_lineOneColor, Color.RED);
        mLineTwoColor = typedArray.getColor(R.styleable.LineChart_lineTwoColor, Color.BLUE);
        mBgColor = typedArray.getColor(R.styleable.LineChart_bgColor, Color.WHITE);
        typedArray.recycle();
        //  初始化折线数据
        mItems1 = new ArrayList<>();
        mItems2 = new ArrayList<>();

        mItems1.add(new ItemBean(1489507200, 23));
        mItems1.add(new ItemBean(1489593600, 88));
        mItems1.add(new ItemBean(1489680000, 60));
        mItems1.add(new ItemBean(1489766400, 50));
        mItems1.add(new ItemBean(1489852800, 70));
        mItems1.add(new ItemBean(1489939200, 10));
        mItems1.add(new ItemBean(1490025600, 33));
        mItems1.add(new ItemBean(1490112000, 44));
        mItems1.add(new ItemBean(1490198400, 99));
        mItems1.add(new ItemBean(1490284800, 17));

        mMargin10 = ScreenUtils.dp2px(context, 10);

        init();
    }

    private void init() {
        //  初始化坐标轴画笔
        mPaintAxes = new Paint();
        mPaintAxes.setColor(mAxesColor);
        mPaintAxes.setStrokeWidth(mAxesWidth);

        //  初始化文字画笔
        mPaintText = new Paint();
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setAntiAlias(true); //抗锯齿
        mPaintText.setTextSize(mTextSizeX);
        mPaintText.setColor(mTextColorX);
        mPaintText.setTextAlign(Paint.Align.LEFT);

        //  初始化折线的画笔
        mPaintLine = new Paint();
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeWidth(mAxesWidth / 2);
        mPaintLine.setColor(mLineOneColor);

        //  初始化折线路径
        mPath = new Path();
        mPathShader = new Path();

        //  阴影画笔
        mPaintShader = new Paint();
        mPaintShader.setAntiAlias(true);
        mPaintShader.setStrokeWidth(2f);


        mPaintX = new Paint();
        mPaintX.setColor(mTextColorX);
        mPaintX.setTextSize(mTextSizeX);
        mPaintY = new Paint();
        mPaintY.setColor(mTextColorY);
        mPaintY.setTextSize(mTextSizeY);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {

            mWidth = getWidth();
            mHeight = getHeight();
            textWidth = (int) mPaintY.measureText("-00.00%");
            timeWidth = (int) mPaintX.measureText(startTime);
            //  初始化原点坐标
            xori = 0 + mMargin10;
            yori = (mHeight - mTextSizeY - mMargin10);

            //  设置背景色
            setBackgroundColor(mBgColor);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //  Y轴间距
        yInterval = (max - min) / (yori - mMargin10);
        xInterval = (mWidth - xori) / (mItems1.size() - 1);
        //  画坐标轴
        drawAxes(canvas);
        //  画文字
        drawText(canvas);
        //  画折线
        drawLine(canvas);
    }

    private void drawLine(Canvas canvas) {
        //  画坐标点
        for (int i = 0; i < mItems1.size(); i++) {
            float x = i * xInterval + xori + mAxesWidth;
            if (i == 0) {
                mPathShader.moveTo(x, yori - (mItems1.get(i).getValue() - min) / yInterval);
                mPath.moveTo(x, yori - (mItems1.get(i).getValue() - min) / yInterval);
            } else {
                mPath.lineTo(x - mMargin10 - mAxesWidth, yori - (mItems1.get(i).getValue() - min) / yInterval);
                mPathShader.lineTo(x - mMargin10 - mAxesWidth, yori - (mItems1.get(i).getValue() - min) / yInterval);
                if (i == mItems1.size() - 1) {
                    mPathShader.lineTo(x - mMargin10 - mAxesWidth, yori);
                    mPathShader.lineTo(xori, yori);
                    mPathShader.close();
                }
            }
        }
        //  设置动画
        PathMeasure measure = new PathMeasure(mPath, false);
        float pathLength = measure.getLength();
        PathEffect effect = new DashPathEffect(new float[]{pathLength,
                pathLength}, pathLength - pathLength * mProgress);
        mPaintLine.setPathEffect(effect);
        canvas.drawPath(mPath, mPaintLine);

        //  渐变阴影
        Shader mShader = new LinearGradient(0, 0, 0, getHeight(), new int[]{
                Color.argb(100, 255, 86, 86), Color.argb(15, 255, 86, 86),
                Color.argb(0, 255, 86, 86)}, null, Shader.TileMode.CLAMP);
        mPaintShader.setShader(mShader);

        //  画渐变阴影
        canvas.drawPath(mPathShader, mPaintShader);
    }

    private void drawText(Canvas canvas) {
        //  最大值
        canvas.drawText(String.format("%.2f", max * 100 / 100.0) + "%", xori + 6, 3 * mMargin10 + 6, mPaintText);
        //  最小值
        canvas.drawText(String.format("%.2f", min * 100 / 100.0) + "%", xori + 6, yori - 6, mPaintText);
        //  中间值
        canvas.drawText((String.format("%.2f", (min + max) * 100 / 200.0) + "%"), xori + 6, yori / 2 + 5, mPaintText);

        //  开始日期
        canvas.drawText(startTime, xori, mHeight - 2 * mMargin10, mPaintText);
        //  结束日期
        canvas.drawText(endTime, mWidth - timeWidth - mMargin10, mHeight - 2 * mMargin10, mPaintText);
    }

    //  画坐标轴
    private void drawAxes(Canvas canvas) {
        //  X轴
        canvas.drawLine(xori, yori, mWidth - mMargin10, yori, mPaintAxes);
        //  X中轴线
        canvas.drawLine(xori, yori / 2, mWidth - mMargin10, yori / 2, mPaintAxes);
        //  X上边线
        canvas.drawLine(xori, mMargin10, mWidth - mMargin10, mMargin10, mPaintAxes);
        //  画Y轴
        canvas.drawLine(xori, yori, xori, mMargin10, mPaintAxes);
        //  Y右边线
        canvas.drawLine(mWidth - mMargin10, mMargin10, mWidth - mMargin10, yori, mPaintAxes);
    }

    public static class ItemBean {

        private long Timestamp;

        private int value;

        public ItemBean(long timestamp, int value) {
            super();
            Timestamp = timestamp;
            this.value = value;
        }

        public long getTimestamp() {
            return Timestamp;
        }

        public void setTimestamp(long timestamp) {
            Timestamp = timestamp;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

    }


    public static String timeStampToString(Long num) {
        Timestamp ts = new Timestamp(num * 1000);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(ts);
    }

    public void setPercentage(float percentage) {
        if (percentage < 0.0f || percentage > 1.0f) {
            throw new IllegalArgumentException(
                    "setPercentage not between 0.0f and 1.0f");
        }

        mProgress = percentage;
        invalidate();
    }
}
