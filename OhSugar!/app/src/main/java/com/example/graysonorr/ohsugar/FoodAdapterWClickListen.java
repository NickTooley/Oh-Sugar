package com.example.graysonorr.ohsugar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.graysonorr.ohsugar.db.Food;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by toolnj1 on 17/08/2018.
 */

public class FoodAdapterWClickListen extends ArrayAdapter<Food> {

    Context mContext;

    public FoodAdapterWClickListen(Context context, List<Food> foods){
        super(context, 0 , foods);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Food food = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.food_item, parent, false);
        }

        final View convertViewFnl = convertView;

        TextView foodName = (TextView) convertViewFnl.findViewById(R.id.foodName);
        TextView foodSugar = (TextView) convertViewFnl.findViewById(R.id.foodSugar);

        //SharedPreferences sharedPref = FoodAdapter.this.getSharedPreferences("conversions", Context.MODE_PRIVATE);

        foodName.setText(food.name);
        foodSugar.setText(Double.toString(food.sugarServing) + "g of sugar");

        Button btn = (Button) convertViewFnl.findViewById(R.id.AddBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("Name", food.name);
                intent.putExtra("Sugar", food.sugarServing);
                intent.putExtra("Barcode", food.barcode);
                intent.putExtra("ID", food.foodID);
                ((Activity)mContext).setResult(RESULT_OK, intent);
                ((Activity)mContext).finish();
            }
        });


        return convertView;
    }

}
