package com.example.sotiria.sightsar;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.vuforia.CameraDevice;
import com.vuforia.DataSet;
import com.vuforia.HINT;
import com.vuforia.ObjectTracker;
import com.vuforia.STORAGE_TYPE;
import com.vuforia.State;
import com.vuforia.Trackable;
import com.vuforia.Tracker;
import com.vuforia.TrackerManager;
import com.vuforia.VIEW;
import com.vuforia.Vuforia;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Vector;


public class CameraActivity extends AppCompatActivity implements CameraControl, ImageTargetRenderer.OnTargetDetectListener, SampleAppMenuInterface {


    private static final String LOGTAG = CameraActivity.class.getSimpleName();
    CameraSession vuforiaAppSession;
    private boolean mSwitchDatasetAsap = false;
    private DataSet mCurrentDataset;
    private ArrayList<String> mDatasetStrings = new ArrayList<String>();
    private String mCurrentTargetName;
    private int mCurrentId=0;
    private boolean mExtendedTracking = false;
    private ProgressBar mLoadingDialog;
    private View mInfoBox;
    DatabaseHelper myDb=null;
    private Activity mActivity;
    private Dialog dialog;
    private Dialog history_dialog, history_dialog2;
    int[] ImageList = new int[]{R.drawable.kamara, R.drawable.agdimitrios, R.drawable.rotonta, R.drawable.megas_alexandros, R.drawable.omprelles, R.drawable.leukospurgos};

public String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/Targetsinformations";
    SQLiteDatabase db;
    private static final String ns = null;


    // The textures we will use for rendering:
    private Vector<Texture> mTextures;
    // Our OpenGL view:
    private CameraGLView mGlView;
    // Our renderer:
    private ImageTargetRenderer mRenderer;
    private GestureDetector mGestureDetector;
    private RelativeLayout mUILayout;
    LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
    private static final int REQUEST_CODE = 0;

    // Alert Dialog used to display SDK errors
    private AlertDialog mErrorDialog;
    boolean mIsDroidDevice = false;
    XmlResourceParser parser;
    XmlResourceParser parser2, parser3;
    Xml xml;
    Animation downtoup;
    int orientation;
    final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        Log.d(LOGTAG, "onCreate");
        startLoadingAnimation();

        vuforiaAppSession = new CameraSession(this);
        myDb= new DatabaseHelper(this);
      //  xml=new Xml(this);
        myDb.getWritableDatabase();

        mDatasetStrings.add("sights.xml");
         orientation = this.getResources().getConfiguration().orientation;
         //returns int
            vuforiaAppSession.initAR(this, orientation);

        // vuforiaAppSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mGestureDetector = new GestureDetector(this, new GestureListener());
        mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith(
                "droid");
        // Load any sample specific textures:
        mTextures = new Vector<Texture>();
         final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this,R.style.Dialog2);
             alertBuilder.setCancelable(true);
        alertBuilder.setIcon(android.R.drawable.ic_dialog_info);
        alertBuilder.setTitle("Scan object");
        alertBuilder.setMessage("..\nScan a statue from a short distance in order to see more historical information ..\n");

        alertBuilder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog alert = alertBuilder.create();
                alert.dismiss();
            }
        });

        AlertDialog alert = alertBuilder.create();
       alert.show();

        Window window = alert.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
    }

       private String getXmlValues () throws XmlPullParserException, IOException {
        parser = getResources().getXml(R.xml.sights);
        String name,key = null;
        Integer lid = null, hid=null, year=null;
        String lname = null, description1 = null, description2 = null;
        try {
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                name = parser.getName();
                if (name.equals("Landmark")) {
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        name = parser.getName();
                        if (name.equals("id")) {
                            lid = Integer.valueOf(readText(parser));
                        } else if (name.equals("key")) {
                            key = readText(parser);
                        }else if (name.equals("name")) {
                            lname = readText(parser);
                        } else if (name.equals("description")) {
                            description1 = readText(parser);
                        } else if (name.equals("description2")) {
                            description2 = readText(parser);
                        }
                    }
                    myDb.insertData(lid, key, lname, description1, description2);
                    myDb.updateXmlValues(lid,key, lname, description1, description2);
                }
            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e(LOGTAG, "XML NO SUCCESS");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOGTAG, "XML NO SUCCESS");

        } finally {
            if (myDb != null) {
                myDb.close();
                Log.e(LOGTAG, "XML CLOSE");
            }
        }
        return lid + lname + description1 + description2;
    }
    private String getXmlValues2() throws XmlPullParserException, IOException{
        parser2 = getResources().getXml(R.xml.history);
        String name, key = null;
        Integer hid=null ,  frkey=null ;
        String  year=null, description=null;

        try {
            while (parser2.next() != XmlPullParser.END_TAG) {
                if (parser2.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                name = parser2.getName();
                if (name.equals("Landmark")) {
                    while (parser2.next() != XmlPullParser.END_TAG) {
                        if (parser2.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        name = parser2.getName();
                        if (name.equals("id")) {
                            hid = Integer.valueOf(readText(parser2));
                        } else if (name.equals("key")) {
                            key = readText(parser2);
                        } else if (name.equals("year")) {
                            year = readText(parser2);
                        } else if (name.equals("fid")) {
                            frkey = Integer.valueOf(readText(parser2));
                        }
                        Log.e(LOGTAG, "XML2:" +  + hid + year + frkey);

                    }
                    myDb.insertHistoryData(hid, key, year, frkey);
                    myDb.updateXmlHistoryValues(hid, key, year, frkey);
                }
                Log.e(LOGTAG, "XML SUCCESS");
            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e(LOGTAG, "XML NO SUCCESS");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOGTAG, "XML NO SUCCESS");

        }
        finally {
         if (myDb != null) {
              myDb.close();
            Log.e(LOGTAG, "XML CLOSE");
        }
         }
        return hid + year + frkey;
    }


    private String getXmlValues3() throws XmlPullParserException, IOException{
        parser3 = getResources().getXml(R.xml.yeardesc);
        String name = null;
        Integer id=null;
        String  year=null, year_desc=null;

        try {
            while (parser3.next() != XmlPullParser.END_TAG) {
                if (parser3.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                name = parser3.getName();
                if (name.equals("Landmark")) {
                    while (parser3.next() != XmlPullParser.END_TAG) {
                        if (parser3.getEventType() != XmlPullParser.START_TAG) {
                            continue;
                        }
                        name = parser3.getName();
                        if (name.equals("id")) {
                            id = Integer.valueOf(readText(parser3));
                        } else if (name.equals("year")) {
                            year = readText(parser3);
                        } else if (name.equals("description")) {
                            year_desc = readText(parser3);
                        }
                        Log.e(LOGTAG, "XML3:" +id +year +year_desc);

                    }
                    myDb.insertYearData(id, year, year_desc);
                    myDb.updateYeardescValues(id, year, year_desc);
                }
                Log.e(LOGTAG, "XML SUCCESS");
            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e(LOGTAG, "XML NO SUCCESS");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOGTAG, "XML NO SUCCESS");

        }
        finally {
            if (myDb != null) {
                myDb.close();
                Log.e(LOGTAG, "XML CLOSE");
            }
        }
        return id + year + year_desc;
    }
    private String readText(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void startLoadingAnimation() {
        mUILayout = (RelativeLayout) View.inflate(this, R.layout.camera_overlay,
                null);
        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);
        mLoadingDialog = (ProgressBar) mUILayout.findViewById(R.id.loading_indicator);
        mInfoBox = mUILayout.findViewById(R.id.identify_menu);

        // Shows the loading indicator at start
        loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
        loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_INFOMENU);
        // Adds the inflated layout to the view
        addContentView(mUILayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

    }


    public void showHideLoading(boolean show) {
        if(mLoadingDialog != null) {
            int visibility = show ? View.VISIBLE : View.GONE;
            Log.e(String.valueOf(visibility), String.valueOf(visibility));
            if (visibility != mLoadingDialog.getVisibility()) {
                mLoadingDialog.setVisibility(visibility);
            }
        }
    }

    public void showHideInfoBox(boolean show) {
        int visibility;
        if (mInfoBox != null) {
             visibility = show ? View.VISIBLE : View.GONE;
            if (visibility != mInfoBox.getVisibility()) {
                mInfoBox.setVisibility(visibility);
            }
        }
    }


    // Process Single Tap event to trigger autofocus
    private class GestureListener extends
            GestureDetector.SimpleOnGestureListener {
        // Used to set autofocus one second after a manual focus is triggered
        private final Handler autofocusHandler = new Handler();

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // Generates a Handler to trigger autofocus
            // after 1 second
            autofocusHandler.postDelayed(new Runnable() {
                public void run() {
                    boolean result = CameraDevice.getInstance().setFocusMode(
                            CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);

                    if (!result)
                        Log.e("SingleTapUp", "Unable to trigger focus");
                }
            }, 1000L);

            return true;
        }
    }
    // Initializes AR application components.
    private void initApplicationAR() {
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();

        mGlView = new CameraGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);

        mRenderer = new ImageTargetRenderer(this, vuforiaAppSession);
        mRenderer.setTargetDetectListener(this);
        mGlView.setRenderer(mRenderer);
    }

    // To be called to initialize the trackers
    @Override
    public boolean doInitTrackers() {
        // Indicate if the trackers were initialized correctly
        boolean result = true;

        TrackerManager tManager = TrackerManager.getInstance();
        Tracker tracker;

        // Trying to initialize the image tracker
        tracker = tManager.initTracker(ObjectTracker.getClassType());
        if (tracker == null) {
            Log.e(LOGTAG, "Tracker not initialized. Tracker already initialized or the camera is already started");
            result = false;
        } else {
            Log.e(LOGTAG, "Tracker successfully initialized");
        }
        return result;
    }
    /**
     * Methods to load and destroy tracking data.
     */
    @Override
    public boolean doLoadTrackersData() {
        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
                .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;
        if (mCurrentDataset == null)
            mCurrentDataset = objectTracker.createDataSet();
        if (mCurrentDataset == null)
           return false;
        if (!mCurrentDataset.load("sights.xml",
                STORAGE_TYPE.STORAGE_APPRESOURCE)) {
            Log.e(LOGTAG, "Failed to load data set");
            return false;
        } else {
            Log.e(LOGTAG, "Success to load data set");
        }
        if (!objectTracker.activateDataSet(mCurrentDataset))
            return false;
        int numTrackables = mCurrentDataset.getNumTrackables();
        for (int count = 0; count < numTrackables; count++) {
            Trackable trackable = mCurrentDataset.getTrackable(count);
            if (isExtendedTrackingActive()) {
                trackable.startExtendedTracking();
            }
            String name = "Current Dataset : " + trackable.getName();
            trackable.setUserData(name);
           Log.e(LOGTAG, "UserData:Set the following user data "
                   + trackable.getUserData());
        }
      return true;
    }
    /**
     * @return boolean
     */
    boolean isExtendedTrackingActive() {
        return mExtendedTracking;
    }

    final public static int CMD_EXTENDED_TRACKING = 1;

    @Override
    public boolean doStartTrackers() {
        // Indicate if the trackers were started correctly
        Tracker objectTracker = TrackerManager.getInstance().getTracker(
                ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.start();

        return true;

    }

    @Override
    public boolean doStopTrackers() {
        // Indicate if the trackers were stopped correctly
        boolean result = true;
        Tracker objectTracker = TrackerManager.getInstance().getTracker(
                ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.stop();
        return true; }
    @Override
    public boolean doUnloadTrackersData() {
        // Indicate if the trackers were unloaded correctly
        boolean result = true;
        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
                .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;
        if (mCurrentDataset != null && mCurrentDataset.isActive()) {
            if (objectTracker.getActiveDataSet(0).equals(mCurrentDataset)
                    && !objectTracker.deactivateDataSet(mCurrentDataset)) {
                result = false;
            } else if (!objectTracker.destroyDataSet(mCurrentDataset)) {
                result = false;
            }
            mCurrentDataset = null;
        }
        return result;
    }
    @Override
    public boolean doDeinitTrackers() {
        // Indicate if the trackers were deinitialized correctly
        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ObjectTracker.getClassType());
        return true;  }

    @Override
    public void onInitARDone(CameraException exception) {
        if (exception == null) {
            initApplicationAR();

            mRenderer.setActive(true);

            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            addContentView(mGlView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            // Sets the UILayout to be drawn in front of the camera
            mUILayout.bringToFront();

            // Sets the layout background to transparent
            mUILayout.setBackgroundColor(Color.TRANSPARENT);

            try {
                vuforiaAppSession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);
            } catch (CameraException e) {
                Log.e(LOGTAG, e.getString());
            }

            boolean result = CameraDevice.getInstance().setFocusMode(
                    CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

            if (!result) {
                Log.e(LOGTAG, "Unable to enable continuous autofocus");
            }
            //else
            //   Log.e(LOGTAG, "Unable to enable continuous autofocus");

            // mSampleAppMenu = new SampleAppMenu(this, this, "Image Targets",
            //        mGlView, mUILayout, null);
            // setSampleAppMenuSettings();

        } else {
            Log.e(LOGTAG, exception.getString());
            showInitializationErrorMessage(exception.getString());
        }
    }

    // Shows initialization error messages as System dialogs
    public void showInitializationErrorMessage(String message) {
        final String errorMessage = message;
        runOnUiThread(new Runnable() {
            public void run() {
                if (mErrorDialog != null) {
                    mErrorDialog.dismiss();
                }

                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CameraActivity.this);
                builder
                        .setMessage(errorMessage)
                        .setTitle(getString(R.string.INIT_ERROR))
                        .setCancelable(false)
                        .setIcon(0)
                        .setPositiveButton(getString(R.string.button_OK),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });

                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }

    @Override
    public void onVuforiaUpdate(State state) {

        if (mSwitchDatasetAsap) {
            mSwitchDatasetAsap = false;
            TrackerManager tm = TrackerManager.getInstance();
            ObjectTracker ot = (ObjectTracker) tm.getTracker(ObjectTracker
                    .getClassType());
            if (ot == null || mCurrentDataset == null
                    || ot.getActiveDataSet(0) == null) {
                return;
            }

            try {
                vuforiaAppSession.resumeAR();
            }  catch (CameraException e) {
                e.printStackTrace();
            }
            doUnloadTrackersData();
            doLoadTrackersData();
           // showData(mCurrentId);
                }


    }
      @Override
    public void onTargetDetected(final String targetName) {
        if (!targetName.equalsIgnoreCase(mCurrentTargetName)) {
            mCurrentTargetName = targetName;
               }
         // loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.SHOW_INFOMENU);
            //Image Gallery Intent
          final Button galleryTarget = (Button) findViewById(R.id.gallery);
          galleryTarget.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  //loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_INFOMENU);

                 Intent i = new Intent(CameraActivity.this, GalleryTargets.class);
                i.putExtra("imageName", mCurrentTargetName);
                 startActivity(i);
              }
          });

          final int i=0;
          String containerData="";
          String nam="";
          String description="";
          String description2="";
          final String year="";
          TextView identifyText, identifyHeader;
          final ArrayList<String> years = new ArrayList<String>();
          myDb.getWritableDatabase();

          try {
              getXmlValues();
              getXmlValues2();
          } catch (XmlPullParserException e) {
              e.printStackTrace();
          } catch (IOException e) {
              e.printStackTrace();
          }

          Cursor cursor = myDb.getAlldata(targetName);
          if(cursor != null){
              cursor.moveToFirst();
              while (!cursor.isAfterLast()){
                  nam = cursor.getString(cursor.getColumnIndex(myDb.COL_2));
                  description = cursor.getString(cursor.getColumnIndex(myDb.COL_4));
                  description2 = cursor.getString(cursor.getColumnIndex(myDb.COL_5));
                  years.add(cursor.getString(cursor.getColumnIndex(myDb.HISTORY_2)));
                  Log.e(LOGTAG,"ShowData->" + nam + "" + description + "" + description2 +"" +years);
                  cursor.moveToNext();
              } cursor.close();
          }else{
              Log.e(LOGTAG,"Null Data->");   }
          loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.SHOW_INFOMENU);


          final Button aboutTarget = (Button) findViewById(R.id.about);
          final String finalDescription3 = nam;
          final String finalDescription = description;
          final String finalDescription1 = description2;


          identifyHeader=(TextView) findViewById(R.id.identify_header);
          identifyText=(TextView) findViewById(R.id.identify_menu_text);
          setText(identifyHeader, finalDescription3);
          setText(identifyText, finalDescription);
          aboutTarget.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  dialog = new Dialog(context);
                  dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;

                  dialog.setContentView(R.layout.activity_about_target);
                  final TextView targetname = (TextView) dialog.findViewById(R.id.subhead);
                  final TextView targetdesc = (TextView) dialog.findViewById(R.id.description);
                  final TextView targetdesc2 = (TextView) dialog.findViewById(R.id.description2);
                  ImageView imageabout=(ImageView) dialog.findViewById(R.id.imageAbout);
                  ImageView closedialog=(ImageView) dialog.findViewById(R.id.close_btn);
                  ImageView savebtn=(ImageView) dialog.findViewById(R.id.save_btn);
                  targetname.setText(finalDescription3);
                  targetdesc.setText(finalDescription);
                  targetdesc2.setText(finalDescription1);
                  switch (targetName) {
                      case "kamara":
                          imageabout.setImageResource(ImageList[0]);
                          break;
                      case "agdimitrios":
                          imageabout.setImageResource(ImageList[1]);
                          break;
                      case "rotonta":
                          imageabout.setImageResource(ImageList[2]);
                          break;
                      case "alexander":
                          imageabout.setImageResource(ImageList[3]);
                          break;
                      case "ompreles":
                          imageabout.setImageResource(ImageList[4]);
                          break;
                      case "whitetower":
                          imageabout.setImageResource(ImageList[5]);
                          break;
                      default:
                          throw new IllegalArgumentException("Invalid image " + targetName);
                  }
                 // imageabout.setImageResource(ImageList[mCurrentId]);
                  if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                      imageabout.getLayoutParams().height= RelativeLayout.LayoutParams.WRAP_CONTENT;
                  }
                  Window window = dialog.getWindow();
                  window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
                  dialog.setCancelable(true);
                  dialog.setCanceledOnTouchOutside(true);
                  File dir=new File(path);
                  dir.mkdir();

                  savebtn.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          try {
                              requestAppPermissions(REQUEST_CODE);

                              File myFile = new File(path+ "/" + targetname.getText()+ ".txt");
                              myFile.createNewFile();
                              FileOutputStream fOut = new FileOutputStream(myFile);
                              OutputStreamWriter myOutWriter =
                                      new OutputStreamWriter(fOut);
                              myOutWriter.append(targetname.getText()).append(System.getProperty("line.separator")).append(targetdesc.getText()).append(System.getProperty("line.separator")).append(System.getProperty("line.separator")).append(targetdesc2.getText());

                              myOutWriter.close();
                              fOut.close();
                              //display file saved message
                              Toast.makeText(getBaseContext(), "File saved successfully! Go to SDCARD into TargetInformation folder..",
                                      Toast.LENGTH_SHORT).show();
                          } catch (Exception e) {
                              e.printStackTrace();
                              Toast.makeText(getBaseContext(), e.getMessage(),
                                      Toast.LENGTH_LONG).show();
                          }

                      }

                  });
                  closedialog.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          dialog.dismiss();
                      }
                  });
                  dialog.show();
              }
          });

          final Button historyTarget = (Button) findViewById(R.id.history_information);
          final String[] listContent = years.toArray(new String[years.size()]);
          historyTarget.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  try {
                      // myDb.deleteXmlValues(mCurrentId);
                      getXmlValues3();
                  } catch (XmlPullParserException e) {
                      e.printStackTrace();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
                  //Intent i = new Intent(CameraActivity.this, HistoryTargetActivity.class);
                  //i.putExtra("year", years);
                  // startActivity(i);
                  history_dialog = new Dialog(CameraActivity.this);

                  //  history_dialog.getWindow().setBackgroundDrawableResource(R.color.rectangle);
                  history_dialog.setContentView(R.layout.list);
                  history_dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation2;

                  //history_dialog.setTitle("Choose a year: ");
                  history_dialog.setCancelable(true);
                  history_dialog.setCanceledOnTouchOutside(true);
                  final ListView dialog_ListView = (ListView)history_dialog.findViewById(R.id.list1);
                  ArrayAdapter<String> adapter = new ArrayAdapter <String>(CameraActivity.this,
                          R.layout.customlist, R.id.yearname, listContent);
                  dialog_ListView.setAdapter(adapter);
                  ImageView closedialog=(ImageView) history_dialog.findViewById(R.id.close_btn);
                  closedialog.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          history_dialog.dismiss();
                      }
                  });

                  Window window = history_dialog.getWindow();
                  window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                  dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                      @Override
                      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                          String year="";
                          String descyear="";
                          //show new layout
                          history_dialog2 = new Dialog(CameraActivity.this);
                          history_dialog2.setContentView(R.layout.activity_history_target);
                          Window window = history_dialog2.getWindow();
                          window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

                          myDb.getWritableDatabase();
                          Cursor cursor = myDb.getAllyearDesc(dialog_ListView.getItemAtPosition(position).toString());
                          if(cursor != null){
                              cursor.moveToFirst();
                              Log.e(LOGTAG,"data show");

                              while (!cursor.isAfterLast()){
                                  year = cursor.getString(cursor.getColumnIndex(myDb.YEARDESC_2));
                                  descyear = cursor.getString(cursor.getColumnIndex(myDb.YEARDESC_3));
                                  Log.e(LOGTAG,"Results->" + year + "" + descyear);
                                  cursor.moveToNext();
                              } cursor.close();
                              //  myDb.close();
                          }else{
                              Log.e(LOGTAG,"Null Data->"); }
                          final TextView yeartarget = (TextView) history_dialog2.findViewById(R.id.subhead_year);
                          final TextView yeartargetdesc = (TextView) history_dialog2.findViewById(R.id.yeardescr);

                          yeartarget.setText(year);
                          yeartargetdesc.setText(descyear);
                          ImageView dialog_back=(ImageView) history_dialog2.findViewById(R.id.menu_back);
                          ImageView save_history_btn=(ImageView) history_dialog2.findViewById(R.id.close_btn_history);


                          save_history_btn.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  try {
                                      requestAppPermissions(REQUEST_CODE);

                                      File myFile = new File(path+ "/" + yeartarget.getText()+ ".txt");
                                      myFile.createNewFile();
                                      FileOutputStream fOut = new FileOutputStream(myFile);
                                      OutputStreamWriter myOutWriter =
                                              new OutputStreamWriter(fOut);
                                      myOutWriter.append(yeartarget.getText()).append(System.getProperty("line.separator")).append(yeartargetdesc.getText());

                                      myOutWriter.close();
                                      fOut.close();
                                      //display file saved message
                                      Toast.makeText(getBaseContext(), "File saved successfully!",
                                              Toast.LENGTH_SHORT).show();
                                  } catch (Exception e) {
                                      e.printStackTrace();
                                      Toast.makeText(getBaseContext(), e.getMessage(),
                                              Toast.LENGTH_LONG).show();
                                  }

                              }

                          });


                          dialog_back.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  history_dialog2.dismiss();
                              }
                          });
                          history_dialog2.show();
                      }
                  });
                  history_dialog.show();
              }


          });


      }

    private void setText(final TextView text,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }
    @Override
    public void onTargetFound(int id) {


    }


   private void requestAppPermissions(int requestCode) {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                alertBuilder.setTitle("Write external storage permission necessary");
                alertBuilder.setMessage("SightsAR needs write permission permission in order to save text into a txt file..");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(CameraActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE);
                    }
                });

                AlertDialog alert = alertBuilder.create();
               alert.show();
           } else {
                // Write permission has not been granted yet. Request it.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE); }
        }else {
            Log.v(LOGTAG, "Permission is granted");
        }
    }
      @Override
    public void onTargetLose() {
       if (mCurrentTargetName != null) {
            mCurrentTargetName = null; }
        loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_INFOMENU);
          if(dialog!=null && dialog.isShowing()) {
          dialog.dismiss();

          }
          if(history_dialog!=null && history_dialog.isShowing()) {
              history_dialog.dismiss();  }

          }


    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        Log.e(LOGTAG, "Change config");
        try {
            vuforiaAppSession.resumeAR();
        }  catch (CameraException e) {
            e.printStackTrace();
        }
       vuforiaAppSession.onConfigurationChanged();
    }

    @Override
    protected void onPause() {
        Log.e(LOGTAG, "onpause");

        super.onPause();

        if (mGlView != null) {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }

        try {
            vuforiaAppSession.pauseAR();
        } catch (CameraException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {

        Log.e(LOGTAG, "onResume");
        super.onResume();

     //   showProgressIndicator(true);
        // This is needed for some Droid devices to force portrait
        if (mIsDroidDevice) {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        try {
            vuforiaAppSession.resumeAR();
        }  catch (CameraException e) {
            e.printStackTrace();
        }

        // Resume the GL view:
        if (mGlView != null) {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }


    }


    public void showProgressIndicator(boolean show)
    {
        if (loadingDialogHandler != null)
        {
            if (show)
            {
                loadingDialogHandler
                        .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
            }
            else
            {
                loadingDialogHandler
                        .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
            }
        }
    }

            @Override
    protected void onDestroy() {
                Log.e(LOGTAG, "onDestroy");
               super.onDestroy();

        try {
            vuforiaAppSession.stopAR();
        } catch (CameraException e) {
            Log.e(LOGTAG, e.getString());
        }
                // Unload texture:
                mTextures.clear();
                mTextures = null;
                System.gc();
    }

    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean menuProcess(int command) {

        boolean result = true;

        switch (command)
        {

            case CMD_EXTENDED_TRACKING:
                for (int tIdx = 0; tIdx < mCurrentDataset.getNumTrackables(); tIdx++)
                {
                    Trackable trackable = mCurrentDataset.getTrackable(tIdx);

                    if (!mExtendedTracking)
                    {
                        if (!trackable.startExtendedTracking())
                        {
                            Log.e(LOGTAG,
                                    "Failed to start extended tracking target");
                            result = false;
                        } else
                        {
                            Log.d(LOGTAG,
                                    "Successfully started extended tracking target");
                        }
                    } else
                    {
                        if (!trackable.stopExtendedTracking())
                        {
                            Log.e(LOGTAG,
                                    "Failed to stop extended tracking target");
                            result = false;
                        } else
                        {
                            Log.d(LOGTAG,
                                    "Successfully started extended tracking target");
                        }
                    }
                }

                if (result)
                    mExtendedTracking = !mExtendedTracking;

                break;
        }
        return result;}
}