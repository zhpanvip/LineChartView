package com.example.zhpan.linechartview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.zhpan.linechartview.LineChartView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private LineChartView mLineChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

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

    public void onClick(View view) {
        //  开启动画
        mLineChartView.startAnim(2000);
    }

    private void initView() {
        mLineChartView = (LineChartView) findViewById(R.id.lcv);
    }
}
