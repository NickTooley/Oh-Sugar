package com.example.graysonorr.ohsugar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;
import com.example.graysonorr.ohsugar.db.utils.CountdownScraper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private AppDatabase db;
    private ListView lv;
    private AutoCompleteTextView searchText;
    SharedPreferences conversions;

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
        conversions = getSharedPreferences("conversions", Context.MODE_PRIVATE);

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

        searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                Search();
            }
        });

        ImageView scanBtn = (ImageView) findViewById(R.id.scanBtn);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }

    private void Search(){

        lv.setAdapter(null);

        List<Food> searchResult = db.foodDao().searchByName("%"+searchText.getText().toString()+"%");

        SearchResultArrayAdapter adapter1 = new SearchResultArrayAdapter
                (SearchActivity.this, R.layout.food_item, db.foodDao().searchByName("%"+searchText.getText().toString()+"%"));
        ListView lv = (ListView) findViewById(R.id.searchResults);
        lv.setAdapter(adapter1);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Food food = (Food)parent.getItemAtPosition(position);
                int foodID = food.foodID;

                Intent intent = new Intent(getApplicationContext(), MoreInfoActivity.class);
                intent.putExtra("ID", foodID);
                startActivity(intent);

            }
        });

        HashMap<String, String> searchStrings = new HashMap<String, String>();

        for(int i=0; i < searchResult.size(); i++){
            searchStrings.put(searchResult.get(i).name, Double.toString(searchResult.get(i).getSugar100(this)));
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
        View view = SearchActivity.this.getCurrentFocus();
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
                Intent intent = new Intent(getApplicationContext(), BasicSugarContent.class);
                intent.putExtra("Name", name);
                intent.putExtra("URL", URL);
                Log.d("URL", URL);
                Log.d("Name", name);
                startActivity(intent);
            }
        });
    }

    private void populateListView(Food food){
        final ListView lv = (ListView) findViewById(R.id.searchResults);
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
//            populateListView(foods);
            int foodID = foods.foodID;

            Intent intent = new Intent(getApplicationContext(), MoreInfoActivity.class);
            intent.putExtra("ID", foodID);
            startActivity(intent);

        }else{
            Toast.makeText(this, "No items found with that barcode", Toast.LENGTH_LONG).show();
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
            HashMap<String, String> searchResultMap = CountdownScraper.retrieveFoodList(searchRequest);
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
            LayoutInflater inflater = LayoutInflater.from(SearchActivity.this);
            View customView = inflater.inflate(R.layout.food_item, container, false);

            TextView resultTxtVw = (TextView) customView.findViewById(R.id.foodName);
            TextView sugarV = (TextView) customView.findViewById(R.id.sugarValue);
            TextView sugarM = (TextView) customView.findViewById(R.id.sugarMeasurement);
            Button addBtn = (Button) customView.findViewById(R.id.AddBtn);

            final Food currentItem = getItem(position);

            resultTxtVw.setText(currentItem.name);
            if(currentItem.sugar100 >= 0) {
                sugarV.setText(String.format("%.2f", currentItem.sugar100 / conversions.getFloat("floatMeasure", 1)));
                sugarM.setText(conversions.getString("abbreviation", null));
            }else{
                sugarV.setText("No sugar data");
                sugarM.setText("");
            }
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int foodID = currentItem.foodID;

                    Intent intent = new Intent(SearchActivity.this, ShoppingListActivity.class);
                    intent.putExtra("ID", foodID);
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
            LayoutInflater inflater = LayoutInflater.from(SearchActivity.this);
            View customView = inflater.inflate(R.layout.food_item, container, false);

            TextView resultTxtVw = (TextView) customView.findViewById(R.id.foodName);
            TextView sugarTxtVw = (TextView) customView.findViewById(R.id.foodSugar);
            Button addBtn = (Button) customView.findViewById(R.id.AddBtn);

            final String currentItem = getItem(position);

            resultTxtVw.setText(currentItem);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //addToShoppingList(currentItem);
                    Intent intent = new Intent(SearchActivity.this, ShoppingListActivity.class);
                    startActivity(intent);
                }
            });
            resultTxtVw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SearchActivity.this, MoreInfoActivity.class);
                    //intent.putExtra("ID", currentItem.foodID);
                    startActivity(intent);
                }
            });

            return customView;
        }

    }

    public void addToShoppingList(Food item){
        SharedPreferences sharedPreferences = getSharedPreferences("Saved Lists", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = sharedPreferences.getString("current list", null);
        Type type = new TypeToken<ArrayList<Food>>() {}.getType();

        ArrayList<Food> shoppinglist = gson.fromJson(json, type);

        if(shoppinglist == null){
            shoppinglist = new ArrayList<>();
        }

        shoppinglist.add(item);

        gson = new Gson();
        json = gson.toJson(shoppinglist);
        editor.putString("current list", json);
        editor.commit();
    }


}


