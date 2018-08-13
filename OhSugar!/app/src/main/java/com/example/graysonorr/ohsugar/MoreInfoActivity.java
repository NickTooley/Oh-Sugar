package com.example.graysonorr.ohsugar;

import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;

public class MoreInfoActivity extends AppCompatActivity {

    private Food food;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        db = AppDatabase.getInMemoryDatabase(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = extras.getInt("ID");
            food = db.foodDao().findByID(value);
        }

        TextView foodProduct = (TextView) findViewById(R.id.FoodProductTxtVw);
        TextView sugarLevel = (TextView) findViewById(R.id.MsrmntTxtVw);

        foodProduct.setText(food.name);
        sugarLevel.setText(Double.toString(food.sugar));

    }
}
