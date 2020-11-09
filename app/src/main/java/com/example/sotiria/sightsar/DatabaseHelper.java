package com.example.sotiria.sightsar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by sotiria on 06-Nov-17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "DatabaseLandmarksAR.db";
    public static final String TABLE_NAME = "targetsdesc";
    public static final String YEAR_HISTORY = "targetshistory";
    public static final String YEAR_DESC = "targetsyeardesc";

    //TABLE1
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_6 = "NKEY";
    public static final String COL_4 = "DESCRIPTION1";
    public static final String COL_5 = "DESCRIPTION2";

    //TABLE 2
    public static final String HISTORY_1 = "ID";
    public static final String HISTORY_2 = "YEAR";
    public static final String HISTORY_3 = "NAMEID";
    public static final String HISTORY_4 = "NKEY";


    //TABLE 3
    public static final String YEARDESC_1 = "ID";
    public static final String YEARDESC_2 = "YEAR";
    public static final String YEARDESC_3 = "DESCRIPTION1";

    private static final String LOGTAG = DatabaseHelper.class.getSimpleName();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + COL_1 + " INTEGER PRIMARY KEY, "
                    + COL_2 + " TEXT, "
                    + COL_4 + " TEXT, "
                    + COL_5 + " TEXT, "
                    + COL_6 + " TEXT"
                    + "); ");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + YEAR_HISTORY + " ("
                    + HISTORY_1 + " INTEGER PRIMARY KEY, "
                    + HISTORY_2 + " TEXT, "
                    + HISTORY_3 + " INTEGER, "
                    + HISTORY_4 + " TEXT, "
                    +"FOREIGN KEY(" + HISTORY_3 + ") REFERENCES "
                    + TABLE_NAME + "(" + COL_1 + ") "
                    + "); ");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + YEAR_DESC + " ("
                    + YEARDESC_1 + " INTEGER PRIMARY KEY, "
                    + YEARDESC_2 + " TEXT, "
                    + YEARDESC_3 + " TEXT, "
                    +"FOREIGN KEY(" + YEARDESC_2 + ") REFERENCES "
                    + YEAR_HISTORY + "(" + HISTORY_2 + ") "
                    + "); ");
            Log.e(LOGTAG, "Create application database");
        } catch (Exception e) { System.err.println("Caught IOException: " + e.getMessage());  }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + YEAR_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + YEAR_DESC);
        onCreate(db);
    }
//1st table
    public boolean insertData(Integer id, String key, String name, String description1, String description2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_6, key);
        contentValues.put(COL_2, name);
        contentValues.put(COL_4, description1);
        contentValues.put(COL_5, description2);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;

    }
    //1st table
    public boolean updateXmlValues (Integer id,String key, String name, String description1, String description2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_6, key);
        contentValues.put(COL_2, name);
        contentValues.put(COL_4, description1);
        contentValues.put(COL_5, description2);
        db.update(TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    
//2nd table
    public boolean insertHistoryData(Integer id, String key, String year, Integer frgnKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HISTORY_1, id);
        contentValues.put(HISTORY_4, key);
        contentValues.put(HISTORY_2, year);
        contentValues.put(HISTORY_3, frgnKey);
        long result = db.insert(YEAR_HISTORY, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;

    }
//2nd table
    public boolean updateXmlHistoryValues (Integer id, String key, String year, Integer frgnKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HISTORY_1, id);
        contentValues.put(HISTORY_4, key);
        contentValues.put(HISTORY_2, year);
        contentValues.put(HISTORY_3, frgnKey);
        db.update(YEAR_HISTORY, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
    //3d table
    public boolean insertYearData(Integer id, String currentYear, String year_desc ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(YEARDESC_1, id);
        contentValues.put(YEARDESC_2, currentYear);
        contentValues.put(YEARDESC_3, year_desc);
        long result = db.insert(YEAR_DESC, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;

    }
    //3d table
    public boolean updateYeardescValues(Integer id, String currentYear, String year_desc ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(YEARDESC_1, id);
        contentValues.put(YEARDESC_2, currentYear);
        contentValues.put(YEARDESC_3, year_desc);
        db.update(YEAR_DESC, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

   public Cursor getAlldata(String name) {
       SQLiteDatabase db = this.getWritableDatabase();
       String selectQuery = "SELECT  * FROM targetsdesc JOIN targetshistory ON targetsdesc.NKEY = targetshistory.NKEY WHERE targetsdesc.NKEY LIKE '%" +name + "%'";
        Cursor cursor=db.rawQuery(selectQuery, null);
       return cursor;
    }
    public Cursor getAllyearDesc(String yearname) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM targetshistory JOIN targetsyeardesc ON targetshistory.ID = targetsyeardesc.ID WHERE targetshistory.YEAR LIKE '%" +yearname + "%'";
        Cursor cursor=db.rawQuery(selectQuery, null);
        //for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
          //  Log.e(LOGTAG,"Results->" + cursor.getString(cursor.getColumnIndex(YEARDESC_2)));
        //}
        return cursor;
    }



//
//    public Integer deleteXmlValues (Integer id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        Log.e(LOGTAG,"Deleted");
//
//        return db.delete(TABLE_NAME,
//                "id = ? ",
//                new String[] { Integer.toString(id) });
//    }
//    public ArrayList<String> getAllRows() {
//        ArrayList<String> array_list = new ArrayList<String>();
//
//        //hp = new HashMap();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from " + YEAR_DESC, null );
//        res.moveToFirst();
//
//        while(res.isAfterLast() == false){
//            Log.e(LOGTAG,"Results->" + res.getString(res.getColumnIndex(YEARDESC_2)));
//           res.moveToNext();
//        }
//        return array_list;
//    }

}