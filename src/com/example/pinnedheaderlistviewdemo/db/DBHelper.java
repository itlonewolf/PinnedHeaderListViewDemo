package com.example.pinnedheaderlistviewdemo.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DBHelper {

    private static final String TAG = "DBHelper" ;

    /**
     * 数据库相关的所有表
     */
    public interface TableData{
        /**
         * 城市表
         */
        public interface CityTable{
            String table = "city" ;
            String id ="id" ;
            String name ="name" ;
            String pyf ="pyf" ;
            String pys ="pys" ;
            String hot ="hot" ;
        }
    }

    private ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    /**
     * 从指定数据库文件获取一个可读的   SQLiteDatabase
     * @param dbDirPath
     * @param dbFileName
     * @return
     */
    public SQLiteDatabase getReadableDataBase(String dbDirPath, String dbFileName) {

        readLock.lock();

        try {
            String dbPath = dbDirPath.concat(dbFileName);

            return SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 从指定数据库文件获取一个以读写的   SQLiteDatabase
     * @param dbDirPath
     * @param dbFileName
     * @return
     */
    public SQLiteDatabase getWritableDataBase(String dbDirPath, String dbFileName) {

        writeLock.lock();

        try {
            String dbPath = dbDirPath.concat(dbFileName);

            return SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        } finally {
            writeLock.unlock();
        }
    }
}
