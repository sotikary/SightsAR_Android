package com.example.sotiria.sightsar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.util.Base64;
import android.util.EventLogTags;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GalleryTargets extends Activity {
    private static final String LOGTAG = GalleryTargets.class.getSimpleName();
    private static final int REQUEST_NETWORK = 0;


    private static final String newurl = "https://augmentedandroidapp.000webhostapp.com/index.php";
    ListView listView;
    Customadapter customadapter;
    CameraSession vuforiaAppSession;

    public static String[] Image_Url;
    ArrayList<Product> arrayList;
    Activity context;
    public static final String JSON_ARRAY = "result";
    public static final String IMAGEURL = "url";
    String currentId;
    final Context context2 = this;

    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery2);
        arrayList = new ArrayList<>();
        gridView = (GridView) findViewById(R.id.lst);
        LinearLayout l1 = (LinearLayout) findViewById(R.id.galleryView);
        l1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.uptodown));

        //listView = (ListView) findViewById(R.id.lst);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            getURLs();
            onClickView();
        } else {
            try {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(GalleryTargets.this);

                alertDialog.setTitle("Images are not loaded!");
                alertDialog.setMessage("Internet is not available." + " Cross check your internet connectivity and try again..");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setPositiveButton("Enable wifi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        //finish();
                        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        wifiManager.setWifiEnabled(true);
                        Toast.makeText(getApplicationContext(), "Wi-Fi is Enabled!", Toast.LENGTH_LONG).show();
                        GalleryTargets.this.recreate();


                    }
                });
                alertDialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            vuforiaAppSession.resumeAR();
                        } catch (CameraException e) {
                            e.printStackTrace();
                        }

                    }
                });

                alertDialog.show();
            } catch (Exception e) {
                Log.d(LOGTAG, "Show Dialog: " + e.getMessage());
            }
        }
    }

    public void onClickView() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Product item = (Product) parent.getItemAtPosition(position);
                //Create intent
                Intent intent = new Intent(GalleryTargets.this, Image.class);
                intent.putExtra("image", item.getImage());
                Log.e(LOGTAG, "Image:" + item.getImage());
                //Start details activity
                startActivity(intent);
            }
        });
    }

    public void getURLs() {
        Intent in = getIntent();
        currentId = in.getExtras().getString("imageName");
        class GetURLs extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(GalleryTargets.this, "Loading...", "Fetch images from server...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                loading.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray(JSON_ARRAY);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject productObject = jsonArray.getJSONObject(i);
                        arrayList.add(new Product(
                                productObject.getString(IMAGEURL)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                customadapter = new Customadapter(GalleryTargets.this, R.layout.gallery_list, arrayList);

                gridView.setAdapter(customadapter);
            }


            @Override
            protected String doInBackground(String... strings) {
                String uri = strings[0];
                String add = "https://augmentedandroidapp.000webhostapp.com/index.php/?name=" + uri;

                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL(add);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json);
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        GetURLs gu = new GetURLs();
        gu.execute(String.valueOf(currentId));
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

    @Override
    protected void onResume() {

        Log.e(LOGTAG, "onResume");
        super.onResume();
    }
}
