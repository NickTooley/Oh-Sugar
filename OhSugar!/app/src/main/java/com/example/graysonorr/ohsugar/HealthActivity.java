package com.example.graysonorr.ohsugar;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

public class HealthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        LineChart chart = (LineChart) findViewById(R.id.chart);
    }
}
