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

import com.example.graysonorr.ohsugar.db.Food;
import com.example.graysonorr.ohsugar.db.utils.GlobalDBUtils;

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




    class AsyncScraper extends AsyncTask<String, Void, ArrayList<String>> {
        HashMap<String, String> toReturn;
        private Context context;
        private String searchRequest;
        ArrayList<String> allContent;

        public AsyncScraper(Context context, String search){
            this.context = context;
            this.searchRequest = search;
            searchRequest = search.replace(' ', '+');
        }

        protected ArrayList<String> doInBackground(String... search){

            ArrayList<String> allContent = new ArrayList<String>();
            ArrayList<Double> searchResultMap = new ArrayList<Double>();
            //HashMap<String, String> searchResultMap = new HashMap<String, String>();
            double sugarContent = 101;
            double sugarperhundred = 0;

            try{
                Document doc = Jsoup.connect("https://shop.countdown.co.nz"+searchRequest).get();
                Log.d("test", doc.title());

                Elements nutritional = doc.select("td");

                Elements headers = doc.select("th");

                int incrementCount = 0;

                for (Element headings : headers) {
                    if(headings.html().equals("Per 100g")){
                        break;
                    }else{
                        incrementCount++;
                    }
                }

                for(Element nutritionals: nutritional){
                    if(nutritionals.html().equals("Sugars")){
                        String sugarOGString = nutritionals.nextElementSibling().html();
                        String sugarString = sugarOGString.substring(0, sugarOGString.length() - 1);
                        sugarContent = Double.parseDouble(sugarString);

                        Element nextNutritional = nutritionals;
                        for(int i=0; i < incrementCount;i++){
                            nextNutritional = nextNutritional.nextElementSibling();
                        }

                        String sugarHundredOG = nextNutritional.html();
                        sugarHundredOG = sugarHundredOG.substring(0, sugarHundredOG.length() - 1);
                        Log.d("Double check", Integer.toString(incrementCount));
                        Log.d("Double check", sugarHundredOG);
                        sugarperhundred = Double.parseDouble(sugarHundredOG);
                    }

                }

                searchResultMap.add(sugarContent);
                searchResultMap.add(sugarperhundred);

                allContent.add(Double.toString(sugarContent));
                allContent.add(Double.toString(sugarperhundred));





                Elements barcode = doc.select(".product-image");

                for(Element barcodes: barcode){
                    String barcodeString = barcodes.attr("src");
                    Log.d("Full Barcode", barcodeString);
                    String pattern = "(?<=\\/Content\\/ProductImages\\/large\\/)(.*)(?=\\.jpg\\/)";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(barcodeString);

                    if (m.find( )) {
                        Log.d("Barcode", m.group(1) );

                        allContent.add(m.group(1));
                    }else {
                        Log.d("uhh", "NO MATCH");
                    }
                }






                //Double sugars = Double.parseDouble(sugarString);



            }catch(IOException e){

            }

            //return sugarContent;
            //return searchResultMap;
            return allContent;
        }

        protected void onPostExecute(ArrayList<String> allContent){

            if(Double.parseDouble(allContent.get(0)) == 101){

            }else{
                ArrayList<Double> fetchedSugar = new ArrayList<Double>();
                fetchedSugar.add(Double.parseDouble(allContent.get(0)));
                fetchedSugar.add(Double.parseDouble(allContent.get(1)));
                fillText(fetchedSugar);

                Food food = new Food();
                food.name = productName;
                if(allContent.size() == 3) {
                    food.barcode = allContent.get(2);
                }
                food.sugarServing = Double.parseDouble(allContent.get(0));
                food.sugar100 = Double.parseDouble(allContent.get(1));

                GlobalDBUtils.insertFood(food, BasicSugarContent.this);

            }

        }

    }


}
