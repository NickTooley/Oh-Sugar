package com.example.graysonorr.ohsugar;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.DrawableRes;
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

import com.example.graysonorr.ohsugar.db.ShoppingList;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HealthActivity extends AppCompatActivity {

    private BarChart barChart;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

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

        sharedPreferences = getSharedPreferences("Health Entries", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();

        barChart = (BarChart) findViewById(R.id.chart);
        barChart.setNoDataText("No health data entered.");
        Paint p = barChart.getPaint(Chart.PAINT_INFO);
        p.setTextSize(75);
        p.setColor(Color.RED);

        List<Data> data = getData();

        if(data.size()!=0){
            setData(data);

            barChart.setDragEnabled(true);
            barChart.setPinchZoom(true);
            barChart.setDoubleTapToZoomEnabled(true);
            barChart.setHorizontalScrollBarEnabled(true);
            barChart.setDescription(null);
            barChart.setFitBars(true); // make the x-axis fit exactly all bars
            //barChart.setVisibleXRangeMinimum(getData().size()); // amount of bars visible

            YAxis left = barChart.getAxisLeft();
            left.setDrawGridLines(false); // no grid lines
            left.setDrawZeroLine(true); // draw a zero line
            left.setAxisMaximum((barChart.getYMax()+50));
            left.setAxisMinimum((barChart.getYMin()-50));

            barChart.getAxisRight().setEnabled(false); // no right axis

            XAxis xAxis = barChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setTextSize(10f);
            xAxis.setDrawAxisLine(true);
            xAxis.setDrawGridLines(false);
            xAxis.setLabelRotationAngle(-45);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            Legend legend = barChart.getLegend();
            legend.setEnabled(false);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        barChart.invalidate();
    }

    private class Data{
        public float xVal;
        public float yVal;
        public String xAxisVal;

        public Data(float xVal, float yVal, String xAxisVal){
            this.xVal = xVal;
            this.yVal = yVal;
            this.xAxisVal = xAxisVal;
        }
    }

    private void setData(List<Data> dataList){
        ArrayList<BarEntry> values = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        final List<String> xVal = new ArrayList<>();

        int green = Color.rgb(110,190,102);
        int red = Color.rgb(211, 87, 44);

        for(int i=0; i<dataList.size(); i++){
            Data d = dataList.get(i);
            BarEntry entry = new BarEntry(d.xVal, d.yVal);
            values.add(entry);
            xVal.add(d.xAxisVal);

            if(d.yVal>0){
                colors.add(red);
            }
            else{
                colors.add(green);
            }
        }

        BarDataSet set = new BarDataSet(values, "Values");
        set.setColors(colors);
        set.setValueTextColors(colors);

        BarData data = new BarData(set);
        data.setValueTextSize(10f);
        data.setValueFormatter(new ValueFormatter());

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xVal.get((int) value); // xVal is a string array
            }
        });
        barChart.setData(data);
        barChart.invalidate();
    }

    private class ValueFormatter implements IValueFormatter {
        private DecimalFormat format;

        public ValueFormatter(){
            format = new DecimalFormat("####.00");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return format.format(value);
        }
    }

    public List<Data> getData(){
        List<Data> data = new ArrayList<>();

        String json = sharedPreferences.getString("entries", null);
        Type type = new TypeToken<List<ShoppingList>>() {}.getType();
        List<ShoppingList> list = gson.fromJson(json, type);

        if(list.get(0)!=null){
            for(int i =0; i < list.size(); i++){
                double xVal = (list.get(i).getTotalSugar(this)-list.get(i).getRecSugar(this));
                data.add(new Data((float) i, (float) xVal, list.get(i).getTimestamp()));
            }
        }

        return data;
    }
}
