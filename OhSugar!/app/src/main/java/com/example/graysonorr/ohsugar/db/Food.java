package com.example.graysonorr.ohsugar.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by toolnj1 on 1/08/2018.
 */

@Entity(indices = {@Index(value = {"name"},
        unique = true)})
public class Food {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int foodID;

    public String barcode;
    public String name;
    public double sugarServing;
    public double sugar100;
    public String category;
    public boolean onShopList;

    public double getSugar100(Context context){
        return this.sugar100/getPrefs(context).getFloat("floatMeasure", Context.MODE_PRIVATE);
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences("conversions", Context.MODE_PRIVATE);
    }
}


