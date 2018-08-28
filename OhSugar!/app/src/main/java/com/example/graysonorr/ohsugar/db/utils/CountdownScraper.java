package com.example.graysonorr.ohsugar.db.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.graysonorr.ohsugar.BarcodeRetrieval;
import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by toolnj1 on 28/08/2018.
 */

public abstract class CountdownScraper {

    public static Food retrieveFoodDataBarcode(String searchRequest){

        Food food = new Food();
        food.name = null;
        food.sugarServing = 101;
        food.sugar100 = 101;
        food.barcode = searchRequest;
        food.category = null;

        ArrayList<String> searchResultMap = new ArrayList<String>();
        double sugarContent = 0;
        double sugarperhundred = 0;

        try {
            Connection.Response response = Jsoup.connect("https://shop.countdown.co.nz/shop/searchproducts?search=" + searchRequest).timeout(7000).execute();

            int statusCode = response.statusCode();

            if (statusCode != 200) {
                return null;
            } else {
                Document doc = Jsoup.connect("https://shop.countdown.co.nz/shop/searchproducts?search=" + searchRequest).get();
                Log.d("test", doc.title());

                Elements newsHeadlines = doc.select(".gridProductStamp-name");
                List<String> searchResults = doc.select(".gridProductStamp-name").eachText();
                Elements searchResultsURL = doc.select(".gridProductStamp-imageLink");


                for (Element URLs : searchResultsURL) {

                }

                String productName = searchResults.get(0);
                food.name = productName;
                String productURL = searchResultsURL.get(0).attr("href");
                searchResultMap.add(productName);
                searchResultMap.add(productURL);

                Food newFood = retrieveFoodDataURL(productURL, productName);
                food = newFood;
            }

        }catch(IOException e){
            return null;
        }

        return food;
    }



    public static Food retrieveFoodDataURL(String URL, String productName){

        Food food = new Food();
        food.name = productName;
        food.sugarServing = 101;
        food.sugar100 = 101;
        food.barcode = null;
        food.category = null;

        ArrayList<String> allContent = new ArrayList<String>();
        ArrayList<Double> searchResultMap = new ArrayList<Double>();
        //HashMap<String, String> searchResultMap = new HashMap<String, String>();
        double sugarContent = 101;
        double sugarperhundred = 0;

        try{
            Document doc = Jsoup.connect("https://shop.countdown.co.nz"+URL).get();
            Log.d("test", doc.title());

            Elements nutritional = doc.select("td");
            Elements headers = doc.select("th");
            Elements breadcrumbs = doc.select("span[itemprop='name']");

            ArrayList<String> crumbList = new ArrayList<String>();

            for(Element crumbs: breadcrumbs){
                crumbList.add(crumbs.html());
                Log.d("Crumbs", crumbs.html());
            }

            food.category = crumbList.get(crumbList.size()-1);

            int incrementCount = 0;
            for (Element headings : headers) {
                if(headings.html().equals("Per 100g") || headings.html().equals("Per 100ml")){
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

            food.sugarServing = sugarContent;
            food.sugar100 = sugarperhundred;
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
                    food.barcode = m.group(1);
                    allContent.add(m.group(1));
                }else {
                    Log.d("uhh", "NO MATCH");
                }
            }

        }catch(IOException e){
            return null;
        }



        return food;
    }


    public static HashMap<String, String> retrieveFoodList(String searchRequest) {


        HashMap<String, String> searchResultMap = new HashMap<String, String>();

        try {
            Document doc = Jsoup.connect("https://shop.countdown.co.nz/shop/searchproducts?search=" + searchRequest).get();
            Log.d("test", doc.title());

            Elements newsHeadlines = doc.select(".gridProductStamp-name");
            List<String> searchResults = doc.select(".gridProductStamp-name").eachText();
            Elements searchResultsURL = doc.select(".gridProductStamp-imageLink");
            //List<String> searchResultsURL = doc.select("._jumpTop").eachText();


            for (Element URLs : searchResultsURL) {
                //   Log.d("URLs", URLs.attr("href"));
            }

            if (searchResults.size() > 10) {
                for (int i = 0; i < 10; i++) {
                    String productName = searchResults.get(i).toString();
                    String productURL = searchResultsURL.get(i).attr("href");
                    searchResultMap.put(productName, productURL);
                    Log.d("product2", searchResults.get(i).toString());
                    Log.d("URL", searchResultsURL.get(i).attr("href"));
                }
            } else {
                for (int i = 0; i < searchResults.size(); i++) {
                    String productName = searchResults.get(i);
                    String productURL = searchResultsURL.get(i).attr("href");
                    searchResultMap.put(productName, productURL);
                    Log.d("product2", searchResults.get(i));
                    Log.d("URL", searchResultsURL.get(i).attr("href"));
                }
            }


        } catch (IOException e) {
            return null;
        }

        return searchResultMap;
    }

}
