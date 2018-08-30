package com.example.graysonorr.ohsugar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.example.graysonorr.ohsugar.db.Food;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FamilyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        final Spinner gSpinner = (Spinner) findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genderArray, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        gSpinner.setAdapter(adapter);

        final Spinner aSpinner = (Spinner) findViewById(R.id.ageSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.ageArray, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        aSpinner.setAdapter(adapter2);

        TextView add = (TextView) findViewById(R.id.addBtn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMember(gSpinner.getSelectedItem().toString(), aSpinner.getSelectedItem().toString());
                UpdateActivity();
            }
        });

        UpdateActivity();
    }

    public void AddMember(String gender, String age){
        SharedPreferences sharedPreferences = getSharedPreferences("Family", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = sharedPreferences.getString("family", null);
        Type type = new TypeToken<ArrayList<Person>>() {}.getType();

        ArrayList<Person> family = gson.fromJson(json, type);

        if(family == null){
            family = new ArrayList<>();
        }

        family.add(new Person(gender, age));

        gson = new Gson();
        json = gson.toJson(family);
        editor.putString("family", json);
        editor.commit();
        editor.putInt("familySugar", GetRecommendedSugar());
        editor.commit();
    }

    public ArrayList<Person> getFamily(){
        SharedPreferences sharedPreferences = getSharedPreferences("Family", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("family", null);
        Type type = new TypeToken<ArrayList<Person>>() {}.getType();

        ArrayList<Person> family = gson.fromJson(json, type);

        if(family == null){
            family = new ArrayList<>();
        }

        return family;
    }

    public class FamilyArrayAdapter extends ArrayAdapter<Person> {
        public FamilyArrayAdapter(Context context, int resource, List<Person> objects) {
            super(context, resource, objects);
        }

        public View getView(int position, View convertView, ViewGroup container) {
            LayoutInflater inflater = LayoutInflater.from(FamilyActivity.this);
            View customView = inflater.inflate(R.layout.family_member, container, false);

            TextView gender = (TextView) customView.findViewById(R.id.genderTxtVw);
            TextView age = (TextView) customView.findViewById(R.id.ageTxtVw);
            TextView recSugar = (TextView) customView.findViewById(R.id.recSugTxtVw);
            Button remove = (Button)customView.findViewById(R.id.removeBtn);

            final Person currentItem = getItem(position);

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FamilyActivity.this);
                    builder.setTitle("Remove family member from family");
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
                            RemoveMember(currentItem);
                            UpdateActivity();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

            SharedPreferences conversions = getSharedPreferences("conversions", MODE_PRIVATE);
            gender.setText("Gender: " + currentItem.gender);
            age.setText("Age: " + currentItem.age);
            recSugar.setText("Recommended sugar: "
                                + currentItem.recSugar/conversions.getFloat("floatMeasure",0)
                                + conversions.getString("abbreviation", null));

            return customView;
        }
    }

    public void UpdateActivity(){
        SharedPreferences conversions = getSharedPreferences("conversions", MODE_PRIVATE);

        FamilyArrayAdapter adapter3 = new FamilyArrayAdapter
                (FamilyActivity.this, R.layout.family_member, getFamily());
        ListView lv = (ListView) findViewById(R.id.lstVw);
        lv.setAdapter(adapter3);

        TextView recSugar = (TextView) findViewById(R.id.totalRecSugar);
        TextView sugUnits = (TextView) findViewById(R.id.units);
        SharedPreferences sharedPreferences = getSharedPreferences("Family", MODE_PRIVATE);
        recSugar.setText(Float.toString(((sharedPreferences.getInt("familySugar", 0)))/conversions.getFloat("floatMeasure",0)));
        sugUnits.setText(conversions.getString("abbreviation", null));
    }

    public void RemoveMember(Person p){
        SharedPreferences sharedPreferences = getSharedPreferences("Family", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = sharedPreferences.getString("family", null);
        Type type = new TypeToken<ArrayList<Person>>() {}.getType();

        ArrayList<Person> family = gson.fromJson(json, type);

        if(family == null){
            family = new ArrayList<>();
        }

        for(int i=0; i < family.size(); i++){
            if (family.get(i).personID.equals(p.personID)){
                family.remove(i);
            }
        }

        gson = new Gson();
        json = gson.toJson(family);
        editor.putString("family", json);
        editor.commit();
        editor.putInt("familySugar", GetRecommendedSugar());
        editor.commit();
    }

    public Integer GetRecommendedSugar(){
        Integer sugar = 0;
        for(Person p : getFamily()){
            sugar += p.recSugar;
        }
        return sugar;
    }
}
