package com.example.graysonorr.ohsugar;

import android.content.Intent;
import android.graphics.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

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



    }
}
