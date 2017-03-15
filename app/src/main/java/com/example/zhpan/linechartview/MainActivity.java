package com.example.zhpan.linechartview;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    private LineChartView mLineChartView;
    private LinearLayout mActivityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        startAnim();
    }

    private void initView() {
        mLineChartView = (LineChartView) findViewById(R.id.lcv);
        mActivityMain = (LinearLayout) findViewById(R.id.activity_main);
    }

    private void startAnim() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mLineChartView, "percentage", 0.0f, 1.0f);
        anim.setDuration(1000);
        anim.setInterpolator(new LinearInterpolator());
        anim.start();
    }
}
