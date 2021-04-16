package com.example.lzl_task_10.db;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.lzl_task_10.data.City;
import com.example.lzl_task_10.data.LiteCity;

import java.util.ArrayList;
import java.util.List;

public class MarkerDatabase {
    public static final String KEY_ID="mid";
    public static final String KEY_NAME="name";
    public static final String KEY_LONGITUDE="longitude";
    public static final String KEY_LATITUDE="latitude";
    public static final String KEY_LEVEL="level";
    public static final String DB_NAME= "markerdb.db";
    public static final String CITY_TABLE="marker";
    private int version=1;
    private Activity activity;
    private SQLiteDatabase db;

    public MarkerDatabase(Activity activity) {
        this.activity = activity;
    }
    public void open(){
        if (db==null||!db.isOpen()){
            MarkerDatabaseHelper databaseHelper = new MarkerDatabaseHelper();
            db=databaseHelper.getWritableDatabase();
        }
    }
    public void close()
    {
        if (db!=null&&db.isOpen())
        {
            db.close();
        }
    }

    public long insertData(int id,String name,int level,double longitude,double latitude)
    {
        ContentValues cv =new ContentValues();
        cv.put(KEY_ID,id);
        cv.put(KEY_LEVEL,level);
        cv.put(KEY_NAME,name);
        cv.put(KEY_LONGITUDE,longitude);
        cv.put(KEY_LATITUDE,latitude);
//        System.out.println(name);
        return db.insert(CITY_TABLE,null,cv);
    }

    public List<LiteCity> queryBylevel(int level){
        String sql = String.format("select * from %s where %s=%d", CITY_TABLE, KEY_LEVEL, level);
        Cursor cursor = db.rawQuery(sql, null);
        List<LiteCity> list=new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            LiteCity city=new LiteCity(cursor.getInt(cursor.getColumnIndex(KEY_ID))
            ,cursor.getString(cursor.getColumnIndex(KEY_NAME))
            ,cursor.getInt(cursor.getColumnIndex(KEY_LEVEL))
            ,cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE))
            ,cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)));
            list.add(city);

        }
        return list;
    }



    class MarkerDatabaseHelper extends SQLiteOpenHelper {

        public MarkerDatabaseHelper() {
            super(activity, DB_NAME, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = String.format("create table if not exists %s " +
                            "(_id integer primary key autoincrement," +
                            "%s int,%s text,%s int,%s double,%s double)", CITY_TABLE, KEY_ID
                    , KEY_NAME,   KEY_LEVEL,KEY_LONGITUDE,KEY_LATITUDE);
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            resetData(db);
        }
        public void resetData(SQLiteDatabase db)
        {
            String sql = String.format("drop table if exists %s", CITY_TABLE);
            db.execSQL(sql);
            sql = String.format("create table if not exists %s " +
                            "(_id integer primary key autoincrement," +
                            "%s int,%s text,%s int,%s double,%s double)", CITY_TABLE, KEY_ID
                    , KEY_NAME,   KEY_LEVEL,KEY_LONGITUDE,KEY_LATITUDE);
            db.execSQL(sql);
        }
    }
}
