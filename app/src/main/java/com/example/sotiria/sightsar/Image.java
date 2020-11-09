package com.example.sotiria.sightsar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class Image extends AppCompatActivity {
    CameraSession vuforiaAppSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        Picasso.with(this).load(getIntent().getStringExtra("image")).into(imageView);

    }

    public void onMenuItemClick(View view) {
        int menuItemId = view.getId();
        switch (menuItemId) {
            case R.id.menu_back:
                finish();
                    break;
            case R.id.menu_home:
                startActivity(new Intent(this, MainActivity.class));
                try {
                    vuforiaAppSession.stopAR();
                } catch (CameraException e) {
                    e.printStackTrace();
                }
                break;


        }
    }
}
