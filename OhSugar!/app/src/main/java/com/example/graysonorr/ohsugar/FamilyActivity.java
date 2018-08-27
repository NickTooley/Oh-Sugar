package com.example.graysonorr.ohsugar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

        final Spinner gSpinner = (Spinner) findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genderArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gSpinner.setAdapter(adapter);

        final Spinner aSpinner = (Spinner) findViewById(R.id.ageSpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.ageArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aSpinner.setAdapter(adapter2);

        TextView add = (TextView) findViewById(R.id.addBtn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMember(gSpinner.getSelectedItem().toString(), aSpinner.getSelectedItem().toString());
                updateActivity();
            }
        });

        updateActivity();
    }

    public void addMember(String gender, String age){
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
                    Intent intent = new Intent(getApplicationContext(), FamilyActivity.class);
                    startActivity(intent);
                }
            });


            gender.setText("Gender: " + currentItem.gender);
            age.setText("Age: " + currentItem.age);
            recSugar.setText("Recommended sugar: " + currentItem.recSugar);

            return customView;
        }
    }

    public void updateActivity(){
        FamilyArrayAdapter adapter3 = new FamilyArrayAdapter
                (FamilyActivity.this, R.layout.family_member, getFamily());
        ListView lv = (ListView) findViewById(R.id.lstVw);
        lv.setAdapter(adapter3);

        int totalRecsugar = 0 ;

        for(Person p : getFamily()){
            totalRecsugar += p.recSugar;
        }

        TextView recSugar = (TextView) findViewById(R.id.totalRecSugar);
        recSugar.setText("Recommended sugar for family: " + totalRecsugar + "g");
    }
}
