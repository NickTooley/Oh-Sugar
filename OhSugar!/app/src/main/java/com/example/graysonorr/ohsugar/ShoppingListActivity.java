package com.example.graysonorr.ohsugar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;
import com.example.graysonorr.ohsugar.db.ShoppingList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShoppingListActivity extends AppCompatActivity {

    private AppDatabase db;
    private ArrayList<Food> shoppingList;
    SharedPreferences sharedPreferences;
    SharedPreferences conversions;
    SharedPreferences.Editor editor;
    Gson gson;

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

        Button menu = (Button) findViewById(R.id.menuBtn);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowMenuDialog();
            }
        });

        db = AppDatabase.getInMemoryDatabase(getApplicationContext());

        conversions = getSharedPreferences("conversions", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("Saved Lists", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();

        TextView addItem = (TextView) findViewById(R.id.AddToListTxtVw);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShoppingListActivity.this, SearchReturn.class);
                startActivityForResult(intent, 1);
            }
        });

        addItem.measure(0,0);

        android.graphics.Shader textShader=new android.graphics.LinearGradient(0, 0, addItem.getMeasuredWidth() / 2, 0,
                new int[]{android.graphics.Color.parseColor("#fc552e"),android.graphics.Color.parseColor("#f9398e")},
                new float[]{0, 1}, android.graphics.Shader.TileMode.CLAMP);
        addItem.getPaint().setShader(textShader);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Log.d("Build check", "hello");
                addItem.setBackground(ContextCompat.getDrawable(this, R.drawable.longbtn2));
                addItem.setPadding(0,25,0,0);
            }
        }
        if (getShoppingList() != null){
            UpdateActivity();
        }


        Food food;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = extras.getInt("ID");
            boolean remove = extras.getBoolean("remove", false);
            food = db.foodDao().findByID(value);

            if(remove){
                Log.d("bool", "made it here2");
                RemoveFromShoppingList(food);
                UpdateActivity();
            }else {
                //To be replaced with AddToList method on Connor's commit
                SharedPreferences sharedPreferences = getSharedPreferences("Saved Lists", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Gson gson = new Gson();
                String json = sharedPreferences.getString("current list", null);
                Type type = new TypeToken<ShoppingList>() {
                }.getType();

                ShoppingList list = gson.fromJson(json, type);

                list.AddToList(food);

                gson = new Gson();
                json = gson.toJson(list);
                editor.putString("current list", json);
                editor.commit();

                UpdateActivity();
            }
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        if(getShoppingList() != null) {
            UpdateActivity();
        }

        Intent intent = getIntent();
        if (intent.hasExtra("item")){
            Food food = db.foodDao().findByID(intent.getIntExtra("item", 0));
            AddToList(food);
        }

        if (getShoppingList() == null){
            ShowCreateDialog();
        }else{
            UpdateActivity();
        }
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
                String barcode = data.getStringExtra("Barcode");
                int id = data.getIntExtra("ID", 0);

                if (name != null) {
                    SharedPreferences sharedPreferences = getSharedPreferences("Saved Lists", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    Gson gson = new Gson();
                    String json = sharedPreferences.getString("current list", null);
                    Type type = new TypeToken<ShoppingList>() {
                    }.getType();

                    ShoppingList list = gson.fromJson(json, type);

                    //Food item = new Food();
                    Food item = db.foodDao().findByID(id);

                    item.name = name;
                    item.sugarServing = sugar;
                    AddToList(item);
                }
            }
        }else if(resultCode == 404){
            Toast.makeText(this, "Can not find an item with that barcode", Toast.LENGTH_LONG).show();
        }
    }

    public class ShoppingListArrayAdapter extends ArrayAdapter<Food> {
        public ShoppingListArrayAdapter(Context context, int resource, List<Food> objects) {
            super(context, resource, objects);
        }

        public View getView(int position, View convertView, ViewGroup container) {
            LayoutInflater inflater = LayoutInflater.from(ShoppingListActivity.this);
            View customView = inflater.inflate(R.layout.list_item, container, false);

            TextView name = (TextView) customView.findViewById(R.id.foodName);
            TextView sugarV = (TextView) customView.findViewById(R.id.sugarValue);
            TextView sugarM = (TextView) customView.findViewById(R.id.sugarMeasurement);
            Button remove = (Button) customView.findViewById(R.id.AddBtn);

            final Food currentItem = getItem(position);

            name.setText(currentItem.name);
            sugarV.setText(String.format("%.2f", currentItem.getSugar100(ShoppingListActivity.this)));
            sugarM.setText(conversions.getString("abbreviation", null));

//            name.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(ShoppingListActivity.this, MoreInfoActivity.class);
//                    intent.putExtra("ID", currentItem.foodID);
//                    startActivity(intent);
//                }
//            });

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
                            RemoveFromShoppingList(currentItem);
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

            return customView;
        }
    }

    public ShoppingList getShoppingList(){
        String json = sharedPreferences.getString("current list", null);
        Type type = new TypeToken<ShoppingList>() {}.getType();
        ShoppingList list = gson.fromJson(json, type);

        return list;
    }

    private void AddToList(Food item) {
        ShoppingList shoppingList = getShoppingList();
        shoppingList.AddToList(item);
        shoppingList.setTotalSugar(this, shoppingList.getTotalSugar(this) + item.getSugar100(this));
        CommitToList("current list", shoppingList);
    }

    public void RemoveFromShoppingList(Food item){
        ShoppingList shoppingList = getShoppingList();
        ArrayList<Food> list = shoppingList.getList();

        for(int i=0; i < list.size(); i++){
            if (list.get(i).foodID == item.foodID){
                list.remove(i);
            }
        }

        shoppingList.setList(list);
        shoppingList.setTotalSugar(this,shoppingList.getTotalSugar(this)-item.sugarServing);
        CommitToList("current list", shoppingList);
    }

    public void UpdateActivity(){
        ShoppingList list = getShoppingList();

        ShoppingListArrayAdapter adapter1 = new ShoppingListArrayAdapter
                (ShoppingListActivity.this, R.layout.list_item, list.getList());
        ListView lv = (ListView) findViewById(R.id.ListView);
        TextView emptyText = (TextView) findViewById(R.id.EmptyListView);
        lv.setEmptyView(emptyText);
        lv.setAdapter(adapter1);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Food food = (Food)parent.getItemAtPosition(position);
                int foodID = food.foodID;
                Log.d("foodID passing", Integer.toString(foodID));
                Log.d("foodID passing", food.name);

                Intent intent = new Intent(getApplicationContext(), MoreInfoActivity.class);
                intent.putExtra("ID", foodID);
                intent.putExtra("inList", true);
                startActivity(intent);

            }
        });

        TextView title = (TextView) findViewById(R.id.TitleTxtVw);
        title.setText("List name: " + list.getName());

        TextView goal = (TextView) findViewById(R.id.GoalTxtVw);
        goal.setText(String.format("Sugar goal: %.2f ", list.getRecSugar(this)) + list.getConversionString(this));

        TextView units = (TextView) findViewById(R.id.unitsTxtVw);
        units.setText(String.format("%.2f ", list.getTotalSugar(this)) + list.getConversionString(this));
    }

    public void ShowSaveDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Save " + getShoppingList().getName() + " to your shopping lists?");
        dialogBuilder.setMessage("This will overwrite previous saved lists of the same name.");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SaveList(getShoppingList().getName());
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

    public void ShowUpdateDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        ShoppingList list = getShoppingList();

        final EditText name = (EditText) dialogView.findViewById(R.id.edit1);
        name.setText(list.getName());
        final EditText goalLabel = (EditText) dialogView.findViewById(R.id.goalLabel);
        goalLabel.setText("Sugar goal ("+list.getConversionString(this)+")");
        final EditText goal = (EditText) dialogView.findViewById(R.id.edit2);
        goal.setText(Double.toString(list.getRecSugar(this)));

        dialogBuilder.setTitle("Update current list");

        dialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(name.getText().length()>0 && goal.getText().length()>0){
                    UpdateShoppingList(name.getText().toString(), Double.parseDouble(goal.getText().toString()));
                }
                else{
                    Toast.makeText(ShoppingListActivity.this, "Please enter both a name and a sugar goal.", Toast.LENGTH_LONG).show();
                }

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

    public void UpdateShoppingList(String name, Double goal){
        ShoppingList shoppingList = getShoppingList();
        shoppingList.setName(name);
        shoppingList.setRecSugar(this, goal);
        CommitToList("current list", shoppingList);
    }

    public void ShowMenuDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.menu_dialog, null);
        dialogBuilder.setView(dialogView);

        final TextView updateGoal = (TextView) dialogView.findViewById(R.id.updateGoalBtn);
        final TextView save = (TextView) dialogView.findViewById(R.id.saveBtn);
        final TextView create = (TextView) dialogView.findViewById(R.id.createBtn);
        final TextView load = (TextView) dialogView.findViewById(R.id.loadBtn);

        //dialogBuilder.setTitle("Menu");

        updateGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowUpdateDialog();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getShoppingList().getList().size() > 0){
                    ShowSaveDialog();
                }
                else{
                    Toast.makeText(ShoppingListActivity.this, "You can not save an empty list", Toast.LENGTH_LONG).show();
                }
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowCreateDialog();
            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShoppingListActivity.this, LoadShoppingList.class);
                startActivity(intent);
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void SaveList(final String listName){
        CommitToList(listName, getShoppingList());
        Toast.makeText(ShoppingListActivity.this, "List saved successfully", Toast.LENGTH_SHORT).show();
    }

    public void ShowCreateDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.create_list_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText name = (EditText) dialogView.findViewById(R.id.name);
        final EditText sugarGoal = (EditText) dialogView.findViewById(R.id.sugarGoal);

        dialogBuilder.setTitle("Create New");


        dialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                if (getShoppingList() == null) {
                    if(name.getText().length() != 0 && sugarGoal.getText().length() != 0) {
                        Create(name.getText().toString(), sugarGoal.getText().toString());
                    }else if (name.getText().length() == 0 && sugarGoal.getText().length() > 0){
                        Create("Default list", sugarGoal.getText().toString());
                    }else if(name.getText().length() > 0 && sugarGoal.getText().length() == 0){
                        Create(name.getText().toString(), "100");
                    }else{
                        Create("Default list", "100");
                    }
                    UpdateActivity();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingListActivity.this);
                    builder.setTitle("Create new shopping list");
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
                            if(name.getText().length() != 0 && sugarGoal.getText().length() != 0) {
                                Create(name.getText().toString(), sugarGoal.getText().toString());
                            }else if (name.getText().length() == 0 && sugarGoal.getText().length() > 0){
                                Create("Default list", sugarGoal.getText().toString());
                            }else if(name.getText().length() > 0 && sugarGoal.getText().length() == 0){
                                Create(name.getText().toString(), "100");
                            }else{
                                Create("Default list", "100");
                            }
                            UpdateActivity();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        final AlertDialog alert = dialogBuilder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if(getShoppingList() == null){
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
                    alert.setCancelable(false);
                }
            }
        });

        alert.show();
    }

    public void Create(String name, String sugarGoal){
        // Add current list to health activity shared prefs
        AddHealthEntry();

        ShoppingList list = new ShoppingList(name, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()), new ArrayList<Food>(), 0, Double.parseDouble(sugarGoal));
        CommitToList("current list", list);
    }

    public void CommitToList(String key, ShoppingList item){
        String json = gson.toJson(item);
        editor.putString(key, json);
        editor.commit();
        UpdateActivity();
    }

    public void AddHealthEntry(){
        SharedPreferences sp = getSharedPreferences("Health Entries", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String json = sp.getString("entries", null);
        Type type = new TypeToken<List<ShoppingList>>() {}.getType();
        List<ShoppingList> list = gson.fromJson(json, type);

        if(list==null){
            list = new ArrayList<>();
        }

        list.add(getShoppingList());

        System.out.println(list.size());

        json = gson.toJson(list);
        editor.putString("entries", json);
        editor.commit();
    }
}
