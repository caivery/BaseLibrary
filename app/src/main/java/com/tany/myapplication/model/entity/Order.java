package com.tany.myapplication.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by tany on 2016/8/8.
 */
//购物车中的商品
@Entity
public class Order {

    @Id
    private Long id;

    private Long ownerId;//关联外键

    private int productId;//产品id
    private String name;//名称
    private int count;//数量
    private float price;//单价

    private boolean isChecked;//是否被选中

    public boolean getIsChecked() {
        return this.isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public float getPrice() {
        return this.price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProductId() {
        return this.productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Long getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1642498981)
    public Order(Long id, Long ownerId, int productId, String name, int count,
            float price, boolean isChecked) {
        this.id = id;
        this.ownerId = ownerId;
        this.productId = productId;
        this.name = name;
        this.count = count;
        this.price = price;
        this.isChecked = isChecked;
    }

    @Generated(hash = 1105174599)
    public Order() {
    }

}