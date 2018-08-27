package com.example.graysonorr.ohsugar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.vision.*;
import com.google.android.gms.vision.barcode.*;

import java.io.*;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class BarcodeScanner extends AppCompatActivity {

    private Resources mResourceResolver;
    private BarcodeDetector mBarcodeDetector;
    private CameraSource mCameraSource;
    private SurfaceView mSurfaceView;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_barcode_scanner);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        mResourceResolver = getResources();

        mSurfaceView = (SurfaceView)findViewById(R.id.surface_view);
        SurfaceHolder surfHolder = mSurfaceView.getHolder();
        surfHolder.addCallback(new SurfaceHolderSetUp());
        BarcodeDetector.Builder bCodeDectBuilder = new BarcodeDetector.Builder(this);
        bCodeDectBuilder.setBarcodeFormats(Barcode.EAN_13 | Barcode.EAN_8);
        mBarcodeDetector = bCodeDectBuilder.build();
        mBarcodeDetector.setProcessor(new BarcodeProcessor());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        mCameraSource = new CameraSource
                .Builder(this, mBarcodeDetector)
                .setRequestedPreviewSize(height, width)
                .setAutoFocusEnabled(true)
                .build();

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }

    public class BarcodeProcessor implements Detector.Processor<Barcode> {
        @Override
        public void release() {
        }

        @Override
        public void receiveDetections(Detector.Detections<Barcode> detections) {
            SparseArray<Barcode> barcodes = detections.getDetectedItems();
            if (barcodes.size() != 0) {
                String message = barcodes.valueAt(0).displayValue;
                Intent intent = new Intent();
                intent.putExtra("nada", message);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }


    public class SurfaceHolderSetUp implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfHolder) {
            try {
                mCameraSource.start(surfHolder);
            } catch (IOException | SecurityException e) {
                Toast.makeText(BarcodeScanner.this, "You do not have Camera permissions enabled", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            mCameraSource.stop();
        }
    }
}
