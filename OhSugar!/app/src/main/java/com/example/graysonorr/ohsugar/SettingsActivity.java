package com.example.graysonorr.ohsugar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.*;
import android.support.v7.app.*;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.graysonorr.ohsugar.db.AppDatabase;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sharedPref = SettingsActivity.this.getSharedPreferences("conversions", Context.MODE_PRIVATE);

        Log.d("sharedPrefs", sharedPref.getString("stringMeasure", "Nothing here"));

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        TextView conversionsBtn = (TextView) findViewById(R.id.button);

        conversionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConversionsActivity.class);
                startActivity(intent);
            }
        });

        TextView loadShopList = (TextView) findViewById(R.id.button2);

        loadShopList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoadShoppingList.class);
                startActivity(intent);
            }
        });

        TextView familyBtn = (TextView) findViewById(R.id.button3);

        familyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FamilyActivity.class);
                startActivity(intent);
            }
        });

        Shader textShader=new LinearGradient(0, 0, conversionsBtn.getMeasuredWidth() / 2, 0,
                new int[]{Color.parseColor("#fc552e"),Color.parseColor("#f9398e")},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        familyBtn.getPaint().setShader(textShader);

        conversionsBtn.measure(0,0);

        Log.d("conversionswidth", Integer.toString(conversionsBtn.getMeasuredWidth()));

        Shader textShader2=new LinearGradient(0, 0, conversionsBtn.getMeasuredWidth() / 2, 0,
                new int[]{Color.parseColor("#fc552e"),Color.parseColor("#f9398e")},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        conversionsBtn.getPaint().setShader(textShader);

        TextView shoppingListBtn = (TextView) findViewById(R.id.button2);

        shoppingListBtn.measure(0,0);

        Shader textShader3=new LinearGradient(0, 0, shoppingListBtn.getMeasuredWidth() / 2, 0,
                new int[]{Color.parseColor("#fc552e"),Color.parseColor("#f9398e")},
                new float[]{0, 1}, Shader.TileMode.CLAMP);
        shoppingListBtn.getPaint().setShader(textShader2);

        TextView resetDB = (TextView) findViewById(R.id.resetDbBtn);

        resetDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDatabase db = AppDatabase.getInMemoryDatabase(getApplicationContext());
                db.foodDao().deleteAll();

                SharedPreferences sharedPref = getSharedPreferences("syncDate", Context.MODE_PRIVATE);
                android.content.SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("syncDate", "");
                editor.apply();

                Toast.makeText(getApplicationContext(), "DB Successfully deleted", Toast.LENGTH_LONG).show();

            }
        });

        resetDB.getPaint().setShader(textShader2);

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}


