package com.example.zhpan.linechartview;

import android.animation.ObjectAnimator;
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
import android.view.animation.LinearInterpolator;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by zhpan on 2017/3/14.
 */

public class LineChartView extends View {
    private float xOrigin; //  x轴原点坐标
    private float yOrigin;  //  y轴原点坐标
    private int mMargin10;  //  10dp的间距
    private int mWidth; //  控件宽度
    private int mHeight;  //  控件高度
    private int max = 100, min = 0;  //  最大值、最小值
    private float yInterval;  //  y轴坐标间隔
    private float xInterval;  //  x轴坐标间隔

    private String startTime = "2017-03-15";
    private String endTime = "2017-03-24";
    private int timeWidth;  //  日期宽度

    private List<ItemBean> mItems;//  折线数据

    private int[] shadeColors; //  渐变阴影颜色

    private int mAxesColor; //  坐标轴颜色
    private float mAxesWidth; //  坐标轴宽度
    private int mTextColor;  //  字体颜色
    private float mTextSize; //  字体大小
    private int mLineColor;  //  折线颜色
    private int mBgColor;       //  背景色

    private Paint mPaintText;     //  画文字的画笔
    private Paint mPaintAxes;   //  坐标轴画笔
    private Paint mPaintLine;   //  折线画笔
    private Path mPath;   //    折线路径
    private Paint mPaintShader; //  渐变色画笔
    private Path mPathShader;   //  渐变色路径
    private float mProgress;    //  动画进度

    public int[] getShadeColors() {
        return shadeColors;
    }

    public void setShadeColors(int[] shadeColors) {
        this.shadeColors = shadeColors;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<ItemBean> getItems() {
        return mItems;
    }

    public void setItems(List<ItemBean> items) {
        mItems = items;
    }

    public int getAxesColor() {
        return mAxesColor;
    }

    public void setAxesColor(int axesColor) {
        mAxesColor = axesColor;
    }

    public float getAxesWidth() {
        return mAxesWidth;
    }

    public void setAxesWidth(float axesWidth) {
        mAxesWidth = axesWidth;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
    }


    public int getLineColor() {
        return mLineColor;
    }

    public void setLineColor(int lineColor) {
        mLineColor = lineColor;
    }

    public int getBgColor() {
        return mBgColor;
    }

    public void setBgColor(int bgColor) {
        mBgColor = bgColor;
    }

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
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineChartView);
        mAxesColor = typedArray.getColor(R.styleable.LineChartView_axesColor, Color.parseColor("#CCCCCC"));
        mAxesWidth = typedArray.getDimension(R.styleable.LineChartView_axesWidth, 1);
        mTextColor = typedArray.getColor(R.styleable.LineChartView_textColor, Color.parseColor("#ABABAB"));
        mTextSize = typedArray.getDimension(R.styleable.LineChartView_textSize, 32);
        mLineColor = typedArray.getColor(R.styleable.LineChartView_lineColor, Color.RED);
        mBgColor = typedArray.getColor(R.styleable.LineChartView_bgColor, Color.WHITE);
        typedArray.recycle();

        //  初始化渐变色
        shadeColors = new int[]{
                Color.argb(100, 255, 86, 86), Color.argb(15, 255, 86, 86),
                Color.argb(0, 255, 86, 86)};
        //  初始化折线数据
        mItems = new ArrayList<>();
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
        mPaintText.setTextSize(mTextSize);
        mPaintText.setColor(mTextColor);
        mPaintText.setTextAlign(Paint.Align.LEFT);

        //  初始化折线的画笔
        mPaintLine = new Paint();
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeWidth(mAxesWidth / 2);
        mPaintLine.setColor(mLineColor);

        //  初始化折线路径
        mPath = new Path();
        mPathShader = new Path();

        //  阴影画笔
        mPaintShader = new Paint();
        mPaintShader.setAntiAlias(true);
        mPaintShader.setStrokeWidth(2f);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {

            mWidth = getWidth();
            mHeight = getHeight();
            timeWidth = (int) mPaintText.measureText(startTime);
            //  初始化原点坐标
            xOrigin = mMargin10;
            yOrigin = (mHeight - mTextSize - mMargin10);

            //  设置背景色
            setBackgroundColor(mBgColor);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //  Y轴间距
        yInterval = (max - min) / (yOrigin - mMargin10);
        xInterval = (mWidth - xOrigin) / (mItems.size() - 1);
        //  画坐标轴
        drawAxes(canvas);
        //  画文字
        drawText(canvas);
        //  画折线
        drawLine(canvas);

        //  设置动画
        setAnim(canvas);
    }

