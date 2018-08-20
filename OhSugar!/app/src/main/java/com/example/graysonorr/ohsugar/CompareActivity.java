package com.example.graysonorr.ohsugar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;

import org.w3c.dom.Text;

import java.util.Locale;

public class CompareActivity extends AppCompatActivity {

    TextView compare1;
    TextView product1Title;
    TextView product2Title;
    TextView product1Sugar;
    TextView product2Sugar;
    TextView compare2;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        SharedPreferences sharedPref = CompareActivity.this.getSharedPreferences("conversions", Context.MODE_PRIVATE);

        compare1 = (TextView) findViewById(R.id.compare1);
        compare2 = (TextView) findViewById(R.id.compare2);
        product1Title = (TextView) findViewById(R.id.product1Title);
        product2Title = (TextView) findViewById(R.id.product2Title);
        product1Sugar = (TextView) findViewById(R.id.product1Sugar);
        product2Sugar = (TextView) findViewById(R.id.product2Sugar);

        db = AppDatabase.getInMemoryDatabase(getApplicationContext());

        TextView comp1Measure = (TextView) findViewById(R.id.product1Measure);
        TextView comp2Measure = (TextView) findViewById(R.id.product2Measure);

        comp1Measure.setText(sharedPref.getString("abbreviation", "grams"));
        comp2Measure.setText(sharedPref.getString("abbreviation", "grams"));

        compare1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompareActivity.this, SearchReturn.class);
                startActivityForResult(intent, 1);
            }
        });

        compare2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompareActivity.this, SearchReturn.class);
                startActivityForResult(intent, 2);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            String currentQRCode;
            String name;
            Double sugar;
            if (requestCode == 1) {
                name = data.getStringExtra("Name");
                sugar = data.getDoubleExtra("Sugar", 1.0);
                if (name != null) {
                    compare1.setText(name);
                    showOutput(name, sugar, product1Title, product1Sugar);
                }

            }

            if (requestCode == 2) {
                name = data.getStringExtra("Name");
                sugar = data.getDoubleExtra("Sugar", 1.0);
                if (name != null) {
                    compare2.setText(name);
                    showOutput(name, sugar, product2Title, product2Sugar);
                }
            }
        }
    }

    private void fetchData(String barcode, TextView name, TextView sugar) {
        // This activity is executing a query on the main thread, making the UI perform badly.
        Food food = db.foodDao().findByBarcode(barcode);
        //Food food = db.foodDao().findByID(1);

        if(food != null){
            showOutput(food, name, sugar);
        }

    }

    private void showOutput(Food food, TextView name, TextView sugar){
        name.setText(food.name);
        SharedPreferences sharedPref = CompareActivity.this.getSharedPreferences("conversions", Context.MODE_PRIVATE);
        sugar.setText(Integer.toString((int)(food.sugar / sharedPref.getFloat("floatMeasure", 1.0f) + 0.5d)));
    }

    private void showOutput(String name, Double sugar, TextView nameTv, TextView sugarTv){
        nameTv.setText(name);
        SharedPreferences sharedPref = CompareActivity.this.getSharedPreferences("conversions", Context.MODE_PRIVATE);
        sugarTv.setText(Integer.toString((int)(sugar / sharedPref.getFloat("floatMeasure", 1.0f) + 0.5d)));
    }

}
