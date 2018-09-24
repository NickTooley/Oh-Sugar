package com.example.graysonorr.ohsugar.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.graysonorr.ohsugar.db.Food;
import com.example.graysonorr.ohsugar.db.FoodDao;
import com.huma.room_for_asset.RoomAsset;


/**
 * Created by toolnj1 on 1/08/2018.
 */

@Database(entities = {Food.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract FoodDao foodDao();

    public static AppDatabase getInMemoryDatabase(Context context) {
        if (INSTANCE == null) {
//            INSTANCE =
//                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "maindb.db")
//                            // To simplify the codelab, allow queries on the main thread.
//                            // Don't do this on a real app! See PersistenceBasicSample for an example.
//                            .allowMainThreadQueries()
//                            .build();

//            INSTANCE =
//                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "Main Database")
//                            // To simplify the codelab, allow queries on the main thread.
//                            // Don't do this on a real app! See PersistenceBasicSample for an example.
//                            .build();

            INSTANCE = RoomAsset.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "maindb.db").allowMainThreadQueries().build();

        }
        return INSTANCE;
    }

}
