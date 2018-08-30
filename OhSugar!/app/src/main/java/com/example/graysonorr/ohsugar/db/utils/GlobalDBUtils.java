package com.example.graysonorr.ohsugar.db.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.graysonorr.ohsugar.db.Food;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

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

/**
 * Created by toolnj1 on 27/08/2018.
 */

public abstract class GlobalDBUtils {

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
