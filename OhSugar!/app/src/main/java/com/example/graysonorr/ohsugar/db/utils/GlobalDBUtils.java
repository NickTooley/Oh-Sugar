package com.example.graysonorr.ohsugar.db.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by toolnj1 on 27/08/2018.
 */

public abstract class GlobalDBUtils {

    public static List<Food> retrieveFoods(String date, Context context){

        List<Food> allFoods = new ArrayList<Food>();
        String newDate = "";

        try{
            Document doc = Jsoup.connect("http://kate.ict.op.ac.nz/~toolnj1/ohsugar/retrieveFoods.php?date=" + date).get();
            Elements foods = doc.select(".food");


            for (Element food : foods) {

                try {
                    Food item = new Food();

                    Document doc2 = Jsoup.parse(food.html());
                    Element name = doc2.selectFirst(".name");
                    item.name = name.html();
                    Element sugar = doc2.selectFirst(".sugar");
                    item.sugarServing = Double.parseDouble(sugar.html());
                    Element sugar100 = doc2.selectFirst(".sugar100");
                    item.sugar100 = Double.parseDouble(sugar100.html());
                    Element barcode = doc2.selectFirst(".barcode");
                    item.barcode = barcode.html();
                    Element category = doc2.selectFirst(".category");
                    item.category = category.html();

                    allFoods.add(item);

                    Log.d("item", item.name);


                }catch(Exception e){

                }




            }
            newDate = doc.selectFirst("#date").html();
            Log.d("New Date", newDate);

        }catch (Exception e){
            //Log.d("error", e.getMessage());
            return allFoods;
        }

        android.content.SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences("syncDate", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("syncDate", newDate);
        editor.apply();

        AppDatabase db = AppDatabase.getInMemoryDatabase(context);
        dbinit.populateAsync(db, allFoods);

        return allFoods;
    }



    public static List<Food> retrieveFoodsJSON(String date, Context context){
        String URL = "http://kate.ict.op.ac.nz/~toolnj1/ohsugar/retrieveJson.php?date=" + date;
        final List<Food> allFoods = new ArrayList<Food>();
        String newDate = "";
        android.content.SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences("syncDate", Context.MODE_PRIVATE);
        final android.content.SharedPreferences.Editor editor = sharedPref.edit();
        final Context contxt = context;


        try{

            JsonArrayRequest jsonFoodInsert = new JsonArrayRequest
                    (Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {


                        @Override
                        public void onResponse(JSONArray response) {
                           for (int i = 0; i < response.length(); i++){

                               try {
                                   JSONObject newObj = response.getJSONObject(i);
                                   if(i == response.length() - 1){
                                       String nuDate = newObj.getString("date");
                                       Log.d("New Sync", nuDate);
                                       editor.putString("syncDate", nuDate);
                                       editor.apply();
                                   }else {
                                       Food item = new Food();
                                       item.name = newObj.getString("name");
                                       item.sugarServing = newObj.getDouble("sugar");
                                       item.sugar100 = newObj.getDouble("sugar100");
                                       item.barcode = newObj.getString("barcode");
                                       item.category = newObj.getString("category");

                                       Log.d("new item", item.name);

                                       allFoods.add(item);
                                   }



                               }catch(JSONException e){
                                   Log.d("JSONERR", e.getMessage());
                               }
                            }

                            AppDatabase db = AppDatabase.getInMemoryDatabase(contxt);
                            dbinit.populateAsync(db, allFoods);


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("err", error.networkResponse.toString());
                        }

                    });
            Volley.newRequestQueue(context).add(jsonFoodInsert);

            Log.d("New Date", newDate);

        }catch (Exception e){
            //Log.d("error", e.getMessage());
            return allFoods;
        }



        return allFoods;
    }




    public static void insertFood(Food food, Context context){

        String insertURL = "http://kate.ict.op.ac.nz/~toolnj1/ohsugar/insertFoods.php";
        JSONObject FoodObjects;
        JSONArray JSONArr = null;
        JSONObject toPass = new JSONObject();

        //Fills JSON Object with text to be transliterated and puts it into JSONArray
        try {
            toPass.put("foodInsert", true);
            FoodObjects = new JSONObject();
            FoodObjects.put("name", food.name);
            FoodObjects.put("sugar1", food.sugarServing);
            FoodObjects.put("sugar2", food.sugar100);
            FoodObjects.put("barcode", food.barcode);
            FoodObjects.put("category", food.category);
            JSONArr = new JSONArray();
            JSONArr.put(FoodObjects);
            toPass.put("foods", JSONArr);

            Log.d("JSON Output", toPass.toString());

        } catch (Exception e) {

        }

        JsonObjectRequest jsonFoodInsert = new JsonObjectRequest
                (Request.Method.POST, insertURL, toPass, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                });
        Volley.newRequestQueue(context).add(jsonFoodInsert);


    }

}
