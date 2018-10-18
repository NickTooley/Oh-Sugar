package com.example.graysonorr.ohsugar;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;
import com.example.graysonorr.ohsugar.db.ShoppingList;

import org.w3c.dom.Text;

import java.util.List;

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

        String category = food.category;
        if(food.sugar100 > 0) {
            List<Food> healthyAlternative = db.foodDao().searchHealthyAlt(category, food.sugar100);

            if(healthyAlternative.size() > 0) {
                final Food healthy = healthyAlternative.get(0);

                TextView healthyAlt = (TextView) findViewById(R.id.HealthyAltTxtVw);
                healthyAlt.setText(healthy.name);
                healthyAlt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int foodID = healthy.foodID;

                        Intent intent = new Intent(getApplicationContext(), MoreInfoActivity.class);
                        intent.putExtra("ID", foodID);
                        startActivity(intent);
                    }
                });
            }
        }

        TextView addToListBtn = (TextView) findViewById(R.id.AddToListTxtVw);
        addToListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int foodID = food.foodID;

                Intent intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
                intent.putExtra("ID", foodID);
                startActivity(intent);
            }
        });


        TextView foodProduct = (TextView) findViewById(R.id.FoodProductTxtVw);
        TextView sugarLevel = (TextView) findViewById(R.id.MsrmntTxtVw);

        foodProduct.setText(food.name);
        sugarLevel.setText(Double.toString(food.sugarServing));

        TextView backBtn = (TextView) findViewById(R.id.backTxtVw);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
