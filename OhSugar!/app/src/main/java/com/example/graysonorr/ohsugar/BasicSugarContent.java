package com.example.graysonorr.ohsugar;

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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicSugarContent extends AppCompatActivity {
    private String productName;


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

        AsyncScraper scraper = new AsyncScraper(BasicSugarContent.this, URL);
        scraper.execute();

        Button addBtn = (Button) findViewById(R.id.addBtn);
        Button returnBtn = (Button) findViewById(R.id.returnBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });






    }

    private void fillText(ArrayList<Double> sugar){
        SharedPreferences sharedPref = BasicSugarContent.this.getSharedPreferences("conversions", Context.MODE_PRIVATE);
        TextView tvSugar = (TextView) findViewById(R.id.foodSugar);
        tvSugar.setText(Double.toString(sugar.get(0)/sharedPref.getFloat("floatMeasure", 1.0f)) + " " + sharedPref.getString("abbreviation", "g"));
        TextView tvSugar100 = (TextView) findViewById(R.id.foodSugar100);
        tvSugar100.setText(Double.toString(sugar.get(1)) + "g per 100g");
    }

    private void fillText(){
        TextView tvSugar = (TextView) findViewById(R.id.foodSugar);
        tvSugar.setText("Sugar Content Unavailable");
    }




    class AsyncScraper extends AsyncTask<String, Void, Food> {
        HashMap<String, String> toReturn;
        private Context context;
        private String searchRequest;
        ArrayList<String> allContent;

        public AsyncScraper(Context context, String search){
            this.context = context;
            this.searchRequest = search;
            searchRequest = search.replace(' ', '+');
        }

        protected Food doInBackground(String... search){
            Food food = CountdownScraper.retrieveFoodDataURL(searchRequest, productName);
            return food;
        }

        protected void onPostExecute(Food food){

            if(food != null){
                AppDatabase db = AppDatabase.getInMemoryDatabase(getApplicationContext());
                db.foodDao().insertFood(food);
                GlobalDBUtils.insertFood(food, BasicSugarContent.this);
                ArrayList<Double> fetchedSugar = new ArrayList<Double>();
                fetchedSugar.add(food.sugarServing);
                fetchedSugar.add(food.sugar100);
                fillText(fetchedSugar);
            }

        }

    }


}
