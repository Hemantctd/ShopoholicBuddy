package com.appinventiv.codescanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.content.ContentValues.TAG;

/**
 * Created by appinventiv on 12/10/17.
 */

public class CodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    public static ApiListener mApiListener;
    private ZXingScannerView mScannerView;
    private boolean isLoading;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        View mainView = LayoutInflater.from(CodeScannerActivity.this).inflate(R.layout.view_main, null);
        FrameLayout rel_main = mainView.findViewById(R.id.fl_main);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        rel_main.addView(mScannerView);
        setContentView(mainView);                // Set the scanner view as the content view
        ((TextView)mainView.findViewById(R.id.tv_title)).setText(R.string.scan_qr_code);
        ((ImageView)mainView.findViewById(R.id.iv_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
        ((ImageView)mainView.findViewById(R.id.bt_flash)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mScannerView.getFlash())
                    mScannerView.setFlash(false);
                else
                    mScannerView.setFlash(true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(final Result rawResult) {
        // Do something with the result here
//        Toast.makeText(CodeScannerActivity.this, rawResult.getText() + "---" + rawResult.getBarcodeFormat().toString(),Toast.LENGTH_SHORT).show();
        if (!isLoading) {
            Log.v(TAG, rawResult.getText()); // Prints scan results
            Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
            mScannerView.resumeCameraPreview(this);
            if (rawResult.getText() != null) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (v != null) {
                    v.vibrate(500);
                }
                isLoading = true;
                if (mApiListener != null) {
                    mApiListener.hitSetStatusApi(rawResult.getText(), new ApiResponseListener() {
                        @Override
                        public void getResponse(int status) {
                            isLoading = false;
                            Intent intent = new Intent();
                            intent.putExtra("result", rawResult.getText());
                            intent.putExtra("format", rawResult.getBarcodeFormat().toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void getError() {
                            isLoading = false;
                            Toast.makeText(CodeScannerActivity.this, getString(R.string.unable_to_scan_code), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }

    }
}
