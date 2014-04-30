package com.example.pinnedheaderlistviewdemo.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.pinnedheaderlistviewdemo.City;
import com.example.pinnedheaderlistviewdemo.MainActivity;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class CityDao implements DBHelper.TableData {
    private static final String TAG = "CityDao" ;

    private DBHelper helper;

    private String[] mColumns = {CityTable.id,CityTable.name,CityTable.pyf,CityTable.pys} ;
    private String mSelection = " hot = ? " ;
    private String[] mSelectionArgsHotCities = {"2"} ;

    /**
     * <p>
     *     因为热门城市和全部城市，一般不会改变，所以将其设置为静态的
     * </p>
     */
    private static List<City> mHotCities; //热门城市
    private static List<City> mAllCities; //全部城市
    private static TreeSet<String> mFirstCharFromAbbreviationOfCity;  //所有城市缩写的首字母

    private static int mPreCharNum;//当前 热门等选项的个数



    public CityDao(DBHelper helper) {
        this.helper = helper;
        mHotCities = getHotCitiesFromDB() ;
        mAllCities = getAllCitiesFromDB() ;
        getFirstCharFromAbbreviationOfCity() ;
    }


    /**
     * 从数据库中获取热门城市
     * @return
     */
    private List<City> getHotCitiesFromDB(){
        SQLiteDatabase db = helper.getReadableDataBase(MainActivity.APP_DIR, "city.db");

        List<City> list = new ArrayList<City>();

        Cursor cursor = null;

        try {
            if (db.isOpen()) {

                //old
                //:
//                String sql = "SELECT id,name,pyf,pys FROM city where hot = 2";
//                cursor = db.rawQuery(sql, null);
                //~
                //new 改用android自带的形式，替换sqlraw形式
                cursor = db.query(CityTable.table,mColumns,mSelection, mSelectionArgsHotCities,null,null,null,null) ;

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
    /**
     * 获取所有热门城市
     * @return
     */
    public List<City> getHotCities() throws Exception {
        if (mHotCities == null) {
            //TODO  实际上只要初始化了本类，就不会运行到此处
            throw  new Exception("数据库中的热门城市为空") ;
        }
        return mHotCities;
//        SQLiteDatabase db = helper.getReadableDataBase(MainActivity.APP_DIR, "city.db");
//
//        List<City> list = new ArrayList<City>();
//
//        Cursor cursor = null;
//
//        try {
//            if (db.isOpen()) {
//
//                //old
//                //:
////                String sql = "SELECT id,name,pyf,pys FROM city where hot = 2";
////                cursor = db.rawQuery(sql, null);
//                //~
//                //new 改用android自带的形式，替换sqlraw形式
//                cursor = db.query(CityTable.table,mColumns,mSelection, mSelectionArgsHotCities,null,null,null,null) ;
//
//                while (cursor.moveToNext()) {
//
//                    City city = new City();
//                    city.setId(cursor.getString(0));
//                    city.setName(cursor.getString(1));
//                    city.setPyf(cursor.getString(2));
//                    city.setPys(cursor.getString(3));
//
//                    list.add(city);
//                }
//            }
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//            db.close();
//        }
//
//        return list;
    }

    /**
     * 从数据库中获取全部城市
     * @return
     */
    private List<City> getAllCitiesFromDB(){
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
    /**
     * 获取数据库中的所有城市
     * @return
     */
    public List<City> getAllCities() throws Exception {
        if (mAllCities == null) {
            throw  new Exception("数据库中全部城市为空") ;
        }
        return mAllCities;
//        SQLiteDatabase db = helper.getReadableDataBase(MainActivity.APP_DIR, "city.db");
//
//        List<City> list = new ArrayList<City>();
//
//        Cursor cursor = null;
//
//        try {
//            if (db.isOpen()) {
////                String sql = "SELECT id,name,pyf,pys FROM city";
////                cursor = db.rawQuery(sql, null);
//                cursor = db.query(CityTable.table,mColumns,null,null,null,null,null,null) ;
//                while (cursor.moveToNext()) {
//
//                    City city = new City();
//                    city.setId(cursor.getString(0));
//                    city.setName(cursor.getString(1));
//                    city.setPyf(cursor.getString(2));
//                    city.setPys(cursor.getString(3));
//
//                    list.add(city);
//                }
//            }
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//            db.close();
//        }
//
//        return list;
    }

    /**
     * 获取所有城市缩写的首字母,
     * 例如
     * <p>
     *     所有城市缩写的第一个字母[A, B, C, D, F, G, H, J, K, L, M, N, P, Q, R, S, T, W, X, Y, Z]
     * </p>
     * @return
     */
    private TreeSet<String> getFirstCharFromAbbreviationOfCity() {
        if (mFirstCharFromAbbreviationOfCity == null) {
            mFirstCharFromAbbreviationOfCity = new TreeSet<String>() ;
            for (City city : mAllCities) {    //计算全部城市
                //Note  城市缩写的第一个字母
                String firstCharacter = city.getSortKey();
                mFirstCharFromAbbreviationOfCity.add(firstCharacter);
            }
        }

        return mFirstCharFromAbbreviationOfCity;
    }

    /**
     * 可变参数
     * @param presentLocation  当前位置
     *        hotCities        热门城市
     *
     * @return
     * 返回结果类似与以下
     * <p>
     *     {"当前", "热门", "A", "B", "C", "D", "F", "G", "H", "J", "K",
     *       "L", "M", "N", "O", "P", "Q", "R", "S", "T", "W", "X",
     *       "Y", "Z"};
     * </p>
     */
    public String[] sectionsAndBlade(String... presentLocation){
        mPreCharNum = presentLocation.length ;
        String [] sectionsAndBlade ;
        sectionsAndBlade = ArrayUtils.addAll(
                presentLocation,
                mFirstCharFromAbbreviationOfCity.toArray(new String[mFirstCharFromAbbreviationOfCity.size()])) ;
        for (String string : sectionsAndBlade) {
            Log.d(TAG,string) ;
        }
        return  sectionsAndBlade ;
    }
    // public static final String ALL_CHARACTER = "#ABCDFGHJKLMNOPQRSTWXYZ";
    public String allCharacter(){
        StringBuilder stringBuilder = new StringBuilder() ;
        for (int i = 0; i < mPreCharNum; i++) {
            stringBuilder.append("#") ;
        }
        for (String s : mFirstCharFromAbbreviationOfCity) {
            stringBuilder.append(s) ;
        }
        return  stringBuilder.toString() ;

    }
}