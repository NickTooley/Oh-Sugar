package com.example.graysonorr.ohsugar;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.graysonorr.ohsugar.db.Food;

import java.util.List;

/**
 * Created by toolnj1 on 10/08/2018.
 */

public class FoodAdapter extends ArrayAdapter<Food> {

   public FoodAdapter(Context context, List<Food> foods){
       super(context, 0 , foods);
   }

   @Override
    public View getView(int position, View convertView, ViewGroup parent) {

       Food food = getItem(position);

       if (convertView == null) {
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.food_item, parent, false);
       }

       TextView foodName = (TextView) convertView.findViewById(R.id.foodName);
       TextView foodSugar = (TextView) convertView.findViewById(R.id.foodSugar);

       //SharedPreferences sharedPref = FoodAdapter.this.getSharedPreferences("conversions", Context.MODE_PRIVATE);

       foodName.setText(food.name);
       foodSugar.setText(Double.toString(food.sugarServing) + "g of sugar");


       return convertView;
   }

}

