package com.example.sotiria.sightsar;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class HistoryTargetActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String LOGTAG = HistoryTargetActivity.class.getSimpleName();
    ListView listYear;
    CameraSession vuforiaAppSession;
    Handler mHandler = new Handler();
    private ProgressBar mProgressBar;
    private LinearLayout mMenuContainer;
    private LinearLayout mYearContainer;
    public static final long SHOW_MENU_DELAY = 1500;
    DatabaseHelper myDb=null;
    XmlResourceParser parser2;
    Xml xml;
    TextView yeartarget;
    TextView yeartargetdesc;
    LinearLayout toolbar_year, toolbar_year_desc;
    public static Context context;

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
        setContentView(R.layout.activity_history_target);
        myDb= new DatabaseHelper(this);
        myDb.getWritableDatabase();
       // context = this;
       // xml=new Xml(context);
        //try {
           // xml.getXmlValues();
       // } catch (XmlPullParserException e) {
          //  e.printStackTrace();
        //} catch (IOException e) {
          //  e.printStackTrace();
        //}
        //myDb.insertYearData(xml.getid_year(),xml.getYear(), xml.getDescription());
        //myDb.updateYeardescValues(xml.getid_year(),xml.getYear(), xml.getDescription());

        mYearContainer= (LinearLayout) findViewById(R.id.historyview2);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        toolbar_year = (LinearLayout) findViewById(R.id.include_year);
        mMenuContainer = (LinearLayout) findViewById(R.id.historyview1);

        mHandler.postDelayed(mShowMenuRunnable, SHOW_MENU_DELAY);

        yeartarget = (TextView) findViewById(R.id.subhead_year);
        yeartargetdesc = (TextView) findViewById(R.id.yeardescr);


        Intent in = getIntent();
        ArrayList<String> list = in.getStringArrayListExtra("year");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.customlist, R.id.yearname, list);
        listYear.setAdapter(arrayAdapter);
        try {
            getXmlValues3();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String year="";
                String descyear="";
                //show new layout
                mMenuContainer.setVisibility(View.GONE);
                toolbar_year.setVisibility(View.GONE);
               mYearContainer.setVisibility(View.VISIBLE);
               toolbar_year_desc.setVisibility(View.VISIBLE);
                toolbar_year_desc.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein));
                mYearContainer.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.uptodown));

                Toast.makeText(getApplicationContext(), listYear.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                myDb.getWritableDatabase();
              Cursor cursor = myDb.getAllyearDesc(listYear.getItemAtPosition(position).toString());
                if(cursor != null){
                    cursor.moveToFirst();
                    Log.e(LOGTAG,"data show");

                    while (!cursor.isAfterLast()){
                        year = cursor.getString(cursor.getColumnIndex(myDb.YEARDESC_2));
                        descyear = cursor.getString(cursor.getColumnIndex(myDb.YEARDESC_3));
                        Log.e(LOGTAG,"Results->" + year + "" + descyear);
                        cursor.moveToNext();
                    } cursor.close();
                       myDb.close();
                }else{
                    Log.e(LOGTAG,"Null Data->");
                }
                yeartarget.setText(year);
               yeartargetdesc.setText(descyear);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
    }
    public void onMenuItemClick(View view) {
        int menuItemId = view.getId();
        switch (menuItemId) {
            case R.id.menu_back:
                startActivity(new Intent(this, CameraActivity.class));
                try {
                    vuforiaAppSession.resumeAR();
                }  catch (CameraException e) {
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
    private String getXmlValues3() throws XmlPullParserException, IOException {
        parser2 = getResources().getXml(R.xml.yeardesc);
        String name = null;
        Integer id_year=null;
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
                        if(name.equals("id")){
                            id_year = Integer.valueOf(readText(parser2));
                        }else if (name.equals("year")) {
                            year = readText(parser2);
                        } else if (name.equals("description")) {
                            description = readText(parser2);
                        }
                        Log.e(LOGTAG, "XML3:" +  year + "" + description);

                    }
                    myDb.insertYearData(id_year,year, description);
                    myDb.updateYeardescValues(id_year,year, description);
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
        return id_year + year + description;
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

}
