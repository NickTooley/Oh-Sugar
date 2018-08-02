package com.example.graysonorr.ohsugar;

import android.content.Intent;
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

        compare1 = (TextView) findViewById(R.id.compare1);
        compare2 = (TextView) findViewById(R.id.compare2);
        product1Title = (TextView) findViewById(R.id.product1Title);
        product2Title = (TextView) findViewById(R.id.product2Title);
        product1Sugar = (TextView) findViewById(R.id.product1Sugar);
        product2Sugar = (TextView) findViewById(R.id.product2Sugar);

        db = AppDatabase.getInMemoryDatabase(getApplicationContext());

        compare1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompareActivity.this, BarcodeScanner.class);
                startActivityForResult(intent, 1);
            }
        });

        compare2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompareActivity.this, BarcodeScanner.class);
                startActivityForResult(intent, 2);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String currentQRCode;
        if (requestCode == 1) {
            currentQRCode = data.getStringExtra("nada");
            if(currentQRCode!=null) {
                compare1.setText(currentQRCode);
                fetchData(currentQRCode, product1Title, product1Sugar);
            }

        }

        if (requestCode == 2) {
            currentQRCode = data.getStringExtra("nada");
            if(currentQRCode!=null) {
                compare2.setText(currentQRCode);
                fetchData(currentQRCode, product2Title, product2Sugar);
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
        sugar.setText(Integer.toString((int)(food.sugar + 0.5d)));
    }


}