    private void setAnim(Canvas canvas) {

        PathMeasure measure = new PathMeasure(mPath, false);
        float pathLength = measure.getLength();
        PathEffect effect = new DashPathEffect(new float[]{pathLength,
                pathLength}, pathLength - pathLength * mProgress);
        mPaintLine.setPathEffect(effect);
        canvas.drawPath(mPath, mPaintLine);
    }

    private void drawLine(Canvas canvas) {
        //  画坐标点
        for (int i = 0; i < mItems.size(); i++) {
            float x = i * xInterval + xOrigin + mAxesWidth;
            if (i == 0) {
                mPathShader.moveTo(x, yOrigin - (mItems.get(i).getValue() - min) / yInterval);
                mPath.moveTo(x, yOrigin - (mItems.get(i).getValue() - min) / yInterval);
            } else {
                mPath.lineTo(x - mMargin10 - mAxesWidth, yOrigin - (mItems.get(i).getValue() - min) / yInterval);
                mPathShader.lineTo(x - mMargin10 - mAxesWidth, yOrigin - (mItems.get(i).getValue() - min) / yInterval);
                if (i == mItems.size() - 1) {
                    mPathShader.lineTo(x - mMargin10 - mAxesWidth, yOrigin);
                    mPathShader.lineTo(xOrigin, yOrigin);
                    mPathShader.close();
                }
            }
        }

        //  渐变阴影
        Shader mShader = new LinearGradient(0, 0, 0, getHeight(), shadeColors, null, Shader.TileMode.CLAMP);
        mPaintShader.setShader(mShader);

        //  绘制渐变阴影
        canvas.drawPath(mPathShader, mPaintShader);
    }

    private void drawText(Canvas canvas) {

        //  绘制最大值
        String maxValue=String.format(Locale.getDefault(),"%.2f", max * 100 / 100.0) + "%";
        canvas.drawText(maxValue, xOrigin + 6, 2 * mMargin10, mPaintText);
        //  绘制最小值
        String minValue=String.format(Locale.getDefault(),"%.2f", min * 100 / 100.0) + "%";
        canvas.drawText(minValue, xOrigin + 6, yOrigin - 6, mPaintText);
        //  绘制中间值
        String midValue=String.format(Locale.getDefault(),"%.2f", (min + max) * 100 / 200.0) + "%";
        canvas.drawText(midValue, xOrigin + 6, (yOrigin + mMargin10) / 2, mPaintText);

        //  绘制开始日期
        canvas.drawText(startTime, xOrigin, mHeight - mMargin10, mPaintText);
        //  绘制结束日期
        canvas.drawText(endTime, mWidth - timeWidth - mMargin10, mHeight - mMargin10, mPaintText);
    }

    //  画坐标轴
    private void drawAxes(Canvas canvas) {
        //  绘制X轴
        canvas.drawLine(xOrigin, yOrigin, mWidth - mMargin10, yOrigin, mPaintAxes);
        //  绘制X中轴线
        canvas.drawLine(xOrigin, yOrigin / 2, mWidth - mMargin10, yOrigin / 2, mPaintAxes);
        //  绘制X上边线
        canvas.drawLine(xOrigin, mMargin10, mWidth - mMargin10, mMargin10, mPaintAxes);
        //  绘制画Y轴
        canvas.drawLine(xOrigin, yOrigin, xOrigin, mMargin10, mPaintAxes);
        //  绘制Y右边线
        canvas.drawLine(mWidth - mMargin10, mMargin10, mWidth - mMargin10, yOrigin, mPaintAxes);
    }


    public static String timeStampToString(Long num) {
        Timestamp ts = new Timestamp(num * 1000);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(ts);
    }

    //  计算动画进度
    public void setPercentage(float percentage) {
        if (percentage < 0.0f || percentage > 1.0f) {
            throw new IllegalArgumentException(
                    "setPercentage not between 0.0f and 1.0f");
        }
        mProgress = percentage;
        invalidate();
    }

    /**
     * @param lineChartView
     * @param duration      动画持续时间
     */
    public void startAnim(LineChartView lineChartView, long duration) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(lineChartView, "percentage", 0.0f, 1.0f);
        anim.setDuration(duration);
        anim.setInterpolator(new LinearInterpolator());
        anim.start();
    }

    //  折线数据的实体类
    public static class ItemBean {

        private long Timestamp;
        private int value;

        public ItemBean(){}


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
}
