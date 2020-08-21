package com.lyl.demo;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends BaseActivity implements PermissionListener {
    String[] mPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,};

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestRunTimePermission(mPermissions, this);

    }


    public void toBle(View view) {
        startActivity(new Intent(getContext(), BLEActivity.class));
    }

    public void toSpp(View view) {
        startActivity(new Intent(getContext(), SPPActivity.class));
    }

    @Override
    public void onGranted() {

    }

    @Override
    public void onGranted(List<String> grantedPermission) {
        if (grantedPermission.size() == mPermissions.length) {

        }
    }

    @Override
    public void onDenied(List<String> deniedPermission) {
        if (deniedPermission.size() > 0) {
            showShort("拒绝了权限不能正确使用，程序退出");
            finish();
        }
    }

    @Override
    public void onDenied() {

    }
}
