package com.tany.myapplication.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tany.myapplication.R;
import com.tany.myapplication.model.bean.JsonDiscover;
import com.tany.myapplication.utils.ImageLoader;
import com.tany.myapplication.view.view.MyImageView;

import java.util.List;

/**
 * Created by tany on 2016/8/8.
 */
public class GvAdapter extends RecyclerView.Adapter<GvAdapter.MyViewHolder> {
    private Context context;
    private List<JsonDiscover.DataBean.ItemsBean> list;

    public GvAdapter(Context context, List<JsonDiscover.DataBean.ItemsBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;

        itemView = LayoutInflater.from(context).inflate(R.layout.discover_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        JsonDiscover.DataBean.ItemsBean itemsBean = list.get(position);
        String imageUrltemp = itemsBean.getImageUrls().get(0).getImageUrl();//图

        int likeCounttemp = itemsBean.getLikeCount();//喜欢
        int commentCounttemp = itemsBean.getCommentCount();//评论

        ImageLoader.initView(context, holder.iv_pic, imageUrltemp);

        holder.tv_like_num.setText(String.valueOf(likeCounttemp));
        holder.tv_comment_num.setText(String.valueOf(commentCounttemp));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private MyImageView iv_pic;

        private TextView tv_like_num;
        private TextView tv_comment_num;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_pic = (MyImageView) itemView.findViewById(R.id.iv_pic);
            tv_like_num = (TextView) itemView.findViewById(R.id.tv_like_num);
            tv_comment_num = (TextView) itemView.findViewById(R.id.tv_comment_num);
        }
    }
}
