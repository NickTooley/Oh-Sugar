package com.example.graysonorr.ohsugar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
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

import com.example.graysonorr.ohsugar.db.Food;
import com.example.graysonorr.ohsugar.db.ShoppingList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class CreateShopList extends AppCompatActivity {

    ArrayList<Food> list;
    TextView totalSug;
    EditText sugGoal;

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
        View view = getLayoutInflater().inflate(R.layout.activity_create_shop_list, null);
        setContentView(view, new ViewGroup.LayoutParams(mScreenWidth, mScreenHeight));

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        list = new ArrayList<>();

        totalSug = (TextView) findViewById(R.id.totalSugar);
        totalSug.setText("Total sugar: 0.00");
        sugGoal = (EditText) findViewById(R.id.sugarGoal);
        SharedPreferences familyPreferences = getSharedPreferences("Family", MODE_PRIVATE);
        sugGoal.setText(Integer.toString(familyPreferences.getInt("familySugar", 0)));

        Button addBtn = (Button) findViewById(R.id.addBtn);
        Button saveBtn = (Button) findViewById(R.id.saveBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateShopList.this, SearchReturn.class);
                startActivityForResult(intent, 1);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog();
            }
        });
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
                    Food item = new Food();
                    item.name = name;
                    item.sugarServing = sugar;
                    list.add(item);

                    updateActivity();
                }
            }
        }
    }

    public void updateActivity() {
        ArrayAdapter adapter1 = new ArrayAdapter
                (CreateShopList.this, android.R.layout.simple_list_item_1, list);
        ListView lv = (ListView) findViewById(R.id.ListView);
        lv.setAdapter(adapter1);
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
            ShoppingList shoppingList = new ShoppingList(listName, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()), list, Double.parseDouble(totalSug.getText().toString()), Double.parseDouble(sugGoal.getText().toString()));
            Gson gson = new Gson();
            String json = gson.toJson(shoppingList);
            editor.putString(listName, json);
            editor.putString("current list", json);
            editor.commit();
            Toast.makeText(CreateShopList.this, "List saved successfully", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(CreateShopList.this, "Sorry that name is already used for a saved list", Toast.LENGTH_SHORT).show();
        }
    }
}
