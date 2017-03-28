package com.cdxsc.imageselect_y;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageSelecteActivity extends AppCompatActivity {

    private static final String TAG = "lzy";
    @BindView(R.id.ib_back)
    ImageButton mButtonBack;
    @BindView(R.id.tv_preview)
    TextView mTextViewPreview;
    @BindView(R.id.rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_allPic)
    TextView mTextViewAllPic;
    @BindView(R.id.bt_confirm)
    Button mButtonConfirm;
    private GalleryPopupWindow mPopupWindow;
    //存储每个目录下的图片路径,key是文件名
    private Map<String, List<String>> mGroupMap = new HashMap<>();
    private List<ImageBean> list = new ArrayList<>();
    //当前文件夹显示的图片路径
    private List<String> listPath = new ArrayList<>();
    //所选择的图片路径集合
    private ArrayList<String> listSelectedPath = new ArrayList<>();


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //扫描完成后
            getGalleryList();
            listPath.clear();
            listPath.addAll(mGroupMap.get("所有图片"));
            adapter.update(listPath);
            if (mPopupWindow != null)
                mPopupWindow.notifyDataChanged();
        }
    };
    private ImageSelectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selecte);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        getImages();
        mRecyclerView.setLayoutManager(new GridLayoutManager(ImageSelecteActivity.this, 3));
        adapter = new ImageSelectAdapter(this, listPath);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnCheckedChangedListener(onCheckedChangedListener);
    }

    @OnClick({R.id.ib_back, R.id.tv_preview, R.id.tv_allPic, R.id.bt_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.tv_preview://跳转预览界面
                Intent intent = new Intent(ImageSelecteActivity.this, ImagePreviewActivity.class);
                //把选中的图片集合传入预览界面
                intent.putStringArrayListExtra("pic", listSelectedPath);
                startActivity(intent);
                break;
            case R.id.tv_allPic://选择图片文件夹
                if (mPopupWindow == null) {
                    //把文件夹列表的集合传入显示
                    mPopupWindow = new GalleryPopupWindow(this, list);
                    mPopupWindow.setOnItemClickListener(new GalleryPopupWindow.OnItemClickListener() {
                        @Override
                        public void onItemClick(String fileName) {
                            //切换了文件夹，清除之前的选择的信息
                            setButtonDisable();
                            listPath.clear();
                            listSelectedPath.clear();
                            //把当前选择的文件夹内图片的路径放入listPath，更新界面
                            listPath.addAll(mGroupMap.get(fileName));
                            adapter.update(listPath);
                            mTextViewAllPic.setText(fileName);
                        }
                    });
                }
                mPopupWindow.showAtLocation(mRecyclerView, Gravity.BOTTOM, 0, dp2px(50, ImageSelecteActivity.this));
                break;
            case R.id.bt_confirm://确定
                for (int i = 0; i < listSelectedPath.size(); i++) {
                    //这里可通过Glide把它转为Bitmap
                    Glide.with(this).load("file://" + listSelectedPath.get(i)).asBitmap().into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Log.i(TAG, "onResourceReady: " + resource);
                        }
                    });
                }
                break;
        }
    }

    /**
     * dp转px
     */
    public static int dp2px(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    //选择图片变化的监听
    private ImageSelectAdapter.OnCheckedChangedListener onCheckedChangedListener = new ImageSelectAdapter.OnCheckedChangedListener() {
        @Override
        public void onChanged(boolean isChecked, String path, CheckBox cb, int position) {
            if (isChecked) {//选中
                if (listSelectedPath.size() == 9) {
                    Toast.makeText(ImageSelecteActivity.this, "最多选择9张图片", Toast.LENGTH_SHORT).show();
                    //把点击变为checked的图片变为没有checked
                    cb.setChecked(false);
                    adapter.setCheckedBoxFalse(position);
                    return;
                }
                //选中的图片路径加入集合
                listSelectedPath.add(path);

            } else {//取消选中
                //从集合中移除
                if (listSelectedPath.contains(path))
                    listSelectedPath.remove(path);
            }
            //如果没有选中的按钮不可点击
            if (listSelectedPath.size() == 0) {
                setButtonDisable();
            } else {
                setButtonEnable();
            }
        }
    };

    //选中图片时的按钮状态
    private void setButtonEnable() {
        mButtonConfirm.setBackgroundResource(R.drawable.selector_bt);
        mButtonConfirm.setTextColor(Color.parseColor("#ffffff"));
        mButtonConfirm.setEnabled(true);
        mTextViewPreview.setEnabled(true);
        mTextViewPreview.setTextColor(getResources().getColor(R.color.colorAccent));
        mButtonConfirm.setText("确定" + listSelectedPath.size() + "/9");
    }

    //没有选择时按钮状态
    private void setButtonDisable() {
        mButtonConfirm.setBackgroundResource(R.drawable.shape_disable);
        mButtonConfirm.setTextColor(Color.parseColor("#676767"));
        mButtonConfirm.setEnabled(false);
        mTextViewPreview.setEnabled(false);
        mTextViewPreview.setTextColor(Color.parseColor("#BEBFBF"));
        mButtonConfirm.setText("确定");
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
     */
    private void getImages() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = ImageSelecteActivity.this.getContentResolver();
                //只查询jpeg和png的图片
//                Cursor mCursor = mContentResolver.query(mImageUri, null,
//                        MediaStore.Images.Media.MIME_TYPE + "=? or "
//                                + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
//                        new String[]{"image/jpeg", "image/png", "image/jpg"}, MediaStore.Images.Media.DATE_MODIFIED);
                Cursor mCursor = mContentResolver.query(mImageUri, null, null, null,
                        MediaStore.Images.Media.DATE_MODIFIED);
                if (mCursor == null) {
                    return;
                }
                //存放所有图片的路径
                List<String> listAllPic = new ArrayList<String>();
                while (mCursor.moveToNext()) {
                    //获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    //获取该图片的父路径名
                    String parentName = new File(path).getParentFile().getName();
                    listAllPic.add(path);

                    //根据父路径名将图片放入到mGruopMap中
                    if (!mGroupMap.containsKey(parentName)) {
                        List<String> chileList = new ArrayList<String>();
                        chileList.add(path);
                        mGroupMap.put(parentName, chileList);
                    } else {
                        mGroupMap.get(parentName).add(path);
                    }
                }
                //添加所有图片
                mGroupMap.put("所有图片", listAllPic);
                //通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0);
                mCursor.close();
            }
        }).start();

    }

    //获取相册文件夹列表
    private void getGalleryList() {
        Iterator<Map.Entry<String, List<String>>> iterator = mGroupMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> next = iterator.next();
            ImageBean imageBean = new ImageBean();
            imageBean.setFileName(next.getKey());
            imageBean.setFirstPicPath(next.getValue().get(0));
            imageBean.setCount(next.getValue().size());
            if (next.getKey().equals("所有图片"))
                list.add(0, imageBean);
            else
                list.add(imageBean);
        }
    }
}
