package com.example.graysonorr.ohsugar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ConversionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversions);

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        Button btn = (Button) findViewById(R.id.saveBtn);

        final float SUGARCUBES = 4.0f;
        final float TEASPOONS = 4.0f;
        final float GRAMS = 1.0f;
        final float TABLESPOONS = 12.5f;


        final SharedPreferences sharedPref = ConversionsActivity.this.getSharedPreferences("conversions", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        String current = sharedPref.getString("stringMeasure", "Grams");
        RadioButton rb1 = (RadioButton) findViewById(R.id.radioButton);
        RadioButton rb2 = (RadioButton) findViewById(R.id.radioButton2);
        RadioButton rb3 = (RadioButton) findViewById(R.id.radioButton3);
        RadioButton rb4 = (RadioButton) findViewById(R.id.radioButton4);

        Log.d("selected", current);

        RadioButton[] rbArr = {rb1, rb2, rb3, rb4};

        for(int i= 0; i < rbArr.length; i++){

            if(current.equals(rbArr[i].getText().toString())){
                rbArr[i].setChecked(true);
            }

        }

                btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup rg = (RadioGroup) findViewById(R.id.conversionSelect);
                RadioButton rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());

                String selected = rb.getText().toString();

                switch(selected){
                    case "Sugar Cubes":
                        editor.putFloat("floatMeasure", SUGARCUBES);
                        editor.putString("abbreviation", "cubes");
                        break;
                    case "Teaspoons":
                        editor.putFloat("floatMeasure", TEASPOONS);
                        editor.putString("abbreviation", "ts");
                        break;
                    case "Grams":
                        editor.putFloat("floatMeasure", GRAMS);
                        editor.putString("abbreviation", "g");
                        break;
                    case "Tablespoons":
                        editor.putFloat("floatMeasure", TABLESPOONS);
                        editor.putString("abbreviation", "Tbls");
                        break;
                }
                editor.putString("stringMeasure", selected);
                editor.apply();
                Log.d("sharedPrefs", sharedPref.getString("stringMeasure", "Cannot find"));
            }
        });
    }
}
