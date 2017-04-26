首先看下要实现的效果图，动态图片录制效果不好，渐变颜色没有完全显示出来，以至于下半部分渐变色变成了白色。
![这里写图片描述](http://img.blog.csdn.net/20170316135432050?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjA1MjE1NzM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
折线图的绘制主要有一下几个步骤。
一、定义LineChartView类并继承View。
二、添加自定义属性。以在value目录下创建attrs.xml文件,文件中我们可以定义一些用到的属性，比如折线颜色、字体大小等属性。文件内容如下：

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <declare-styleable name="LineChartView">
    <attr name="axesColor" format="color"/> <!--坐标轴颜色-->
    <attr name="axesWidth" format="dimension"/><!--坐标轴宽度-->
    <attr name="textColor" format="color"/> <!--字体颜色-->
    <attr name="textSize" format="dimension"/> <!--字体大小-->
    <attr name="lineColor" format="color"/> <!--折线颜色-->
    <attr name="bgColor" format="color"/> <!--背景色-->
  </declare-styleable>
</resources>
```
接下来在LineChartView的构造方法中解析自定义属性的值并做相应的处理。在构造方法里还初始化了渐变颜色、折线数据的List集合以及初始化画笔等操作代码如下：

```
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
        //  初始化折线数据集合
        mItems = new ArrayList<>();
        mMargin10 = ScreenUtils.dp2px(context, 10);
        init();
    }
```
另外，折现数据需要实体类，实体类可直接添加到LineChartView内部。如下：

```
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
```

三、初始化画笔和路径。代码如下：

```
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

        //  初始化折线画笔
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
```
四、重写onLayout方法。在onLayout方法中获取控件的宽高、初始化原点坐标以及设置控件的背景。代码如下：

```
 @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {

            mWidth = getWidth();
            mHeight = getHeight();
            timeWidth = (int) mPaintText.measureText(startTime);
            //  初始化原点坐标
            xOrigin = 0 + mMargin10;
            yOrigin = (mHeight - mTextSize - mMargin10);

            //  设置背景色
            setBackgroundColor(mBgColor);
        }
    }
```
五、重写onDraw方法。在onDraw方法中完成折线图的绘制。代码如下：

```
 @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //  Y轴坐标间距
        yInterval = (max - min) / (yOrigin - mMargin10);
		//  X轴坐标间距
        xInterval = (mWidth - xOrigin) / (mItems.size() - 1);
        //  画坐标轴
        drawAxes(canvas);
        //  画文字
        drawText(canvas);
        //  画折线
        drawLine(canvas);
		//  设置动画
		setAnim(canvas)
    }
```
折线图的绘制可以分三部分：1.绘制坐标轴。2.绘制View上的文字。3.绘制折线。

1.坐标轴绘制的是第一象限，即左下角的点为原点。绘制坐标轴代码如下：

```
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
```
2.绘制文字，代码如下：

```
private void drawText(Canvas canvas) {
        //  绘制最大值
        canvas.drawText(String.format("%.2f", max * 100 / 100.0) + "%", xOrigin + 6, 2 * mMargin10, mPaintText);
        //  绘制最小值
        canvas.drawText(String.format("%.2f", min * 100 / 100.0) + "%", xOrigin + 6, yOrigin - 6, mPaintText);
        //  绘制中间值
        canvas.drawText((String.format("%.2f", (min + max) * 100 / 200.0) + "%"), xOrigin + 6, (yOrigin + mMargin10) / 2, mPaintText);

        //  绘制开始日期
        canvas.drawText(startTime, xOrigin, mHeight - mMargin10, mPaintText);
        //  绘制结束日期
        canvas.drawText(endTime, mWidth - timeWidth - mMargin10, mHeight - mMargin10, mPaintText);
    }
```
3.绘制折线及渐变填充

```
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
```
六、折线图添加动画。
1.首先需要计算动画的进度，因此在LineChartView中定义成员变量mProgress,并添加以下方法：

```
    /**
     * Animate this property. It is the percentage of the path that is drawn.
     * It must be [0,1].
     *
     * @param percentage float the percentage of the path.
     */
    public void setPercentage(float percentage) {
        if (percentage < 0.0f || percentage > 1.0f) {
            throw new IllegalArgumentException(
                    "setPercentage not between 0.0f and 1.0f");
        }
        mProgress = percentage;
        invalidate();
    }
```
2.接下来设置动画效果，代码如下：

```
 private void setAnim(Canvas canvas) {

        PathMeasure measure = new PathMeasure(mPath, false);
        float pathLength = measure.getLength();
        PathEffect effect = new DashPathEffect(new float[]{pathLength,
                pathLength}, pathLength - pathLength * mProgress);
        mPaintLine.setPathEffect(effect);
        canvas.drawPath(mPath, mPaintLine);
    }
```
3.添加开启动画的方法：

```
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
```

至此，折线图的绘制已经全部完成。最后还可以添加get() set()方法，暴露出属性接口，以供外部调用。代码就不再贴出来了。

七、使用LineChartView
1.在布局文件中添加LineChartView,可设置折线颜色、字体颜色、等属性，如下：

```
<com.example.zhpan.linechartview.LineChartView
        android:id="@+id/lcv"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:layout_marginBottom="20dp"
        android:layout_marginLeft="15dp"
        android:layout_marginRight="15dp"
        android:layout_marginTop="15dp"
        app:lineColor="#FF0000"
        app:textColor="#ABABAB"
        app:textSize="12dp"/>
```

2.在Activity中为LineChartView设置数据，也可以通过代码为其设置属性。

```
private void initData() {
        //  初始化折线数据
        mItems = new ArrayList<>();
        mItems.add(new LineChartView.ItemBean(1489507200, 23));
        mItems.add(new LineChartView.ItemBean(1489593600, 88));
        mItems.add(new LineChartView.ItemBean(1489680000, 60));
        mItems.add(new LineChartView.ItemBean(1489766400, 50));
        mItems.add(new LineChartView.ItemBean(1489852800, 70));
        mItems.add(new LineChartView.ItemBean(1489939200, 10));
        mItems.add(new LineChartView.ItemBean(1490025600, 33));
        mItems.add(new LineChartView.ItemBean(1490112000, 44));
        mItems.add(new LineChartView.ItemBean(1490198400, 99));
        mItems.add(new LineChartView.ItemBean(1490284800, 17));

        shadeColors= new int[]{
                Color.argb(100, 255, 86, 86), Color.argb(15, 255, 86, 86),
                Color.argb(0, 255, 86, 86)};

        //  设置折线数据
        mLineChartView.setItems(mItems);
        //  设置渐变颜色
        mLineChartView.setShadeColors(shadeColors);
        //  开启动画
        mLineChartView.startAnim(mLineChartView,2000);
    }
```

