package com.tany.myapplication.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tany.myapplication.R;
import com.tany.myapplication.model.WuseManager;
import com.tany.myapplication.model.entity.Customer;
import com.tany.myapplication.model.gen.CustomerDao;

//一个登录界面，登录后，会直接向你自己购物车显示出来，你可以在这里进行查改增删
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private EditText et;
    private Button login;
    private Button reg;

    private void initView() {
        et = (EditText) findViewById(R.id.et);
        login = (Button) findViewById(R.id.login);
        reg = (Button) findViewById(R.id.reg);
    }

    public void login(View v) {
        String name = et.getText().toString();
        Customer findCus = WuseManager.getInstance().getSession().getCustomerDao()
                .queryBuilder().where(CustomerDao.Properties.Name.eq(name))
                .build().unique();//查询指定条件的 单个用户，返回可能为null
        if (findCus == null) {
            Toast.makeText(LoginActivity.this, "用户不存在，请注册", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
        }
    }


    public void reg(View v) {
        String name = et.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(LoginActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
        } else {
            Customer findCus = WuseManager.getInstance().getSession().getCustomerDao()
                    .queryBuilder().where(CustomerDao.Properties.Name.eq(name))
                    .build().unique();//查询指定条件的 单个用户，返回可能为null
            if (findCus != null) {
                Toast.makeText(LoginActivity.this, "用户已存在，请登录", Toast.LENGTH_SHORT).show();
            } else {
                Customer customer = new Customer(null, name);
                WuseManager.getInstance().getSession().getCustomerDao().insert(customer);

                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }

        }
    }


}
