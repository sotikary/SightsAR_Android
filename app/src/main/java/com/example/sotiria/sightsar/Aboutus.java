package com.example.sotiria.sightsar;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

public class Aboutus extends AppCompatActivity {
    public static final long SHOW_MENU_DELAY = 1500;
    Handler mHandler = new Handler();
    private ProgressBar mProgressBar;
    private View mMenuContainer;

    Runnable mShowMenuRunnable = new Runnable() {
        @Override
        public void run() {
            mProgressBar.setVisibility(View.GONE);
            mMenuContainer.setVisibility(View.VISIBLE);
            mMenuContainer.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.downtoup));
            mHandler.removeCallbacks(mShowMenuRunnable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        mMenuContainer = findViewById(R.id.scrollView1);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mHandler.postDelayed(mShowMenuRunnable, SHOW_MENU_DELAY);
    }


    public void onMenuItemClick(View view) {
        int menuItemId = view.getId();
        switch (menuItemId) {
            case R.id.menu_vuforia:
                startActivity(new Intent(this, CameraActivity.class));
                break;
            case R.id.menu_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}
