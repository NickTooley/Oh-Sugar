package com.example.graysonorr.ohsugar;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by toolnj1 on 1/08/2018.
 */

@Dao
public interface FoodDao {
    @Query("SELECT * FROM Food")
    List<Food> getAll();

    @Query("SELECT * FROM Food WHERE barcode = :barcode")
    Food findByBarcode(int barcode);

}
