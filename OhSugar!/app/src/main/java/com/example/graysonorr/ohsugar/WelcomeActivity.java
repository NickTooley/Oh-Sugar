package com.example.graysonorr.ohsugar;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.os.*;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.*;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;
import com.example.graysonorr.ohsugar.db.utils.GlobalDBUtils;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(WelcomeActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.INTERNET },
                        1);
            }

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "food-db").build();


        //Shader txtShader = new LinearGradient(0,0,100,0, new int[]{R.color.colorTomato, R.color.colorDeepPink}, new float[]{0,1}, Shader.TileMode.CLAMP);
        //toolBarTitle.getPaint().setShader(txtShader);

        // Code snippet to change notification bar color
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.colorLightSlateGray));
        }

        //Snippet end

        Button settingsBtn = (Button) findViewById(R.id.settingsBtn);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        Button shoppingListBtn = (Button) findViewById(R.id.listBtn);

        shoppingListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
                startActivity(intent);
            }
        });

        Button helpBtn = (Button) findViewById(R.id.helpBtn);

        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent);
            }
        });

        Button scanBtn = (Button) findViewById(R.id.cameraBtn);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BarcodeRetrieval.class);
                startActivity(intent);
            }
        });

        Button compareBtn = (Button) findViewById(R.id.compareBtn);

        compareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CompareActivity.class);
                startActivity(intent);
            }
        });

        Button searchBtn = (Button) findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        Button healthBtn = (Button) findViewById(R.id.healthBtn);

        healthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthActivity.class);
                startActivity(intent);
            }
        });

        Food food = new Food();
        food.name = "testName";
        food.barcode = "9327482393214";
        food.sugarServing = 14.2;

        //GlobalDBUtils.insertFood(food, WelcomeActivity.this);




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(WelcomeActivity.this,
                    "Permission Denied - External Storage",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
