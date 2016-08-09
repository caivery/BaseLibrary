package com.tany.myapplication.view.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tany.myapplication.R;
import com.tany.myapplication.model.WuseManager;
import com.tany.myapplication.model.entity.Order;
import com.tany.myapplication.model.gen.OrderDao;
import com.tany.myapplication.view.adapter.RVAdapter;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private TextView title;
    private EditText et_id;
    private EditText et_productid;
    private EditText et_name;
    private EditText et_count;
    private EditText et_price;
    private RecyclerView rv;
    private String userNametemp;
    private Context mContext;

    private RVAdapter adapter;
    private long userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_detail);
        this.mContext = this;
        userNametemp = getIntent().getStringExtra("name");
        userName = Long.parseLong(userNametemp);
        title = (TextView) findViewById(R.id.title);
        title.setText("当前用户：" + userName);

        et_id = (EditText) findViewById(R.id.et_id);
        et_productid = (EditText) findViewById(R.id.et_productid);
        et_name = (EditText) findViewById(R.id.et_name);
        et_count = (EditText) findViewById(R.id.et_count);
        et_price = (EditText) findViewById(R.id.et_price);
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        adapter = new RVAdapter(mContext, orders);
        rv.setAdapter(adapter);


    }


    //查改增删的回调
    List<Order> orders = new ArrayList<>();//adapter数据集合
    List<Order> orderstemp = new ArrayList<>();//查询集合数据集合

    public void query(View v) {
        query();
    }


    private void query() {
        //得到表中该用户的所有商品，并更新界面
        List<Order> orderstemp = WuseManager.getInstance().getSession().getOrderDao()
                .queryBuilder().where(OrderDao.Properties.OwnerId.eq(userName)).build().list();
        orders.clear();
        orders.addAll(orderstemp);
        adapter.notifyDataSetChanged();
    }

    //根据id进行更新
    public void update(View v) {
        getEditText();
        Order unique = WuseManager.getInstance().getSession().getOrderDao().queryBuilder().where(OrderDao.Properties.Id.eq(id)).build().unique();
        if (unique != null && (userName == unique.getOwnerId())) {//判断操作是否属于该用户控制
            Order order = new Order(id, userName, productid, name, count, price, false);
            WuseManager.getInstance().getSession().getOrderDao().update(order);
            query();
        } else {
            Toast.makeText(DetailActivity.this, "请确认id是否正确", Toast.LENGTH_SHORT).show();
        }

    }

    //为该用户添加一个商品
    public void add(View v) {
        getEditText();
        Order order = new Order(null, userName, productid, name, count, price, false);
        WuseManager.getInstance().getSession().getOrderDao().insert(order);
        orders.add(order);
        adapter.notifyDataSetChanged();
    }


    //根据id删除
    public void delete(View v) {
        getEditText();//根据id
        Order unique = WuseManager.getInstance().getSession().getOrderDao().queryBuilder().where(OrderDao.Properties.Id.eq(id)).build().unique();
        if (unique != null && (userName == unique.getOwnerId())) {//判断操作是否属于该用户控制
            WuseManager.getInstance().getSession().getOrderDao().deleteByKey(id);//删除
        } else {
            Toast.makeText(DetailActivity.this, "请确认id是否正确", Toast.LENGTH_SHORT).show();
        }
        query();
    }


    Long id;
    int productid;
    String name;
    int count;
    Float price;

    //获得所有的et文本
    private void getEditText() {
        id = Long.parseLong(et_id.getText().toString());
        productid = Integer.parseInt(et_productid.getText().toString());
        name = et_name.getText().toString();
        count = Integer.parseInt(et_count.getText().toString());
        price = Float.parseFloat(et_price.getText().toString());
    }
}