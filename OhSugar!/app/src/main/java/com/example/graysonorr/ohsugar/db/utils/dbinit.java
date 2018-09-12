package com.example.graysonorr.ohsugar.db.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;

import java.util.Date;
import java.util.List;

/**
 * Created by toolnj1 on 2/08/2018.
 */

public class dbinit {

    // Simulate a blocking operation delaying each Food insertion with a delay:
    private static final int DELAY_MILLIS = 500;


    public static void populateAsync(final AppDatabase db) {

        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    public static void populateAsync(final AppDatabase db, List<Food> foods){

        PopulateDbAsyncList task = new PopulateDbAsyncList(db, foods);
        task.execute();

    }

    private static class PopulateDbAsyncList extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;
        private final List<Food> foods;

        PopulateDbAsyncList(AppDatabase db, List<Food> foods) {
            mDb = db;
            this.foods = foods;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithList(mDb, foods);
            return null;
        }

    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(mDb);
            return null;
        }

    }
    private int barcode;
    private String name;
    private int sugar;
    private static Food addFood(final AppDatabase db,final String barcode, final String name, final double sugar){
        Food food = new Food();

        food.barcode = barcode;
        food.name = name;
        food.sugarServing = sugar;
        db.foodDao().insertFood(food);
        return food;
    }

    private static void populateWithList(AppDatabase db, List<Food> foods){

        for(Food food: foods){
            db.foodDao().insertFood(food);
        }

    }

    private static void populateWithTestData(AppDatabase db) {
//        db.foodDao().deleteAll();
//
//        addFood(db, "9415767422209", "Fresh Up Big Fizz Juicy Orange", 55.9);
//        addFood(db, "9300675036337", "Pumped Lime", 17.0);
//        addFood(db, "9414912300140", "Choysa Classic Tea Bags", 0.0);
//        addFood(db, "9414789100508", "Pepsi 355ml", 38.5);
//        addFood(db, "8886467103407", "Pringles Originals 134g", 0.1);
    }

}


