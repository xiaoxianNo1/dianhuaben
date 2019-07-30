package com.xiaoxian.dianhuaben.SqlList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 简介：
 * 作者：郑现文
 * 创建时间：2019/6/16/ 0016 21:04
 **/
public class DatabaseUtil extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PhoneBook.db";  //数据库名
    private static final int DATABASE_VERSION = 1;               //数据库版本号

    public DatabaseUtil(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    /**
     * 创建数据库
     * */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTable(sqLiteDatabase);
    }

    /**
     * 建立数据表
     * */
    private void createTable(SQLiteDatabase db){
        db.execSQL("create table UserInfo(" +
                "id integer primary key autoincrement," +
                "userName text," +
                "userPhone text)");
    }

    /**
     * 升级数据库
     * */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
