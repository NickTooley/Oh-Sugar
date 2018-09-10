package com.example.graysonorr.ohsugar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        TextView name = (TextView) findViewById(R.id.listName);
        TextView time = (TextView) findViewById(R.id.time);
        TextView totSug = (TextView) findViewById(R.id.totalSugar);
        TextView recSug = (TextView) findViewById(R.id.recSugar);
        TextView difference = (TextView) findViewById(R.id.difference);

        Button edit = (Button) findViewById(R.id.editBtn);
        Button load = (Button) findViewById(R.id.loadBtn);
        Button newList = (Button) findViewById(R.id.newBtn);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Shopping_List.this, LoadShoppingList.class);
                startActivity(intent);
            }
        });

        newList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Shopping_List.this, CreateShopList.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("Saved Lists", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("current list", null);
        Type type = new TypeToken<ShoppingList>() {}.getType();

        ShoppingList list = gson.fromJson(json, type);

        if(list != null){
            name.setText(list.getName());
            time.setText(list.getTimestamp());
            totSug.setText(Double.toString(list.getTotalSugar()));
            recSug.setText((Double.toString(list.getRecSugar())));
            difference.setText((Double.toString(list.getRecSugar()-list.getTotalSugar())));

            ArrayAdapter adapter1 = new ArrayAdapter
                    (Shopping_List.this, android.R.layout.simple_list_item_1, list.getList());
            ListView lv = (ListView) findViewById(R.id.ListView);
            lv.setAdapter(adapter1);
        }
        else{
            name.setText("None");
            time.setText("None");
            totSug.setText("None");
            recSug.setText("None");
            difference.setText("None");

            ArrayList<String> x =  new ArrayList<>();
            x.add("None");

            ArrayAdapter adapter1 = new ArrayAdapter
                    (Shopping_List.this, android.R.layout.simple_list_item_1, x);
            ListView lv = (ListView) findViewById(R.id.ListView);
            lv.setAdapter(adapter1);

            edit.setEnabled(false);
        }
    }
}
