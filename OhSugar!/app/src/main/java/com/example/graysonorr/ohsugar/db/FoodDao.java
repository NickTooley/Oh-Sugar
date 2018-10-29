package com.example.graysonorr.ohsugar.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertFood(Food food);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFoodReplace(Food food);

    @Query("DELETE FROM Food")
    void deleteAll();

    @Query("SELECT name FROM Food")
    List<String> getAllNames();

    @Query("SELECT * FROM Food WHERE name LIKE :name")
    List<Food> searchByName(String name);

    @Query("SELECT * FROM Food WHERE category = :category AND sugar100 < :sugar")
    List<Food> searchHealthyAlt(String category, Double sugar);
}
