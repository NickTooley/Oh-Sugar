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
        addFood(db, 3, "9300675036337", "Pumped Lime", 12.0);
        addFood(db, 4, "9300675036337", "Pumped Lime", 13.0);
        addFood(db, 5, "9300675036337", "Pumped Lime", 19.0);
        addFood(db, 6, "9300675036337", "Pumped Lime", 27.0);
        addFood(db, 7, "9300675036337", "Pumped Lime", 77.0);
        addFood(db, 8, "9300675036337", "Pumped Lime", 87.0);
        addFood(db, 9, "9300675036337", "Pumped Lime", 127.0);
        addFood(db, 10, "9300675036337", "Pumped Lime", 7.0);
        addFood(db, 11, "9300675036337", "Pumped Lime", 167.0);
        addFood(db, 12, "9414912300140", "Choysa Classic Tea Bags", 0.0);
        addFood(db, 13, "9414789100508", "Pepsi 355ml", 38.5);
    }

}


