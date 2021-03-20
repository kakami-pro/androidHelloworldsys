package com.example.lzl_task_10.db;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.lzl_task_10.data.City;

import java.util.ArrayList;
import java.util.List;

public class CityDatabase {
    public static final String KEY_ID="mid";
    public static final String KEY_PID="pid";
    public static final String KEY_NAME="name";
    public static final String KEY_WEATHER_ID="weather_id";
    public static final String KEY_EN_NAME="en_name";
    public static final String KEY_INI_NAME="ini_name";
    public static final String KEY_LEVEL="level";
    public static final String KEY_LOOK_UP="key_look_up";
    public static final String DB_NAME="citydb.db";
    public static final String CITY_TABLE="city";
    private int version=3;
    private Activity activity;
    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;
    public CityDatabase(Activity activity)
    {
        this.activity=activity;

    }

    public City queryCityById(int id,int level)
    {
        String sql = String.format("select * from %s where %s=%d and %s=%d", CITY_TABLE, KEY_LEVEL, level, KEY_ID, id);
        List<City> list = getCityListBySql(sql, null);
        if (list.size()>0)
        {
            return list.get(0);
        }
        return null;
    }
    interface LoaderWork{
        List<City> queryWork();
    }

    public interface OnQueryFinished{
         void onFinished(List<City> list);
    }

    private void asyncLoader(OnQueryFinished listener,LoaderWork work)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<City> list = work.queryWork();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFinished(list);
                    }
                });
            }
        }).start();
    }
    public void queryAllProvinceAsync(OnQueryFinished listener)
    {
        asyncLoader(listener, new LoaderWork() {
            @Override
            public List<City> queryWork() {
                return queryAllProvine();
            }
        });
    }

    public void queryCityListByParentIdAsync(int parentId,int level,OnQueryFinished listener)
    {
        asyncLoader(listener, new LoaderWork() {
            @Override
            public List<City> queryWork() {
                return queryCityListByParentId(parentId,level);
            }
        });
    }

    public void fuzzyQueryCityListAsync(String match,OnQueryFinished listener)
    {
        asyncLoader(listener, new LoaderWork() {
            @Override
            public List<City> queryWork() {
                return fuzzyQueryCityList(match);
            }
        });
    }

    public void open(){
        if (db==null||!db.isOpen()){
            databaseHelper=new DatabaseHelper();
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

    private ContentValues enCodeCotentValues(City city)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_EN_NAME, city.getEnName());
        cv.put(KEY_ID, city.getId());
        cv.put(KEY_INI_NAME, city.getInitialName());
        cv.put(KEY_LEVEL, city.getLevel());
        cv.put(KEY_LOOK_UP, "");
        cv.put(KEY_PID, city.getParentId());
        cv.put(KEY_WEATHER_ID, city.getWeather_id());
        cv.put(KEY_NAME, city.getName());
        cv.put(KEY_LOOK_UP,generateLookup(city));
        return cv;
    }

    private City getCityfromCursor(Cursor c){
        String name = c.getString(c.getColumnIndex(KEY_NAME));
        String enName = c.getString(c.getColumnIndex(KEY_EN_NAME));
        String iniName = c.getString(c.getColumnIndex(KEY_INI_NAME));
        String weather_id = c.getString(c.getColumnIndex(KEY_WEATHER_ID));
        int id = c.getInt(c.getColumnIndex(KEY_ID));
        int pid = c.getInt(c.getColumnIndex(KEY_PID));
        int level = c.getInt(c.getColumnIndex(KEY_LEVEL));
        City city = new City(id, name, pid);
        city.setEnName(enName);
        city.setInitialName(iniName);
        city.setLevel(level);
        city.setWeather_id(weather_id);
        return city;

    }

    public List<City> fuzzyQueryCityList(String match)
    {
        if (TextUtils.isEmpty(match))
        {
            return queryAllProvine();
        }
        String sql = String.format("select * from %s where %s like ?", CITY_TABLE, KEY_LOOK_UP);
        String[] args=new String[]{"%"+match+"%"};
        return getCityListBySql(sql,args);
    }

    private String generateLookup(City city)
    {
        String name=city.getName();
        String enName = city.getEnName();
        String initialName = city.getInitialName();
        String[] enNameArray=enName.split("\\s");
        StringBuilder sb = new StringBuilder();
        sb.append(name+" ");
        sb.append(enName+" ");
        sb.append(initialName+" ");
        sb.append(enName.replace("\\s","")+" ");
        for (int i = 1; i < enNameArray.length; i++) {
            sb.append(initialName.substring(0,i));
            for (int j = i; j < enNameArray.length; j++) {
                sb.append(enNameArray[j]);
            }
            sb.append(" ");
        }
        return sb.toString();
    }

    public long insertData(City city)
    {
        ContentValues cv = enCodeCotentValues(city);
        return db.insert(CITY_TABLE,KEY_WEATHER_ID,cv);
    }

    public int insertList(List<City> list)
    {
        int count=0;
        for (int i = 0; i < list.size(); i++) {
            City city = list.get(i);
            if (insertData(city)>0)
            {
                count++;
            }

        }
        return count;
    }

    public  void clearDatabase()
    {
        if (db!=null&&db.isOpen())
        {
            databaseHelper.resetData(db);
        }
    }

    public List<City> getCityListFromCursor(Cursor c)
    {
        List<City> list=new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            City ci = getCityfromCursor(c);
            list.add(ci);

        }
        return list;
    }

    public List<City> getCityListBySql(String sql,String[] args)
    {
        Cursor cursor = db.rawQuery(sql, args);
        List<City> cityListFromCursor = getCityListFromCursor(cursor);
        cursor.close();
        return cityListFromCursor;

    }

    public List<City> queryAllProvine(){
        String sql = String.format("select * from %s where %s=0", CITY_TABLE, KEY_LEVEL);
        return getCityListBySql(sql,null);
    }

    public List<City> queryCityListByParentId(int parentId,int level)
    {
        if (level==0){
            return queryAllProvine();

        }
        String sql = String.format("select * from %s where %s=%d and %s=%d", CITY_TABLE, KEY_PID, parentId, KEY_LEVEL, level);
        return getCityListBySql(sql,null);

    }


    class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper() {
            super(activity, DB_NAME, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = String.format("create table if not exists %s " +
                            "(_id integer primary key autoincrement," +
                            "%s int,%s int,%s text,%s text,%s text,%s int,%s text,%s text)", CITY_TABLE, KEY_ID, KEY_PID
                    , KEY_NAME, KEY_EN_NAME, KEY_INI_NAME, KEY_LEVEL, KEY_LOOK_UP, KEY_WEATHER_ID);
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
            onCreate(db);
        }
    }
}
