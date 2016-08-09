package com.tany.myapplication.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tany.myapplication.R;
import com.tany.myapplication.model.entity.Order;

import java.util.List;

/**
 * Created by tany on 2016/8/9.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {
    private Context mContext;
    private List<Order> orders;

    public RVAdapter(Context mContext, List<Order> orders) {
        this.mContext = mContext;
        this.orders = orders;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_rv, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_id.setText(orders.get(position).getId() + "");
        holder.tv_productid.setText(orders.get(position).getProductId() + "");
        holder.tv_name.setText(orders.get(position).getName() + "");
        holder.tv_count.setText(orders.get(position).getCount() + "");
        holder.tv_price.setText(orders.get(position).getPrice() + "");

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_id;
        private TextView tv_productid;
        private TextView tv_name;
        private TextView tv_count;
        private TextView tv_price;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_id = (TextView) itemView.findViewById(R.id.tv_id);
            tv_productid = (TextView) itemView.findViewById(R.id.tv_productid);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_count = (TextView) itemView.findViewById(R.id.tv_count);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
        }
    }
}