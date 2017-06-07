效果图，动态图片录制效果不好，凑合看吧。
![image](http://img.blog.csdn.net/20170316135432050?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjA1MjE1NzM=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
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
详情请参看：http://blog.csdn.net/qq_20521573/article/details/62421993
