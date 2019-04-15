package yahu.tw.com.mytest;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.vondear.rxtool.RxFileTool;
import com.vondear.rxtool.RxPhotoTool;
import com.vondear.rxtool.view.RxToast;

import java.io.File;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.vondear.rxtool.RxPhotoTool.GET_IMAGE_BY_CAMERA;
import static com.vondear.rxtool.RxPhotoTool.GET_IMAGE_FROM_PHONE;

public class MainActivity extends AppCompatActivity {
    private ImageView image;
    private CustomDialog photo_dialog;
    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rxPermissions = new RxPermissions(this);
        image=findViewById(R.id.image);
        findViewById(R.id.btn_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoDialog();
            }
        });
    }

    void showPhotoDialog() {
        if (null == photo_dialog) {
            photo_dialog = new CustomDialog(this, R.layout.photo_dialog, new int[]{R.id.tv_camera, R.id.tv_photo, R.id.tv_cancel}, false, Gravity.BOTTOM);
            photo_dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {
                @Override
                public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                    switch (view.getId()) {
                        case R.id.tv_cancel:
                            photo_dialog.dismiss();
                            break;
                        case R.id.tv_camera:
                            rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
                                if (aBoolean) {
                                    RxPhotoTool.openCameraImage(MainActivity.this);
                                    photo_dialog.dismiss();
                                }
                            });
                            break;

                        case R.id.tv_photo:
                            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
                                if (aBoolean) {
                                    RxPhotoTool.openLocalImage(MainActivity.this);
                                    photo_dialog.dismiss();
                                }
                            });
                            break;
                    }
                }
            });
        }
        photo_dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GET_IMAGE_BY_CAMERA:

                    String dz = RxPhotoTool.getRealFilePath(this, RxPhotoTool.imageUriFromCamera);

                    Luban.with(this).load(dz).ignoreBy(100).setTargetDir(RxFileTool.getSDCardPath()).filter(path -> !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"))).setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
//                            showLoading();
                        }

                        @Override
                        public void onSuccess(File file) {

                            if (file.exists()) {
                                if (file.exists()) {
                                    String photo_path01 = file.getPath();
                                    Glide.with(MainActivity.this).load(photo_path01).thumbnail(0.5f).into(image);
                                }
                            }
//                            dissLoading();
                        }

                        @Override
                        public void onError(Throwable e) {
//                            dissLoading();
                            RxToast.error(e.getMessage());
                        }
                    }).launch();

                    break;

                case GET_IMAGE_FROM_PHONE://图库

                    if (null != data.getData()) {

                        File files = new File(RxPhotoTool.getImageAbsolutePath(this, data.getData()));

                        Luban.with(this).load(files).setTargetDir(RxFileTool.getSDCardPath()).filter(path -> !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"))).setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {
//                                showLoading();
                            }

                            @Override
                            public void onSuccess(File file) {

                                if (file.exists()) {
                                    String photo_path01 = file.getPath();
                                    Glide.with(MainActivity.this).load(photo_path01).thumbnail(0.5f).into(image);
                                }
//                                dissLoading();
                            }

                            @Override
                            public void onError(Throwable e) {
//                                dissLoading();
                                RxToast.error(e.getMessage());
                            }
                        }).launch();
                    }
                    break;
            }
        }
    }

}
