package com.example.graysonorr.ohsugar.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;


/**
 * Created by Connor on 10/09/2018.
 */

public class ShoppingList {

    String name;
    String timestamp;
    ArrayList<Food> list;
    double totalSugar;
    double recSugar;

    public ShoppingList(String name, String timestamp, ArrayList<Food> list, double totalSugar, double recSugar){
        this.name = name;
        this.timestamp = timestamp;
        this.list = list;
        this.totalSugar = totalSugar;
        this.recSugar = recSugar;
    }

    public void AddToList(Food item){
        list.add(item);
    }

    public String getName() {return name;}
    public void setName(String name){ this.name = name; }

    public String getTimestamp() {return timestamp;}

    public ArrayList<Food> getList() {return list;}
    public void setList(ArrayList<Food> list) { this.list = list; }

    public double getTotalSugar(Context context) {
        return totalSugar/getPrefs(context).getFloat("floatMeasure", 1);
    }
    public void setTotalSugar(Context context, double totalSugar) {
        this.totalSugar = totalSugar/getPrefs(context).getFloat("floatMeasure", 1);
    }

    public double getRecSugar(Context context) {
        return recSugar/getPrefs(context).getFloat("floatMeasure", 1);
    }
    public void setRecSugar(Context context, double recSugar) {
        this.recSugar = recSugar/getPrefs(context).getFloat("floatMeasure", 1);
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("conversions", Context.MODE_PRIVATE);
    }

    public String getConversionString(Context context){
        String conversionString = getPrefs(context).getString("stringMeasure", null);
        return conversionString;
    }
}
