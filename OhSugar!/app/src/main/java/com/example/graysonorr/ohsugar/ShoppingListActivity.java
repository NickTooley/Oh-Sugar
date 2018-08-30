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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShoppingListActivity extends AppCompatActivity {

    private AppDatabase db;
    private ArrayList<Food> shoppingList;
    SharedPreferences conversions;

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
        View view = getLayoutInflater().inflate(R.layout.shopping_list_v2, null);
        setContentView(view, new ViewGroup.LayoutParams(mScreenWidth, mScreenHeight));

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        Button save = (Button) findViewById(R.id.saveBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog();
            }
        });

        db = AppDatabase.getInMemoryDatabase(getApplicationContext());

        conversions = getSharedPreferences("conversions", Context.MODE_PRIVATE);

        TextView addItem = (TextView) findViewById(R.id.AddToListTxtVw);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShoppingListActivity.this, SearchReturn.class);
                startActivityForResult(intent, 1);
            }
        });

        updateActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String currentQRCode;
            String name;
            Double sugar;
            if (requestCode == 1) {
                name = data.getStringExtra("Name");
                sugar = data.getDoubleExtra("Sugar", 1.0);

                if (name != null) {
                    SharedPreferences sharedPreferences = getSharedPreferences("Shopping List", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    Gson gson = new Gson();
                    String json = sharedPreferences.getString("shopping list", null);
                    Type type = new TypeToken<ArrayList<Food>>() {
                    }.getType();

                    ArrayList<Food> shoppinglist = gson.fromJson(json, type);

                    if (shoppinglist == null) {
                        shoppinglist = new ArrayList<>();
                    }

                    Food item = new Food();

                    item.name = name;
                    item.sugarServing = sugar;

                    shoppinglist.add(item);

                    gson = new Gson();
                    json = gson.toJson(shoppinglist);
                    editor.putString("shopping list", json);
                    editor.commit();

                updateActivity();
            }


            }
        }
    }

    public class ShoppingListArrayAdapter extends ArrayAdapter<Food> {
        public ShoppingListArrayAdapter(Context context, int resource, List<Food> objects) {
            super(context, resource, objects);
        }

        public View getView(int position, View convertView, ViewGroup container) {
            LayoutInflater inflater = LayoutInflater.from(ShoppingListActivity.this);
            View customView = inflater.inflate(R.layout.food_item, container, false);

            TextView name = (TextView) customView.findViewById(R.id.foodName);
            TextView sugarV = (TextView) customView.findViewById(R.id.sugarValue);
            TextView sugarM = (TextView) customView.findViewById(R.id.sugarMeasurement);
            Button remove = (Button) customView.findViewById(R.id.AddBtn);

            final Food currentItem = getItem(position);

            name.setText(currentItem.name);
            sugarV.setText(String.format("%.2f", currentItem.sugarServing/conversions.getFloat("floatMeasure", 1)));
            sugarM.setText(conversions.getString("abbreviation", null));

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ShoppingListActivity.this, MoreInfoActivity.class);
                    intent.putExtra("ID", currentItem.foodID);
                    startActivity(intent);
                }
            });

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingListActivity.this);
                    builder.setTitle("Remove " + currentItem.name + " from your shopping list?");
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
                            removeFromShoppingList(currentItem);
                            updateActivity();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

            return customView;
        }
    }

    public ArrayList<Food> getShoppingList(){
        SharedPreferences sharedPreferences = getSharedPreferences("Shopping List", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("shopping list", null);
        Type type = new TypeToken<ArrayList<Food>>() {}.getType();

        ArrayList<Food> shoppinglist = gson.fromJson(json, type);

        if(shoppinglist == null){
            shoppinglist = new ArrayList<>();
        }

        return shoppinglist;
    }

    public void removeFromShoppingList(Food item){
        SharedPreferences sharedPreferences = getSharedPreferences("Shopping List", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = sharedPreferences.getString("shopping list", null);
        Type type = new TypeToken<ArrayList<Food>>() {}.getType();

        ArrayList<Food> shoppinglist = gson.fromJson(json, type);

        if(shoppinglist == null){
            shoppinglist = new ArrayList<>();
        }

        for(int i=0; i < shoppinglist.size(); i++){
            if (shoppinglist.get(i).foodID == item.foodID){
                shoppinglist.remove(i);
            }
        }

        gson = new Gson();
        json = gson.toJson(shoppinglist);
        editor.putString("shopping list", json);
        editor.commit();
    }

    public void updateActivity(){
        ShoppingListArrayAdapter adapter1 = new ShoppingListArrayAdapter
                (ShoppingListActivity.this, R.layout.food_item, getShoppingList());
        ListView lv = (ListView) findViewById(R.id.ListView);
        lv.setAdapter(adapter1);

        double totalSugar = 0.00;

        for(Food f : getShoppingList()){
            totalSugar += f.sugarServing/conversions.getFloat("floatMeasure", 1);
        }

        TextView units = (TextView) findViewById(R.id.unitsTxtVw);
        units.setText(String.format("%.2f ", totalSugar) + conversions.getString("stringMeasure", null));
    }

    public void ShowDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.save_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText listName = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Save your shopping list");
        dialogBuilder.setMessage("Name your shopping list: ");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Save(listName.getText().toString());
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void Save(String listName){
        SharedPreferences sharedPreferences = getSharedPreferences("Saved Lists", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Map<String,?> keys = sharedPreferences.getAll();

        boolean alreadyUsed = false;

        for(Map.Entry<String, ?> lists : keys.entrySet()){
            if(lists.getKey().toString().equals(listName)){
                alreadyUsed = true;
            }
        }

        if(!alreadyUsed){
            Gson gson = new Gson();
            String json = gson.toJson(getShoppingList());
            editor.putString(listName, json);
            editor.commit();
            Toast.makeText(ShoppingListActivity.this, "List saved successfully", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(ShoppingListActivity.this, "Sorry that name is already used for a saved list", Toast.LENGTH_SHORT).show();
        }
    }
}
