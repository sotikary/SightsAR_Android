package com.example.sotiria.sightsar;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by sot on 1/5/2018.
 */

public class Xml extends AppCompatActivity {
    private static final String LOGTAG = CameraActivity.class.getSimpleName();
    String name = null;
    Integer id_year=null;
    String  year=null, description=null;
    private Context context;
    XmlResourceParser parser2, parser, parser3;
    Integer lid = null;
    String lname = null, description1 = null, description2 = null;
    Integer hid=null ,  frkey=null ;
    String  hyear=null;

    public Xml(Context context) {
        this.context=context;

    }

    public String getXmlValues () throws XmlPullParserException, IOException {
        parser = context.getResources().getXml(R.xml.sights);

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
                        } else if (name.equals("name")) {
                            lname = readText(parser);
                        } else if (name.equals("description")) {
                            description1 = readText(parser);
                        } else if (name.equals("description2")) {
                            description2 = readText(parser);
                        }
                    }
                }
            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e(LOGTAG, "XML NO SUCCESS");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOGTAG, "XML NO SUCCESS");

        } finally {
             }

        return lid + lname + description1 + description2;
    }

    public String getXmlValues2() throws XmlPullParserException, IOException{
        parser2 = context.getResources().getXml(R.xml.history);


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
                        } else if (name.equals("year")) {
                            hyear = readText(parser2);
                        } else if (name.equals("fid")) {
                            frkey = Integer.valueOf(readText(parser2));
                        }
                        Log.e(LOGTAG, "XML2:" +  + hid + hyear + frkey);

                    }
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
        }
        return hid + hyear + frkey;
    }

    public String getXmlValues3() throws XmlPullParserException, IOException{
        parser3 = context.getResources().getXml(R.xml.yeardesc);

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
                        if(name.equals("id")){
                            id_year = Integer.valueOf(readText(parser3));
                        }else if (name.equals("year")) {
                            year = readText(parser3);
                        } else if (name.equals("description")) {
                            description = readText(parser3);
                        }
                        Log.e(LOGTAG, "XML3:" +  year + "" + description);

                    }
                }
                Log.e(LOGTAG, "XML SUCCESS");
            }


        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
            Log.e(LOGTAG, "XML NO SUCCESS");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOGTAG, "XML NO SUCCESS");

        }
        //finally {
           // if (myDb != null) {
          //      myDb.close();
            //    Log.e(LOGTAG, "XML CLOSE");
            //}
       // }
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

    public Integer getLid(){ return  lid;}
    public String getLname(){return  lname; }
    public String getDescription1(){return  description1; }
    public String getDescription2(){return  description2; }

    public Integer getHid(){return hid;}
    public String getHyear(){return hyear;}
    public Integer getFrkey(){return frkey; }
    public Integer getid_year(){
        return id_year;
    }
    public String getYear(){
        return year;
    }
    public String getDescription(){
        return description;
    }



}
