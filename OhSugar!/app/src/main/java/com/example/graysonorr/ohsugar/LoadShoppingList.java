package com.example.graysonorr.ohsugar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Map;

public class LoadShoppingList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_shopping_list);

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        TextView create = (TextView) findViewById(R.id.createBtn);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoadShoppingList.this, CreateShopList.class);
                startActivity(intent);
                UpdateActivity();
            }
        });

        UpdateActivity();
    }

    public class ListsArrayAdapter extends ArrayAdapter<String> {
        public ListsArrayAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }

        public View getView(int position, View convertView, ViewGroup container) {
            LayoutInflater inflater = LayoutInflater.from(LoadShoppingList.this);
            View customView = inflater.inflate(R.layout.saved_lists, container, false);

            TextView name = (TextView) customView.findViewById(R.id.ListName);
            TextView sugar = (TextView) customView.findViewById(R.id.sugar);
            Button preview = (Button) customView.findViewById(R.id.PrevBtn);
            final Button delete = (Button) customView.findViewById(R.id.DelBtn);

            final String currentItem = getItem(position);

            name.setText(currentItem);
            sugar.setText("Sugar total: ---");

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoadShoppingList.this);
                    builder.setTitle("Load " + currentItem + " to your shopping list?");
                    builder.setMessage("Are you sure? This will replace your current shopping list.");
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LoadShoppingList(currentItem);
                            Intent intent = new Intent(LoadShoppingList.this, ShoppingListActivity.class);
                            startActivity(intent);
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoadShoppingList.this);
                    builder.setTitle("Delete " + currentItem + " from your saved lists?");
                    builder.setMessage("Are you sure?");
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Delete(currentItem);
                            UpdateActivity();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

            return customView;
        }
    }

    public ArrayList<String> GetShoppingLists(){
        SharedPreferences sharedPreferences = getSharedPreferences("Saved Lists", MODE_PRIVATE);

        Map<String,?> keys = sharedPreferences.getAll();
        ArrayList<String> savedLists = new ArrayList();

        for(Map.Entry<String, ?> lists : keys.entrySet()){
            if(!lists.getKey().toString().equals("current list")){
                savedLists.add(lists.getKey().toString());
            }
        }

        return savedLists;
    }

    public void LoadShoppingList(String listName){
        SharedPreferences sharedPreferences = getSharedPreferences("Saved Lists", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = sharedPreferences.getString(listName, null);
        Type type = new TypeToken<ShoppingList>() {
        }.getType();

        ShoppingList list = gson.fromJson(json, type);

        gson = new Gson();
        json = gson.toJson(list);
        editor.putString("current list", json);
        editor.commit();
    }

    public void Delete(String listName){
        SharedPreferences preferences = getSharedPreferences("Saved Lists", MODE_PRIVATE);
        preferences.edit().remove(listName).commit();
    }

    public void UpdateActivity(){
        ListsArrayAdapter adapter3 = new ListsArrayAdapter
                (LoadShoppingList.this, R.layout.saved_lists, GetShoppingLists());
        ListView lv = (ListView) findViewById(R.id.ListView);
        lv.setAdapter(adapter3);
    }

    /*public Double GetSugar(String listName){
        SharedPreferences sharedPreferences = getSharedPreferences("Saved Lists", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = sharedPreferences.getString(listName, null);
        Type type = new TypeToken<ArrayList<Food>>() {
        }.getType();

        ArrayList<Food> shoppingList = gson.fromJson(json, type);

        double totalSugar = 0.00;

        for(Food f : shoppingList){
            totalSugar += f.sugarServing;
        }

        return totalSugar;
    }*/
}
