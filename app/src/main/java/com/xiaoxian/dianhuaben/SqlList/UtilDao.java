package com.xiaoxian.dianhuaben.SqlList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaoxian.dianhuaben.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 简介：
 * 作者：郑现文
 * 创建时间：2019/6/16/ 0016 21:06
 **/
public class UtilDao {
    private DatabaseUtil du;
    private SQLiteDatabase db;

    public UtilDao(Context context){
        du = new DatabaseUtil(context);
        db = du.getWritableDatabase();
    }


    /**
     * 添加数据
     * */
    public void addData(String tableName,String[] key,String[] values){
        try {
            ContentValues contentValues = new ContentValues();
            for(int i = 0; i < key.length; i ++){
                contentValues.put(key[i],values[i]);
            }
            db.insert(tableName,null,contentValues);
            contentValues.clear();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 删除数据
     * */
    public int delData(String where,String[] values){
        int del_data;
        del_data = db.delete("UserInfo",where,values);
        return del_data;
    }

    /**
     * 修改数据
     * */
    public void update(String[] values){
        db.execSQL("update UserInfo set userName=?,userPhone=? where userName=? ",values);
    }

    /**
     * 查询数据
     * */
    public List<User> inquireData(){
        List<User> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select userName,userPhone" +
                " from UserInfo",null);
        while(cursor.moveToNext()){
            String name = cursor.getString(0);
            String phone = cursor.getString(1);

            User user = new User();
            user.setName(name);
            user.setPhone(phone);

            list.add(user);
        }

        return list;
    }

    /**
     * 关闭数据库连接
     * */
    public void getClose(){
        if(db != null){
            db.close();
        }
    }

}
