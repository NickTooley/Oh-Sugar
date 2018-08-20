package com.example.graysonorr.ohsugar;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

public class SearchReturn extends AppCompatActivity {

    private AppDatabase db;
    private ListView lv;
    private AutoCompleteTextView searchText;

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, db.foodDao().getAllNames());
        searchText = (AutoCompleteTextView)
                findViewById(R.id.searchText);

        searchText.setAdapter(adapter);

        searchText.measure(0,0);

        Shader textShader2=new LinearGradient(0, 0, searchText.getMeasuredWidth() / 2, 0,
                new int[]{Color.parseColor("#fc552e"),Color.parseColor("#f9398e")},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        searchText.getPaint().setShader(textShader2);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Search();
                return false;
            }
        });

        Button scanBtn = (Button) findViewById(R.id.scanBtn);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(SearchActivity.this, BarcodeScanner.class);
                Intent intent = new Intent(SearchReturn.this, BarcodeScanner.class);
                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            String currentQRCode;
            if (requestCode == 1) {
                currentQRCode = data.getStringExtra("nada");
                if (currentQRCode != null) {
                    fetchBarcodeData(currentQRCode);
                }
            } else if (requestCode == 2) {
                if (data.getStringExtra("Name") == null) {

                } else {
                    String name = data.getStringExtra("Name");
                    Double sugar = data.getDoubleExtra("Sugar", 1.0);

                    Intent intent = new Intent();
                    intent.putExtra("Name", name);
                    intent.putExtra("Sugar", sugar);
                    setResult(1, intent);
                    finish();
                }
            }
        }
    }





    private void Search(){

        lv.setAdapter(null);

        List<Food> searchResult = db.foodDao().searchByName("%"+searchText.getText().toString()+"%");

        FoodAdapterWClickListen adapter1 = new FoodAdapterWClickListen
                (this, searchResult);
        ListView lv = (ListView) findViewById(R.id.searchResults);
        lv.setAdapter(adapter1);

        HashMap<String, String> searchStrings = new HashMap<String, String>();

        for(int i=0; i < searchResult.size(); i++){
            searchStrings.put(searchResult.get(i).name, Double.toString(searchResult.get(i).sugar));
        }

        Log.d("get count", Integer.toString(adapter1.getCount()));
        int adaptCount = adapter1.getCount();
        if(adaptCount == 0) {
            fetchTextData(searchText.getText().toString());
        }else if(adaptCount > 0 && adaptCount < 10){
            int fillNum = 10 - adaptCount;
            //fetchTextData(searchText.getText().toString(), searchStrings, fillNum);
        }

        findViewById(R.id.searchResults).requestFocus();
        View view = SearchReturn.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }






    private void populateListView(List<Food> foods){
        final ListView lv = (ListView) findViewById(R.id.searchResults);
        FoodAdapter adapter = new FoodAdapter(this, foods);
        lv.setAdapter(adapter);

    }

    private void populateListView(final HashMap<String, String> foods){

        List<String> results = new ArrayList<String>(foods.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 , results);
        final ListView lv = (ListView) findViewById(R.id.searchResults);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = lv.getItemAtPosition(position).toString();
                String URL = foods.get(name);
                //Intent intent = new Intent(getApplicationContext(), BasicSugarContent.class);
                Intent intent = new Intent(SearchReturn.this, BasicSugarContentReturn.class);
                intent.putExtra("Name", name);
                intent.putExtra("URL", URL);
                Log.d("URL", URL);
                Log.d("Name", name);
                startActivityForResult(intent, 2);
            }
        });
    }

    private void populateListView(Food food){
        final ListView lv = (ListView) findViewById(R.id.searchResults);
        List<Food> foods = new ArrayList<Food>();

        foods.add(food);

        FoodAdapterWClickListen adapter = new FoodAdapterWClickListen(this, foods);
        lv.setAdapter(adapter);

    }



    private void fetchBarcodeData(String barcode) {
        // This activity is executing a query on the main thread, making the UI perform badly.
        Log.d("Barcode", barcode);
        Food foods = db.foodDao().findByBarcode(barcode);

        if(foods != null){
            Intent intent = new Intent();
            intent.putExtra("Name", foods.name);
            intent.putExtra("Sugar", foods.sugar);
            intent.putExtra("Barcode", foods.barcode);
            intent.putExtra("ID", foods.foodID);
            setResult(1, intent);
            finish();
            //populateListView(foods);
        }else{
            BarcodeAsyncScraper scraper = new BarcodeAsyncScraper(this, barcode);
            scraper.execute();
        }

    }

    private void fetchTextData(String search) {
        // This activity is executing a query on the main thread, making the UI perform badly.
        Log.d("Barcode", search);
        //Food food = db.foodDao().findByBarcode(barcode);
        List<Food> foods = db.foodDao().searchByName(search);

        if(foods.size() > 0){
            //showOutput(food);
            populateListView(foods);

        }else{
            AsyncScraper scraper = new AsyncScraper(this, search);
            scraper.execute();
        }

    }





    class AsyncScraper extends AsyncTask<String, Void, HashMap<String,String>> {
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
                        String productName = searchResults.get(i);
                        String productURL = searchResultsURL.get(i).attr("href");
                        searchResultMap.put(productName, productURL);
                        Log.d("product2", searchResults.get(i));
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
                populateListView(fetchedMap);
                //returnValues(fetchedMap);
            }else{
                //returnValues();
            }

        }

    }

    public class SearchResultArrayAdapter extends ArrayAdapter<Food> {
        public SearchResultArrayAdapter(Context context, int resource, List<Food> objects) {
            super(context, resource, objects);
        }

        public View getView(int position, View convertView, ViewGroup container) {
            LayoutInflater inflater = LayoutInflater.from(SearchReturn.this);
            View customView = inflater.inflate(R.layout.food_item, container, false);

            TextView resultTxtVw = (TextView) customView.findViewById(R.id.foodName);
            TextView sugarTxtVw = (TextView) customView.findViewById(R.id.foodSugar);
            Button addBtn = (Button) customView.findViewById(R.id.AddBtn);

            final Food currentItem = getItem(position);

            resultTxtVw.setText(currentItem.name);
            sugarTxtVw.setText(Double.toString(currentItem.sugar));
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SearchReturn.this, ShoppingListActivity.class);
                    startActivity(intent);
                }
            });

            return customView;
        }
    }

    public class CountdownScrapeArrayAdapter extends ArrayAdapter<String> {

        public CountdownScrapeArrayAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }

        public View getView(int position, View convertView, ViewGroup container) {
            LayoutInflater inflater = LayoutInflater.from(SearchReturn.this);
            View customView = inflater.inflate(R.layout.food_item, container, false);

            TextView resultTxtVw = (TextView) customView.findViewById(R.id.foodName);
            TextView sugarTxtVw = (TextView) customView.findViewById(R.id.foodSugar);
            Button addBtn = (Button) customView.findViewById(R.id.AddBtn);

            final String currentItem = getItem(position);

            resultTxtVw.setText(currentItem);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SearchReturn.this, ShoppingListActivity.class);
                    startActivity(intent);
                }
            });

            return customView;
        }

    }



    class BarcodeAsyncScraper extends AsyncTask<String, Void, ArrayList<String>> {
        HashMap<String, String> toReturn;
        private Context context;
        private String searchRequest;

        public BarcodeAsyncScraper(Context context, String search){
            this.context = context;
            this.searchRequest = search;
            searchRequest = search.replace(' ', '+');
        }

        protected ArrayList<String> doInBackground(String... search){

            ArrayList<String> searchResultMap = new ArrayList<String>();
            double sugarContent = 0;

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

                String productName = searchResults.get(0);
                String productURL = searchResultsURL.get(0).attr("href");
                searchResultMap.add(productName);
                searchResultMap.add(productURL);

                Document doc2 = Jsoup.connect("https://shop.countdown.co.nz"+productURL).get();

                Elements nutritional = doc2.select("td");

                for(Element nutritionals: nutritional){
                    if(nutritionals.html().equals("Sugars")){
                        String sugarOGString = nutritionals.nextElementSibling().html();
                        String sugarString = sugarOGString.substring(0, sugarOGString.length() - 1);
                        sugarContent = Double.parseDouble(sugarString);
                    }
                }
                searchResultMap.add(Double.toString(sugarContent));



            }catch(IOException e){

            }

            return searchResultMap;
        }

        protected void onPostExecute(ArrayList<String> fetchedMap){
            //CountdownScraper.returnValues();
            //toReturn = fetchedMap;
            if(fetchedMap != null) {
                //productNameText.setText(fetchedMap.get(0));
                //sugarContentText.setText(fetchedMap.get(2) + "g");

                Food food = new Food();
                food.sugar = Double.parseDouble(fetchedMap.get(2));
                food.name = fetchedMap.get(0);
                food.barcode = searchRequest;

                Intent intent = new Intent();
                intent.putExtra("Name", food.name);
                intent.putExtra("Sugar", food.sugar);
                intent.putExtra("Barcode", food.barcode);
                intent.putExtra("ID", food.foodID);
                setResult(1, intent);
                finish();

                //showOutput(food);

                //populateListView(fetchedMap);
                //returnValues(fetchedMap);
            }else{
                //returnValues();
            }

        }

    }


}
