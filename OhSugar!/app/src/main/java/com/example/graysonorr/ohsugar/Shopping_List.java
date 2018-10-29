package com.example.graysonorr.ohsugar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.graysonorr.ohsugar.db.Food;
import com.example.graysonorr.ohsugar.db.ShoppingList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Shopping_List extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);

        int mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
        View view = getLayoutInflater().inflate(R.layout.activity_shopping_list, null);
        setContentView(view, new ViewGroup.LayoutParams(mScreenWidth, mScreenHeight));

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        //TextView name = (TextView) findViewById(R.id.listName);
        TextView time = (TextView) findViewById(R.id.time);
        TextView totSug = (TextView) findViewById(R.id.totalSugar);
        TextView recSug = (TextView) findViewById(R.id.recSugar);
       // TextView difference = (TextView) findViewById(R.id.difference);

        Button menu = (Button) findViewById(R.id.menuBtn);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create dialog fragment with save, load, create options...
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("Saved Lists", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("current list", null);
        Type type = new TypeToken<ShoppingList>() {}.getType();

        ShoppingList list = gson.fromJson(json, type);

        if(list != null){
            //name.setText(list.getName());
            time.setText(list.getTimestamp());
            totSug.setText(Double.toString(list.getTotalSugar(this)));
            recSug.setText((Double.toString(list.getRecSugar(this))));
            //difference.setText((Double.toString(list.getRecSugar()-list.getTotalSugar())));

            ShoppingListArrayAdapter adapter1 = new ShoppingListArrayAdapter
                    (Shopping_List.this, R.layout.food_item, list.getList());
            ListView lv = (ListView) findViewById(R.id.ListView);
            lv.setAdapter(adapter1);
        }
        else{
            //name.setText("Name");
            time.setText("Timestamp");
            totSug.setText("Total");
            recSug.setText("Goal");
            //difference.setText("Difference");

            ArrayList<String> x =  new ArrayList<>();
            x.add("None");

            ArrayAdapter adapter1 = new ArrayAdapter
                    (Shopping_List.this, android.R.layout.simple_list_item_1, x);
            ListView lv = (ListView) findViewById(R.id.ListView);
            lv.setAdapter(adapter1);
        }
    }

    public class ShoppingListArrayAdapter extends ArrayAdapter<Food> {
        public ShoppingListArrayAdapter(Context context, int resource, List<Food> objects) {
            super(context, resource, objects);
        }

        public View getView(final int position, View convertView, ViewGroup container) {
            LayoutInflater inflater = LayoutInflater.from(Shopping_List.this);
            final View customView = inflater.inflate(R.layout.food_item, container, false);

            SharedPreferences conversions = getSharedPreferences("conversions",MODE_PRIVATE);

            TextView name = (TextView) customView.findViewById(R.id.foodName);
            TextView sugarV = (TextView) customView.findViewById(R.id.sugarValue);
            TextView sugarM = (TextView) customView.findViewById(R.id.sugarMeasurement);
            Button moreInfo = (Button) customView.findViewById(R.id.AddBtn);

            final Food currentItem = getItem(position);

            name.setText(currentItem.name);
            sugarV.setText(String.format("%.2f", currentItem.sugarServing/conversions.getFloat("floatMeasure", 1)));
            sugarM.setText(conversions.getString("abbreviation", null));

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            moreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Shopping_List.this, MoreInfoActivity.class);
                    intent.putExtra("ID", currentItem.foodID);
                    startActivity(intent);
                }
            });

            return customView;
        }
    }
}
