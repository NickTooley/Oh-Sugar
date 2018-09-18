package com.example.graysonorr.ohsugar;

import android.app.ProgressDialog;
import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.*;
import android.widget.ImageView;

import com.example.graysonorr.ohsugar.db.AppDatabase;
import com.example.graysonorr.ohsugar.db.Food;
import com.example.graysonorr.ohsugar.db.utils.CountdownScraper;
import com.example.graysonorr.ohsugar.db.utils.GlobalDBUtils;
import com.example.graysonorr.ohsugar.db.utils.dbinit;

import java.util.HashMap;
import java.util.List;

import static com.example.graysonorr.ohsugar.db.utils.GlobalDBUtils.retrieveFoods;

public class SplashScreenActivity extends AppCompatActivity {

    public Boolean animFinished;
    public Boolean dbLoadFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        animFinished = false;
        dbLoadFinished = false;

        AppDatabase db = AppDatabase.getInMemoryDatabase(getApplicationContext());
        dbinit.populateAsync(db);

        AsyncScraper scraper = new AsyncScraper(this, db);
        scraper.execute();

        ImageView splashIconImgView = findViewById(R.id.img_view_splash_icon);
        Animation downAnimation = AnimationUtils.loadAnimation(this, R.anim.down);
        splashIconImgView.startAnimation(downAnimation);

        SharedPreferences sharedPref = SplashScreenActivity.this.getSharedPreferences("conversions", Context.MODE_PRIVATE);

        if(sharedPref.getString("abbreviation", "no") == "no"){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putFloat("floatMeasure", 1.0f);
            editor.putString("abbreviation", "g");
            editor.putString("stringMeasure", "Grams");
            editor.apply();
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            //w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }



        Thread timer = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2500);
                    if(dbLoadFinished){
                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        animFinished = true;
                    }
                    super.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();
    }

    class AsyncScraper extends AsyncTask<String, Void, List<Food>> {
        private Context context;
        private ProgressDialog dialog;
        private AppDatabase db;

        public AsyncScraper(Context context, AppDatabase db){
            this.context = context;
            dialog = new ProgressDialog(context);
            this.db = db;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Retrieving database information");
            //dialog.show();
        }


        protected List<Food> doInBackground(String... search) {
            SharedPreferences sharedPref = SplashScreenActivity.this.getSharedPreferences("syncDate", Context.MODE_PRIVATE);
            String date = sharedPref.getString("syncDate", "");
            List<Food> food = GlobalDBUtils.retrieveFoods(date, getApplicationContext());
            return food;
        }

        protected void onPostExecute(List<Food> food){

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(animFinished){
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
            finish();
            }else{
                dbLoadFinished = true;
            }

        }

    }
}
