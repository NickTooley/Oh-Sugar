package com.example.graysonorr.ohsugar.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

/**
 * Created by toolnj1 on 1/08/2018.
 */

@Dao
public interface FoodDao {
    @Query("SELECT * FROM Food")
    List<Food> getAll();

    @Query("SELECT * FROM Food WHERE barcode = :barcode")
    Food findByBarcode(String barcode);

    @Query("SELECT * FROM Food WHERE foodID = :id")
    Food findByID(int id);

    @Insert(onConflict = IGNORE)
    void insertFood(Food food);

    @Query("DELETE FROM Food")
    void deleteAll();

}
