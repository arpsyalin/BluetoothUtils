package com.lyl.demo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BaseActivity extends AppCompatActivity {
    AlertDialog.Builder mAlertDialogBuilder;
    AlertDialog mAlertDialog;
    /*
        弹窗
     */
    protected void showDialog(String title, String msg, String pStr, DialogInterface.OnClickListener pc, String nStr, DialogInterface.OnClickListener nc) {
        if (mAlertDialogBuilder == null) {
            mAlertDialogBuilder = new AlertDialog.Builder(this);
        }
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        mAlertDialogBuilder.setIcon(R.mipmap.ic_launcher);
        mAlertDialogBuilder.setTitle(TextUtils.isEmpty(title) ? getString(R.string.app_name) : title);
        if (!TextUtils.isEmpty(msg))
            mAlertDialogBuilder.setMessage(msg);
        if (!TextUtils.isEmpty(pStr))
            mAlertDialogBuilder.setPositiveButton(pStr, pc == null ? new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            } : pc);
        if (!TextUtils.isEmpty(nStr))
            mAlertDialogBuilder.setNeutralButton(nStr, nc == null ? new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            } : nc);
        mAlertDialog = mAlertDialogBuilder.show();
    }


    private PermissionListener mlistener;

    /**
     * 权限申请
     * @param permissions 待申请的权限集合
     * @param listener  申请结果监听事件
     */
    protected void requestRunTimePermission(String[] permissions, PermissionListener listener){
        this.mlistener = listener;

        //用于存放为授权的权限
        List<String> permissionList = new ArrayList<>();
        //遍历传递过来的权限集合
        for (String permission : permissions) {
            //判断是否已经授权
            if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                //未授权，则加入待授权的权限集合中
                permissionList.add(permission);
            }
        }

        //判断集合
        if (!permissionList.isEmpty()){  //如果集合不为空，则需要去授权
            ActivityCompat.requestPermissions(this,permissionList.toArray(new String[permissionList.size()]),1);
        }else{  //为空，则已经全部授权
            if(listener != null){
                listener.onGranted();
            }
        }
    }

    /**
     * 权限申请结果
     * @param requestCode  请求码
     * @param permissions  所有的权限集合
     * @param grantResults 授权结果集合
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0){
                    //被用户拒绝的权限集合
                    List<String> deniedPermissions = new ArrayList<>();
                    //用户通过的权限集合
                    List<String> grantedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        //获取授权结果，这是一个int类型的值
                        int grantResult = grantResults[i];

                        if (grantResult != PackageManager.PERMISSION_GRANTED){ //用户拒绝授权的权限
                            String permission = permissions[i];
                            deniedPermissions.add(permission);
                        }else{  //用户同意的权限
                            String permission = permissions[i];
                            grantedPermissions.add(permission);
                        }
                    }

                    if (deniedPermissions.isEmpty()){  //用户拒绝权限为空
                        if(mlistener != null){
                            mlistener.onGranted();
                        }
                    }else {  //不为空
                        if(mlistener != null){
                            //回调授权成功的接口
                            mlistener.onDenied(deniedPermissions);
                            //回调授权失败的接口
                            mlistener.onGranted(grantedPermissions);
                            mlistener.onDenied();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    Toast mToast;

    Context getContext() {
        return this;
    }

    protected void showShort(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) mToast.cancel();
//                if (mToast == null) {
                    mToast = Toast.makeText(getContext(), s, Toast.LENGTH_SHORT);
//                } else {
//                    mToast.setText(s);
//                }
                mToast.show();
            }
        });
    }
}
