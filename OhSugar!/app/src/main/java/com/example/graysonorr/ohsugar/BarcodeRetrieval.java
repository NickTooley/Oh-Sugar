package com.example.graysonorr.ohsugar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IInterface;
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

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BarcodeRetrieval extends AppCompatActivity {
    private TextView barcodeText;
    private TextView productNameText;
    private TextView sugarContentText;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_retrieval);

        db = AppDatabase.getInMemoryDatabase(getApplicationContext());
        //populateDb();

        barcodeText = findViewById(R.id.barcodeText);
        productNameText = findViewById(R.id.productNameText);
        sugarContentText = findViewById(R.id.sugarText);

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        Button scanBtn = (Button) findViewById(R.id.scanBtn);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BarcodeRetrieval.this, BarcodeScanner.class);
                startActivityForResult(intent, 1);
            }
        });

        Intent intent = new Intent(BarcodeRetrieval.this, BarcodeScanner.class);
        startActivityForResult(intent, 1);






    }

    private void populateDb() {
        dbinit.populateAsync(db);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
        Log.d("ResultCode", Integer.toString(resultCode));
            String currentQRCode;
            if (requestCode == 1) {
                currentQRCode = data.getStringExtra("nada");
                if (currentQRCode != null) {
                    barcodeText.setText(currentQRCode);
                    fetchData(currentQRCode);
                }

            }
        }else{
            finish();
        }
    }

    private void showOutput(Food food){
        productNameText.setText(food.name);
        SharedPreferences sharedPref = BarcodeRetrieval.this.getSharedPreferences("conversions", Context.MODE_PRIVATE);
        String abbrev = sharedPref.getString("abbreviation", "g");
        Float measure = sharedPref.getFloat("floatMeasure", 1.0f);
        sugarContentText.setText((food.sugarServing / measure) + abbrev);
    }

    private void fetchData(String barcode) {
        // This activity is executing a query on the main thread, making the UI perform badly.
        Log.d("Barcode", barcode);
        Food food = db.foodDao().findByBarcode(barcode);
        //Food food = db.foodDao().findByID(1);

        if(food != null){
            showOutput(food);
        }else{
            AsyncScraper scraper = new AsyncScraper(this, barcode);
            scraper.execute();
        }

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

        protected Food doInBackground(String... search) {
            Food food = CountdownScraper.retrieveFoodDataBarcode(searchRequest);
            return food;
        }

        protected void onPostExecute(Food food){

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(food.name != null) {

                AppDatabase db = AppDatabase.getInMemoryDatabase(getApplicationContext());
                db.foodDao().insertFood(food);
                GlobalDBUtils.insertFood(food, BarcodeRetrieval.this);

                showOutput(food);

            }else{
                //returnValues();
            }

        }

    }
}
