package com.example.graysonorr.ohsugar.db.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;

import java.util.Date;

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
    private static Food addFood(final AppDatabase db, final int id, final String barcode, final String name, final double sugar){
        Food food = new Food();

        food.foodID = id;
        food.barcode = barcode;
        food.name = name;
        food.sugar = sugar;

        db.foodDao().insertFood(food);
        return food;

    }

    private static void populateWithTestData(AppDatabase db) {
        db.foodDao().deleteAll();

        addFood(db, 1, "9415767422209", "Fresh Up Big Fizz Juicy Orange", 55.9);
        addFood(db, 2, "9300675036337", "Pumped Lime", 17.0);
    }

}


