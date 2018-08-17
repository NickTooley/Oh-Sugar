package com.example.graysonorr.ohsugar.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by toolnj1 on 1/08/2018.
 */

@Entity
public class Food {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int foodID;

    public String barcode;
    public String name;
    public double sugar;

}
