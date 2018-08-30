package com.example.graysonorr.ohsugar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;
import com.example.graysonorr.ohsugar.db.utils.*;
import com.example.graysonorr.ohsugar.db.utils.CountdownScraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicSugarContentReturn extends AppCompatActivity {
    private String productName;
    Button addBtn;
    Button returnBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_sugar_content);

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        Intent prevIntent = getIntent();

        String URL = prevIntent.getStringExtra("URL");
        productName = prevIntent.getStringExtra("Name");

        Log.d("New Intent", productName);
        Log.d("New Intent", URL);


        TextView tvName = (TextView) findViewById(R.id.foodTitle);

        tvName.setText(productName);

        AsyncScraper scraper = new AsyncScraper(this, URL);
        scraper.execute();

        addBtn = (Button) findViewById(R.id.addBtn);
        returnBtn = (Button) findViewById(R.id.returnBtn);


    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent();
        setResult(0, intent);
        finish();
    }

    private void fillText(double sugar){
        SharedPreferences sharedPref = BasicSugarContentReturn.this.getSharedPreferences("conversions", Context.MODE_PRIVATE);
        TextView tvSugar = (TextView) findViewById(R.id.foodSugar);
        double convertedSugar = sugar / sharedPref.getFloat("floatMeasure", 1.0f);
        String abbreviation = sharedPref.getString("abbreviation", "g");
        tvSugar.setText(convertedSugar + " " + abbreviation);
    }

    private void fillText(){
        TextView tvSugar = (TextView) findViewById(R.id.foodSugar);
        tvSugar.setText("Sugar Content Unavailable");
    }




    class AsyncScraper extends AsyncTask<String, Void, Food> {
        HashMap<String, String> toReturn;
        private Context context;
        private String searchRequest;
        private ProgressDialog dialog;


        public AsyncScraper(Context context, String search){
            this.context = context;
            this.searchRequest = search;
            searchRequest = search.replace(' ', '+');
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Retrieving barcode information");
            dialog.show();
        }

        protected Food doInBackground(String... search){
            Food food = CountdownScraper.retrieveFoodDataURL(searchRequest, productName);
            return food;
        }

        protected void onPostExecute(final Food food){



            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(food.sugar100 != 101){
                AppDatabase db = AppDatabase.getInMemoryDatabase(getApplicationContext());
                db.foodDao().insertFood(food);
                GlobalDBUtils.insertFood(food, BasicSugarContentReturn.this);
                fillText(food.sugarServing);
            }

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("Name", food.name);
                    intent.putExtra("Sugar", food.sugarServing);
                    intent.putExtra("Sugar100", food.sugar100);
                    intent.putExtra("Category", food.category);
                    //intent.putExtra("Barcode", foods.barcode);
                    //intent.putExtra("ID", foods.foodID);
                    Log.d("test", "Does this work?");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

            returnBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("test", "wb dis");
                    Intent intent = new Intent();
                    setResult(0, intent);
                    finish();
                }
            });
        }
    }
}
