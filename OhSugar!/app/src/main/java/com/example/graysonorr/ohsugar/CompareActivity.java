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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;

import org.w3c.dom.Text;

import java.util.List;
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

        int mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
        View view = getLayoutInflater().inflate(R.layout.activity_compare, null);
        setContentView(view, new ViewGroup.LayoutParams(mScreenWidth, mScreenHeight));

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_top);
            setSupportActionBar(toolbar);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

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

        comp1Measure.setText(sharedPref.getString("abbreviation", "g"));
        comp2Measure.setText(sharedPref.getString("abbreviation", "g"));

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

        TextView newItem1 = (TextView) findViewById(R.id.selectNew1);
        TextView newItem2 = (TextView) findViewById(R.id.selectNew2);

        newItem1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompareActivity.this, SearchReturn.class);
                startActivityForResult(intent, 1);
            }
        });

        newItem2.setOnClickListener(new View.OnClickListener() {
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
                    Food food1 = db.foodDao().searchByName(name).get(0);
                    compare1.setText(name);
                    //showOutput(name, sugar, product1Title, product1Sugar);
                    showOutput1(food1, product1Title, product1Sugar);
                }
            }
            if (requestCode == 2) {
                name = data.getStringExtra("Name");
                sugar = data.getDoubleExtra("Sugar", 1.0);
                if (name != null) {
                    compare2.setText(name);
                    Food food2 = db.foodDao().searchByName(name).get(0);
                    showOutput2(food2, product2Title, product2Sugar);
                }
            }
        }
    }

    private void fetchData(String barcode, TextView name, TextView sugar) {
        // This activity is executing a query on the main thread, making the UI perform badly.
        Food food = db.foodDao().findByBarcode(barcode);
        //Food food = db.foodDao().findByID(1);

        if(food != null){
            //showOutput(food, name, sugar);
        }

    }

    private void showOutput1(Food food, TextView name, TextView sugar){

        final Food food2 = food;
        name.setText(food.name);
        SharedPreferences sharedPref = CompareActivity.this.getSharedPreferences("conversions", Context.MODE_PRIVATE);
        sugar.setText(Integer.toString((int)(food.sugar100 / sharedPref.getFloat("floatMeasure", 1.0f) + 0.5d)));

        String category = food.category;
        List<Food> healthy = db.foodDao().searchHealthyAlt(category, food.sugar100);
        if(healthy.size() > 0){
            final Food healthyFood = healthy.get(0);
            TextView healthyAlt1Tv = (TextView) findViewById(R.id.healthyAlt1);
            healthyAlt1Tv.setText(healthyFood.name);
            healthyAlt1Tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int foodID = healthyFood.foodID;

                    Intent intent = new Intent(getApplicationContext(), MoreInfoActivity.class);
                    intent.putExtra("ID", foodID);
                    startActivity(intent);
                }
            });
        }

        TextView addToList = (TextView) findViewById(R.id.addBtn1);
        addToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int foodID = food2.foodID;

                Intent intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
                intent.putExtra("ID", foodID);
                startActivity(intent);
            }
        });

    }

    private void showOutput2(Food food, TextView name, TextView sugar){

        final Food food1 = food;
        name.setText(food.name);
        SharedPreferences sharedPref = CompareActivity.this.getSharedPreferences("conversions", Context.MODE_PRIVATE);
        sugar.setText(Integer.toString((int)(food.sugar100 / sharedPref.getFloat("floatMeasure", 1.0f) + 0.5d)));

        String category = food.category;
        List<Food> healthy = db.foodDao().searchHealthyAlt(category, food.sugar100);
        if(healthy.size() > 0){
            final Food healthyFood = healthy.get(0);
            TextView healthyAlt1Tv = (TextView) findViewById(R.id.healthyAlt2);
            healthyAlt1Tv.setText(healthyFood.name);
            healthyAlt1Tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int foodID = healthyFood.foodID;

                    Intent intent = new Intent(getApplicationContext(), MoreInfoActivity.class);
                    intent.putExtra("ID", foodID);
                    startActivity(intent);
                }
            });
        }

        TextView addToList = (TextView) findViewById(R.id.addBtn2);
        addToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int foodID = food1.foodID;

                Intent intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
                intent.putExtra("ID", foodID);
                startActivity(intent);
            }
        });

    }

    private void showOutput(String name, Double sugar, TextView nameTv, TextView sugarTv){
        nameTv.setText(name);
        SharedPreferences sharedPref = CompareActivity.this.getSharedPreferences("conversions", Context.MODE_PRIVATE);
        sugarTv.setText(Integer.toString((int)(sugar / sharedPref.getFloat("floatMeasure", 1.0f) + 0.5d)));
    }

}
