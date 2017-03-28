package com.cdxsc.imageselect_y;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BottomSheetDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //申请权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {//未获取权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //当用户第一次申请拒绝时，再次申请该权限调用
                Toast.makeText(this, "读取图片 ", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0x01);
        }

    }


    public void bt(View view) {
        if (dialog == null) {
            dialog = new BottomSheetDialog(this);
            View inflate = LayoutInflater.from(this).inflate(R.layout.bottom_layout, null);
            Button button = (Button) inflate.findViewById(R.id.bt1);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, ImageSelecteActivity.class));
                    dialog.dismiss();
                }
            });
            dialog.setContentView(inflate);
//            View parent = (View) inflate.getParent();
//            BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
//            inflate.measure(0, 0);
//            behavior.setPeekHeight(inflate.getMeasuredHeight());
//            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
//            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
//            parent.setLayoutParams(params);
        }
        dialog.show();
    }


}
