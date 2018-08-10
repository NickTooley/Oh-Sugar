package com.example.graysonorr.ohsugar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private AppDatabase db;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        lv = (ListView) findViewById(R.id.searchResults);

        db = AppDatabase.getInMemoryDatabase(getApplicationContext());

        final EditText searchText = (EditText) findViewById(R.id.searchText);

        searchText.measure(0,0);

        Shader textShader2=new LinearGradient(0, 0, searchText.getMeasuredWidth() / 2, 0,
                new int[]{Color.parseColor("#fc552e"),Color.parseColor("#f9398e")},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        searchText.getPaint().setShader(textShader2);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                lv.setAdapter(null);

                fetchTextData(searchText.getText().toString());

                findViewById(R.id.searchResults).requestFocus();
                View view = SearchActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                return false;
            }
        });

        Button scanBtn = (Button) findViewById(R.id.scanBtn);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(SearchActivity.this, BarcodeScanner.class);
                Intent intent = new Intent(SearchActivity.this, BarcodeScanner.class);
                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String currentQRCode;
        if (requestCode == 1) {
            currentQRCode = data.getStringExtra("nada");
            if(currentQRCode!=null) {
                fetchBarcodeData(currentQRCode);
            }
        }

        if(requestCode == 2) {
            HashMap<String, String> searchResults = (HashMap<String,String>) data.getSerializableExtra("countdownMap");
            Log.d("Checkeromni", "heiojdf");

            populateListView(lv, searchResults);


        }
    }

    private void populateListView(ListView lv, List<Food> foods){
        FoodAdapter adapter = new FoodAdapter(this, foods);
        lv.setAdapter(adapter);

    }

    private void populateListView(ListView lv, HashMap<String, String> foods){

        List<String> results = new ArrayList<String>(foods.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , results);
        lv.setAdapter(adapter);

    }

    private void populateListView(ListView lv, Food food){
        List<Food> foods = new ArrayList<Food>();

        foods.add(food);

        FoodAdapter adapter = new FoodAdapter(this, foods);
        lv.setAdapter(adapter);

    }



    private void fetchBarcodeData(String barcode) {
        // This activity is executing a query on the main thread, making the UI perform badly.
        Log.d("Barcode", barcode);
        Food foods = db.foodDao().findByBarcode(barcode);

        if(foods != null){
            populateListView(lv, foods);
        }

    }

    private void fetchTextData(String search) {
        // This activity is executing a query on the main thread, making the UI perform badly.
        Log.d("Barcode", search);
        //Food food = db.foodDao().findByBarcode(barcode);
        List<Food> foods = db.foodDao().findByText(search);

        if(foods.size() > 0){
            //showOutput(food);
            populateListView(lv, foods);

        }else{
            AsyncScraper scraper = new AsyncScraper(SearchActivity.this, search);
            scraper.execute();
        }

    }





    class AsyncScraper extends AsyncTask<String, Void, HashMap<String,String>>{
        HashMap<String, String> toReturn;
        private Context context;
        private String searchRequest;

        public AsyncScraper(Context context, String search){
            this.context = context;
            this.searchRequest = search;
            searchRequest = search.replace(' ', '+');
        }

        protected HashMap<String, String> doInBackground(String... search){

            HashMap<String, String> searchResultMap = new HashMap<String, String>();

            try{
                Document doc = Jsoup.connect("https://shop.countdown.co.nz/shop/searchproducts?search="+searchRequest).get();
                Log.d("test", doc.title());

                Elements newsHeadlines = doc.select(".gridProductStamp-name");
                List<String> searchResults = doc.select(".gridProductStamp-name").eachText();
                Elements searchResultsURL = doc.select(".gridProductStamp-imageLink");
                //List<String> searchResultsURL = doc.select("._jumpTop").eachText();


                for(Element URLs: searchResultsURL){
                    //   Log.d("URLs", URLs.attr("href"));
                }

                if(searchResults.size() > 10) {
                    for (int i = 0; i < 10; i++) {
                        String productName = searchResults.get(i).toString();
                        String productURL = searchResultsURL.get(i).attr("href");
                        searchResultMap.put(productName, productURL);
                        Log.d("product2", searchResults.get(i).toString());
                        Log.d("URL", searchResultsURL.get(i).attr("href"));
                    }
                }else{
                    for (int i = 0; i < searchResults.size(); i++) {
                        String productName = searchResults.get(i).toString();
                        String productURL = searchResultsURL.get(i).attr("href");
                        searchResultMap.put(productName, productURL);
                        Log.d("product2", searchResults.get(i).toString());
                        Log.d("URL", searchResultsURL.get(i).attr("href"));
                    }
                }



            }catch(IOException e){

            }

            return searchResultMap;
        }

        protected void onPostExecute(HashMap<String, String> fetchedMap){
            //CountdownScraper.returnValues();
            toReturn = fetchedMap;
            if(fetchedMap != null) {
                populateListView(lv, fetchedMap);
                //returnValues(fetchedMap);
            }else{
                //returnValues();
            }

        }

    }


}


