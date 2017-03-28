package com.cdxsc.imageselect_y;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lzy on 2017/2/8.
 */
public class ImageSelectAdapter extends RecyclerView.Adapter<ImageSelectAdapter.NViewHolder> {

    private Context context;
    private List<String> list = new ArrayList<>();
    private OnCheckedChangedListener onCheckedChangedListener;
    private List<Boolean> listChecked = new ArrayList<>();

    public ImageSelectAdapter(Context context, List<String> list) {
        this.context = context;
        this.list.addAll(list);
        setListCheched(list);
    }

    public void update(List<String> list) {
        this.list.clear();
        this.list.addAll(list);
        setListCheched(list);
        notifyDataSetChanged();

    }

    /**
     * 设置listChecked的初始值
     *
     * @param list
     */
    private void setListCheched(List<String> list) {
        listChecked.clear();
        for (int i = 0; i < list.size(); i++) {
            listChecked.add(false);
        }
    }

    //当点击超过了九张图片，再点击的设置为false
    public void setCheckedBoxFalse(int pos) {
        listChecked.set(pos, false);
    }

    public interface OnCheckedChangedListener {
        /**
         * @param isChecked 是否选中
         * @param path      点击的图片路径
         * @param cb        点击的CheckBox
         * @param pos       点击的位置
         */
        void onChanged(boolean isChecked, String path, CheckBox cb, int pos);
    }

    public void setOnCheckedChangedListener(OnCheckedChangedListener onCheckedChangedListener) {
        this.onCheckedChangedListener = onCheckedChangedListener;
    }

    @Override
    public NViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NViewHolder(LayoutInflater.from(context).inflate(R.layout.item_image_select, parent, false));
    }

    @Override
    public void onBindViewHolder(final NViewHolder holder, final int position) {
        Glide.with(context).load("file://" + list.get(position)).into(holder.iv);
        holder.cb.setChecked(listChecked.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cb.setChecked(!holder.cb.isChecked());
                if (holder.cb.isChecked()) {
                    listChecked.set(position, true);
                } else {
                    listChecked.set(position, false);
                }
                if (onCheckedChangedListener != null) {
                    onCheckedChangedListener.onChanged(holder.cb.isChecked(), list.get(position), holder.cb, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_itemImageSelect)
        ImageView iv;
        @BindView(R.id.cb_itemImageSelect)
        CheckBox cb;

        public NViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
