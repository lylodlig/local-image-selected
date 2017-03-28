package com.cdxsc.imageselect_y;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lzy on 2017/2/8.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.NViewHolder> {

    private Context context;
    private List<ImageBean> list;
    private OnItemClickListener onItemClickListener;
    //用于记录是选中的哪一个文件夹
    private int selectedPos;

    public GalleryAdapter(List<ImageBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(String fileName);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public NViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NViewHolder(LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false));
    }

    @Override
    public void onBindViewHolder(NViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPos = position;
                notifyDataSetChanged();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(list.get(position).getFileName());
                }
            }
        });
        if (position == selectedPos) {
            holder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            holder.ivCheck.setVisibility(View.GONE);
        }
        holder.tvCount.setText(list.get(position).getCount() + "张");
        holder.tvName.setText(list.get(position).getFileName());
        Glide.with(context).load("file://" + list.get(position).getFirstPicPath()).into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_itemGallery)
        ImageView iv;
        @BindView(R.id.tv_itemGallery_name)
        TextView tvName;
        @BindView(R.id.tv_itemGallery_count)
        TextView tvCount;
        @BindView(R.id.iv_itemGallery_check)
        ImageView ivCheck;

        public NViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
