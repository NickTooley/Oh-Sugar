package com.example.graysonorr.ohsugar;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by toolnj1 on 1/08/2018.
 */

@Database(entities = {Food.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FoodDao foodDao();

}
