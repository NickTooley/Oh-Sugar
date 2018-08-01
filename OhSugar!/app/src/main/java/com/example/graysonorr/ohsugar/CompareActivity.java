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

import org.w3c.dom.Text;

public class CompareActivity extends AppCompatActivity {

    TextView compare1;
    TextView compare2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        TextView toolBarTitle = findViewById(R.id.toolbar_title);
        Typeface customFont = Typeface.createFromAsset(getAssets(), getString(R.string.font));
        toolBarTitle.setTypeface(customFont);

        compare1 = (TextView) findViewById(R.id.compare1);
        compare2 = (TextView) findViewById(R.id.compare2);

        compare1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompareActivity.this, BarcodeScanner.class);
                startActivityForResult(intent, 1);
            }
        });

        compare2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompareActivity.this, BarcodeScanner.class);
                startActivityForResult(intent, 2);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String currentQRCode;
        if (requestCode == 1) {
            currentQRCode = data.getStringExtra("nada");
            if(currentQRCode!=null) {
                compare1.setText(currentQRCode);
            }

        }

        if (requestCode == 2) {
            currentQRCode = data.getStringExtra("nada");
            if(currentQRCode!=null) {
                compare2.setText(currentQRCode);
            }
        }
    }


}
