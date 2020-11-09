package com.example.sotiria.sightsar;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vuforia.DataSet;

import java.util.ArrayList;




public class AboutTarget extends AppCompatActivity {
    private static final String LOGTAG = AboutTarget.class.getSimpleName();


    CameraSession vuforiaAppSession;
    private boolean mSwitchDatasetAsap = false;
    private DataSet mCurrentDataset;
    private ArrayList<String> mDatasetStrings = new ArrayList<String>();
    private String mCurrentTargetName;
    private boolean mExtendedTracking = false;
    private ImageTargetRenderer mRenderer;
    private ImageTargetRenderer.OnTargetDetectListener mListener;
    private CameraActivity cameraActivity;
    TextView targetname;
    TextView targetdesc;
    TextView targetdesc2;
    String name;
    Handler mHandler = new Handler();
    private ProgressBar mProgressBar;
    private View mMenuContainer;
    Integer currentId;
    ImageView imageabout;
    public static final long SHOW_MENU_DELAY = 1500;
   // ArrayList<Integer> imageList = new ArrayList<Integer>();
    int[] ImageList = new int[]{0,R.drawable.kamara, R.drawable.agdimitrios, R.drawable.rotonta, R.drawable.megas_alexandros, R.drawable.omprelles, R.drawable.leukospurgos};

    Runnable mShowMenuRunnable = new Runnable() {
        @Override
        public void run() {
            mProgressBar.setVisibility(View.GONE);
            mMenuContainer.setVisibility(View.VISIBLE);
            mMenuContainer.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.downtoup));
            mHandler.removeCallbacks(mShowMenuRunnable);
        }
    };    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_target);
        mMenuContainer = findViewById(R.id.scrollView1);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mHandler.postDelayed(mShowMenuRunnable, SHOW_MENU_DELAY);
        targetname = (TextView) findViewById(R.id.subhead);
        targetdesc = (TextView) findViewById(R.id.description);
        targetdesc2 = (TextView) findViewById(R.id.description2);
        imageabout=(ImageView) findViewById(R.id.imageAbout);

        Intent in = getIntent();
        currentId= in.getIntExtra("paramId", 0);
        String tv3 = in.getExtras().getString("location3");
        String tv1 = in.getExtras().getString("location");
        String tv2 = in.getExtras().getString("location2");

        targetname.setText(tv3);
        targetdesc.setText(tv1);
        targetdesc2.setText(tv2);
        imageabout.setImageResource(ImageList[currentId]);
       }

    public void onMenuItemClick(View view) {
        int menuItemId = view.getId();
        switch (menuItemId) {
            case R.id.menu_back:
                  startActivity(new Intent(this, CameraActivity.class));
                 try {
                   vuforiaAppSession.resumeAR();
               } catch (CameraException e) {
                  e.printStackTrace();
                }

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
