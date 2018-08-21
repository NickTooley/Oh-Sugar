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
        tvSugar.setText(Double.toString(sugar/sharedPref.getFloat("floatMeasure", 1.0f)) + " " + sharedPref.getString("abbreviation", "g"));
    }

    private void fillText(){
        TextView tvSugar = (TextView) findViewById(R.id.foodSugar);
        tvSugar.setText("Sugar Content Unavailable");
    }




    class AsyncScraper extends AsyncTask<String, Void, Double> {
        HashMap<String, String> toReturn;
        private Context context;
        private String searchRequest;

        public AsyncScraper(Context context, String search){
            this.context = context;
            this.searchRequest = search;
            searchRequest = search.replace(' ', '+');
        }

        protected Double doInBackground(String... search){

            HashMap<String, String> searchResultMap = new HashMap<String, String>();
            double sugarContent = 0;

            try{
                Document doc = Jsoup.connect("https://shop.countdown.co.nz"+searchRequest).get();
                Log.d("test", doc.title());

                Elements nutritional = doc.select("td");

                /**while(nutritional.next().html() != "Sugars"){
                 String sugarString = nutritional.next().html();
                 Log.d("test", sugarString);
                 }**/

                for(Element nutritionals: nutritional){
                    if(nutritionals.html().equals("Sugars")){
                        String sugarOGString = nutritionals.nextElementSibling().html();
                        String sugarString = sugarOGString.substring(0, sugarOGString.length() - 1);
                        sugarContent = Double.parseDouble(sugarString);
                    }

                }



                Elements barcode = doc.select(".product-image");

                for(Element barcodes: barcode){
                    String barcodeString = barcodes.attr("src");
                    Log.d("Full Barcode", barcodeString);
                    String pattern = "(?<=\\/Content\\/ProductImages\\/large\\/)(.*)(?=\\.jpg\\/)";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(barcodeString);

                    if (m.find( )) {
                        Log.d("Barcode", m.group(1) );
                    }else {
                        Log.d("uhh", "NO MATCH");
                    }
                }




                //Double sugars = Double.parseDouble(sugarString);



            }catch(IOException e){

            }

            return sugarContent;
        }

        protected void onPostExecute(final Double fetchedSugar){

            if(fetchedSugar == 0){

            }else{
                fillText(fetchedSugar);
            }

            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("Name", productName);
                    intent.putExtra("Sugar", fetchedSugar);
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
