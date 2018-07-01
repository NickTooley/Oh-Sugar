package com.example.graysonorr.ohsugar;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.animation.*;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ImageView splashIconImgView = findViewById(R.id.img_view_splash_icon);
        Animation downAnimation = AnimationUtils.loadAnimation(this, R.anim.down);
        splashIconImgView.startAnimation(downAnimation);

        Thread timer = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2500);
                    Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                    super.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();
    }
}
