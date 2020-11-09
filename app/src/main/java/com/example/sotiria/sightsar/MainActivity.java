package com.example.sotiria.sightsar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = "activity";
    private static final int REQUEST_CAMERA = 0;
    LinearLayout l1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        l1 = (LinearLayout) findViewById(R.id.home);
        l1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein));


        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your handler code here
                Log.i(TAG, "Show camera button pressed. Checking permission.");
                requestAppPermissions(REQUEST_CAMERA);
            }
        });



        final Button aboutus = (Button) findViewById(R.id.about);
        aboutus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              //  Uri uri = Uri.parse("https://www.dropbox.com/sh/8j4b2hiive2gk0a/AAD9cVYkOCdv6kZ3O-zSENbIa?dl=0");

                Intent intent2 = new Intent(MainActivity.this, Aboutus.class);
                startActivity(intent2);
            }
        });

        final Button information = (Button) findViewById(R.id.information);
        information.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               Uri uri = Uri.parse("https://www.dropbox.com/sh/dt1rskvbcm9yxer/AADOsbkYg6biaiUAQEciaQ1Wa?dl=0");
              Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void requestAppPermissions(int requestCode) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Camera permission has not been granted.
            Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Log.i(TAG,
                        "Displaying camera permission rationale to provide additional context.");
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                alertBuilder.setTitle("Camera permission necessary");
                alertBuilder.setMessage("SightsAR needs camera permission to scan real time objects.");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                REQUEST_CAMERA);
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();



            } else {
                // Camera permission has not been granted yet. Request it directly.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA);
            }

        } else {
            Log.i(TAG,
                    "CAMERA permission has already been granted. Displaying camera preview.");
            showCameraPreview();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showCameraPreview();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
    private void showCameraPreview(){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);

    }
}
