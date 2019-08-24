带动画效果的颜色渐变折线图

![这里写图片描述](https://github.com/zhpanvip/LineChartView/blob/master/image/ezgif-5-47fee30472.gif)

如何使用LineChartView

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
        List<Float> listValues = new ArrayList<>();
        Random random = new Random();
        float startValue = random.nextFloat() * 10;
        listValues.add(startValue);
        for (int i = 0; i < 30; i++) {
            startValue += random.nextFloat() * 10 - 5;
            listValues.add(startValue);
        }
        List<Integer> listShadeColors = new ArrayList<>();
        listShadeColors.add(Color.argb(100, 255, 86, 86));
        listShadeColors.add(Color.argb(15, 255, 86, 86));
        listShadeColors.add(Color.argb(0, 255, 86, 86));
        //  设置折线数据
        mLineChartView.setValues(listValues);
        //  设置渐变颜色
        mLineChartView.setShadeColors(listShadeColors);
        //  设置动画插值器
        mLineChartView.setInterpolator(new DecelerateInterpolator());
        mLineChartView.setAxisMinValue(-30);
        mLineChartView.setAxisMaxValue(30);
        mLineChartView.setStartTime("2017-03-15");
        mLineChartView.setEndTime("2017-04-14");
        //  开启动画
        mLineChartView.startAnim(2500);
    }
```
详情请参看：http://blog.csdn.net/qq_20521573/article/details/62421993
