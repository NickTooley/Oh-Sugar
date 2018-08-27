package com.example.graysonorr.ohsugar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CountdownScraper extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown_scraper);


        //AsyncScraper scraper = new AsyncScraper(this);

        //scraper.execute();


    }

    public void returnValues(HashMap<String, String> searchResults){
        Intent intent = new Intent();
        intent.putExtra("countdownMap", searchResults);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void returnValues(){
        Intent intent = new Intent();
        intent.putExtra("countdownMap", "nada");
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
    class AsyncScraper extends AsyncTask<String, Void, HashMap<String,String>>{
        HashMap<String, String> toReturn;
        private Context context;

        public AsyncScraper(Context context){
            this.context = context;
        }

        protected HashMap<String, String> doInBackground(String... search){

            HashMap<String, String> searchResultMap = new HashMap<String, String>();

            try{
                Document doc = Jsoup.connect("https://shop.countdown.co.nz/shop/searchproducts?search=pump").get();
                Log.d("test", doc.title());

                Elements newsHeadlines = doc.select(".gridProductStamp-name");
                List<String> searchResults = doc.select(".gridProductStamp-name").eachText();
                Elements searchResultsURL = doc.select(".gridProductStamp-imageLink");
                //List<String> searchResultsURL = doc.select("._jumpTop").eachText();


                for(Element URLs: searchResultsURL){
                    //   Log.d("URLs", URLs.attr("href"));
                }

                for (int i = 0; i < 10 || i > searchResults.size(); i++) {
                    String productName = searchResults.get(i).toString();
                    String productURL = searchResultsURL.get(i).attr("href");
                    searchResultMap.put(productName, productURL);
                    Log.d("product2", searchResults.get(i).toString());
                    Log.d("URL", searchResultsURL.get(i).attr("href"));
                }



            }catch(IOException e){

            }

            return searchResultMap;
        }

        protected void onPostExecute(HashMap<String, String> fetchedMap){
            //CountdownScraper.returnValues();
            toReturn = fetchedMap;
            if(fetchedMap != null) {
                returnValues(fetchedMap);
            }else{
                returnValues();
            }

        }

    }**/
}


