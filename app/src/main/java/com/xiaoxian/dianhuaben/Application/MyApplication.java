package com.xiaoxian.dianhuaben.Application;

import android.app.Application;

import com.xiaoxian.dianhuaben.SqlList.UtilDao;

/**
 * 简介：
 * 作者：郑现文
 * 创建时间：2019/6/16/ 0016 21:21
 **/
public class MyApplication extends Application {
    private UtilDao dao;

    /**
     * 创建时调用
     * */
    @Override
    public void onCreate() {
        super.onCreate();
        dao = new UtilDao(this);
    }

    /**
     * 后台进程终止，前台程序需要内存时调用此方法，用于释放内存
     * 用于关闭数据库连接
     * */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        dao.getClose();
    }

    public UtilDao getDao() {
        return dao;
    }

}
