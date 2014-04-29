package com.example.pinnedheaderlistviewdemo.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.pinnedheaderlistviewdemo.City;
import com.example.pinnedheaderlistviewdemo.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class CityDao implements DBHelper.TableData {

    private DBHelper helper;

    private String[] mColumns = {CityTable.id,CityTable.name,CityTable.pyf,CityTable.pys} ;
    private String mSelection = " hot = ? " ;
    private String[] mSelectionArgsHotCities = {"2"} ;

    public CityDao(DBHelper helper) {
        this.helper = helper;
    }

    public List<City> getHotCities() {

        SQLiteDatabase db = helper.getReadableDataBase(MainActivity.APP_DIR, "city.db");

        List<City> list = new ArrayList<City>();

        Cursor cursor = null;

        try {
            if (db.isOpen()) {

                //old
                //:
                String sql = "SELECT id,name,pyf,pys FROM city where hot = 2";
                cursor = db.rawQuery(sql, null);
                //~
                //new 改用android自带的形式，替换sqlraw形式
//                cursor = db.query(CityTable.table,mColumns,mSelection, mSelectionArgsHotCities,null,null,null,null) ;
                while (cursor.moveToNext()) {

                    City city = new City();
                    city.setId(cursor.getString(0));
                    city.setName(cursor.getString(1));
                    city.setPyf(cursor.getString(2));
                    city.setPys(cursor.getString(3));

                    list.add(city);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return list;
    }

    public List<City> getAllCities() {

        SQLiteDatabase db = helper.getReadableDataBase(MainActivity.APP_DIR, "city.db");

        List<City> list = new ArrayList<City>();

        Cursor cursor = null;

        try {
            if (db.isOpen()) {
//                String sql = "SELECT id,name,pyf,pys FROM city";
//                cursor = db.rawQuery(sql, null);
                cursor = db.query(CityTable.table,mColumns,null,null,null,null,null,null) ;
                while (cursor.moveToNext()) {

                    City city = new City();
                    city.setId(cursor.getString(0));
                    city.setName(cursor.getString(1));
                    city.setPyf(cursor.getString(2));
                    city.setPys(cursor.getString(3));

                    list.add(city);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return list;
    }

}
