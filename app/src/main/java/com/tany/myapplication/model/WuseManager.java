package com.tany.myapplication.model;

import android.database.sqlite.SQLiteDatabase;

import com.tany.myapplication.MyApplication;
import com.tany.myapplication.model.gen.DaoMaster;
import com.tany.myapplication.model.gen.DaoSession;

/**
 * Created by tany on 2016/8/9.
 */
//简单的封装
public class WuseManager {
    private static WuseManager mInstance;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    //初始化
    //1.获得指定数据库如wuse-db的协助者对象，（有就获得指定数据库对象，没有就创建。）
    //2.使用数据库链接，用来创建DaoMaster
    //3.创建回话对象（未指定对话对象）
    private WuseManager() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(MyApplication.getAppContext(), "wuse-db", null);
        SQLiteDatabase writableDatabase = devOpenHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(writableDatabase);//传入指定数据可，来初始化DaoMaster
        mDaoSession = mDaoMaster.newSession();
    }

    public static WuseManager getInstance() {//单例
        if (mInstance == null) {
            mInstance = new WuseManager();
        }
        return mInstance;
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    //关闭上一次回话，开启一个先的会话
    public DaoSession getNewSession() {
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }


    //查改增删的方法 实例。CRUD
    /*
    1.查。需求场景。查询表中所有符合条件的 集合（.list()返回集合  .unique()返回单个，可能为空）
        WuseManager.getInstance().getSession().getShopCartDao().queryBuilder().build().list();


    2.改。需求场景：将属性 productId 为0001 商品，更新他的数量 +1
    private void updateUser(String prevName, String newName) {
                //查询执行条件 查询单个
        ShopCart findShopCart = WuseManager.getInstance().getSession().getShopCartDao().queryBuilder()
                .where(ShopCartDao.Properties.Name.eq(prevName)).build().unique();//查询指定条件用户

        if (findShopCart != null) {//可能为空
            findShopCart.setName(newName); //设置新值更新对象
            WuseManager.getInstance().getSession().getShopCartDao().update(findUser);//更新用户} }


    3.新增一个对象
        ShopCartDao shopCartDao = WuseManager.getInstance().getSession().getShopCartDao();
        ShopCart shopCart = new ShopCart(null,id, name,……);
        shopCartDao.insert(shopCart);


    4.删除一个用户 //先查 后删，
        ShopCartDao userDao = WuseManager.getInstance().getSession().getShopCartDao();
        User findUser = shopCartDao.queryBuilder().where(ShopCartDao.Properties.Name.eq(name)).build().unique();

        if (findUser != null) {
            shopCartDao.deleteByKey(findShopCartDao.getId());
        }
     */

}
