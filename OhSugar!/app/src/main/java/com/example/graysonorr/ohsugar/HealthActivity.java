package com.example.graysonorr.ohsugar;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class HealthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        SharedPreferences sharedPreferences = getSharedPreferences("Family", MODE_PRIVATE);

<<<<<<< HEAD
=======
        arrow = (ImageView) findViewById(R.id.arrow);
        float percentage = ((((float)193/(float)sharedPreferences.getInt("familySugar", 0))*100)/180)*100;

        rotateNeedle(0, Math.round(percentage));

>>>>>>> parent of 5c4bfda... Fixed removal from shopping list and worked on performance activity
        TextView recSugar = (TextView) findViewById(R.id.RecSugarTotal);
        TextView listSugar = (TextView) findViewById(R.id.ListSugarTotal);

        recSugar.setText("Rec Sugar: " + Integer.toString(sharedPreferences.getInt("familySugar", 0)) + "g");
        listSugar.setText(Float.toString(percentage));
<<<<<<< HEAD
=======
    }

    public void rotateNeedle(int startPoint, int endPoint){
        RotateAnimation rotateAnimation1 = new RotateAnimation(startPoint, endPoint,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation1.setInterpolator(new LinearInterpolator());
        rotateAnimation1.setDuration(1000);
        rotateAnimation1.setFillAfter(true);
        arrow.startAnimation(rotateAnimation1);
>>>>>>> parent of 5c4bfda... Fixed removal from shopping list and worked on performance activity
    }
}
