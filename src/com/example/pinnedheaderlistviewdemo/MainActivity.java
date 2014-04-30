package com.example.pinnedheaderlistviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.example.pinnedheaderlistviewdemo.adapter.CityListAdapter;
import com.example.pinnedheaderlistviewdemo.db.CityDao;
import com.example.pinnedheaderlistviewdemo.db.DBHelper;
import com.example.pinnedheaderlistviewdemo.view.BladeView;
import com.example.pinnedheaderlistviewdemo.view.BladeView.OnItemClickListener;
import com.example.pinnedheaderlistviewdemo.view.PinnedHeaderListView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

public class MainActivity extends Activity {

    private static final int COPY_DB_SUCCESS = 10;
    private static final int COPY_DB_FAILED = 11;
    protected static final int QUERY_CITY_FINISH = 12;
    private MySectionIndexer mIndexer;

    private List<City> mCityList = new ArrayList<City>();


    private DBHelper helper;

    private CityListAdapter mAdapter;
    public static final String ALL_CHARACTER = "#ABCDFGHJKLMNOPQRSTWXYZ";
    protected static final String TAG = null;

    private static final String TAG_= "MainActivity" ;

    private String[] sections = {"当前", "A", "B", "C", "D", "F", "G", "H", "J", "K",
            "L", "M", "N", "O", "P", "Q", "R", "S", "T","W", "X",
            "Y", "Z"};
    private int[] counts;
    private PinnedHeaderListView mListView;


    public static String APP_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/";
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case QUERY_CITY_FINISH:

                    if (mAdapter == null) {

                        mIndexer = new MySectionIndexer(sections, counts);

                        mAdapter = new CityListAdapter(mCityList, mIndexer, getApplicationContext());
                        mListView.setAdapter(mAdapter);

                        mListView.setOnScrollListener(mAdapter);

                        //設置頂部固定頭部
                        mListView.setPinnedHeaderView(LayoutInflater.from(getApplicationContext()).inflate(
                                R.layout.list_group_item, mListView, false));

                    } else if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }

                    break;

                case COPY_DB_SUCCESS:
                    requestData();
                    break;
                default:
                    break;
            }
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Log.d(TAG_,"MainActivity onCreate") ;
        helper = new DBHelper();

        copyDBFile();
        findView();
    }

    private void copyDBFile() {

        File file = new File(APP_DIR + "/city.db");
        if (file.exists()) {
            requestData();
            Log.w(TAG_,"") ;

        } else {    //拷贝文件
            Runnable task = new Runnable() {

                @Override
                public void run() {

                    copyAssetsFile2SDCard("city.db");
                }
            };

            new Thread(task).start();
        }
    }

    /**
     * 拷贝资产目录下的文件到 手机
     */
    private void copyAssetsFile2SDCard(String fileName) {

        File desDir = new File(APP_DIR);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }

        // 拷贝文件
        File file = new File(APP_DIR + fileName);
        if (file.exists()) {
            file.delete();
        }

        try {
            InputStream in = getAssets().open(fileName);

            FileOutputStream fos = new FileOutputStream(file);

            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }

            fos.flush();
            fos.close();

            handler.sendEmptyMessage(COPY_DB_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(COPY_DB_FAILED);
        }
    }

    /**
     * 从数据库中请求数据
     */
    private void requestData() {

        Log.d(TAG_,"执行了requestData") ;

        Runnable task = new Runnable() {

            @Override
            public void run() {
                CityDao dao = new CityDao(helper);

                List<City> hot = null;    //热门城市
                List<City> all = null;    //全部城市
                try {
                    hot = dao.getHotCities();
                    all = dao.getAllCities();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (all != null) {

                    Collections.sort(all, new MyComparator());    //排序

                    mCityList.addAll(hot);
                    mCityList.addAll(all);

                    //初始化每个字母有多少个item
                    counts = new int[sections.length];

                    counts[0] = hot.size();    //热门城市 个数

                    TreeSet<String> cityFirstCharOfAbbreviation = new TreeSet<String>() ;

                    for (City city : all) {    //计算全部城市
                        //Note  城市缩写的第一个字母
                        String firstCharacter = city.getSortKey();
                        cityFirstCharOfAbbreviation.add(firstCharacter) ;
                        int index = ALL_CHARACTER.indexOf(firstCharacter);
                        counts[index]++;
                    }
                    Log.w(TAG_,"所有城市缩写的第一个字母" + cityFirstCharOfAbbreviation.toString()) ;

                    handler.sendEmptyMessage(QUERY_CITY_FINISH);
                }
            }
        };

        new Thread(task).start();
    }

    public class MyComparator implements Comparator<City> {

        @Override
        public int compare(City c1, City c2) {

            return c1.getSortKey().compareTo(c2.getSortKey());
        }

    }

    private void findView() {

        mListView = (PinnedHeaderListView) findViewById(R.id.mListView);
        BladeView mLetterListView = (BladeView) findViewById(R.id.mLetterListView);

        mLetterListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(String s) {
                if (s != null) {
                    if (s.equalsIgnoreCase("当前")) {
                        mListView.setSelection(0);
                        return;
                    }

                    int section = ALL_CHARACTER.indexOf(s);

                    int position = mIndexer.getPositionForSection(section);


                    Log.i(TAG_, "s:" + s + ",section:" + section + ",position:" + position);

                    if (position != -1) {
                        mListView.setSelection(position);
                    } else {

                    }
                }

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), mCityList.get(position).getName(),100).show();
            }
        });
    }

}
