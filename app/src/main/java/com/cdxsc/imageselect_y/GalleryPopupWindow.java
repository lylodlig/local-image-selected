package com.cdxsc.imageselect_y;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import java.util.List;

/**
 * Created by lzy on 2017/2/8.
 */
public class GalleryPopupWindow extends PopupWindow {
    private static final String TAG = "lzy";

    RecyclerView mRecyclerView;

    private Activity activity;
    private GalleryPopupWindow.OnItemClickListener onItemClickListener;
    private List<ImageBean> list;
    private GalleryAdapter adapter;


    public GalleryPopupWindow(Activity context, List<ImageBean> list) {
        super(context);
        this.activity = context;
        this.list = list;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.popu_gallery, null);
        initView(contentView);

        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        this.setContentView(contentView);
        this.setWidth(w);
        this.setHeight(ImageSelecteActivity.dp2px(350, context));
        this.setFocusable(false);
        this.setOutsideTouchable(true);
        this.update();

        setBackgroundDrawable(new ColorDrawable(000000000));
    }

    public void notifyDataChanged() {
        adapter.notifyDataSetChanged();
    }

    private void initView(View contentView) {
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.rv_gallery);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new GalleryAdapter(list, activity);
        adapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String fileName) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(fileName);
                    dismiss();
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    //暴露点击的接口
    public interface OnItemClickListener {
        /**
         * @param keyValue
         */
        void onItemClick(String keyValue);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
