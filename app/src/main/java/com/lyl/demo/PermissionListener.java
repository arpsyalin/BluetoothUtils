package com.lyl.demo;

import java.util.List;

public interface PermissionListener {

    //授权成功
    void onGranted();

    //授权部分
    void onGranted(List<String> grantedPermission);

    //拒绝授权
    void onDenied(List<String> deniedPermission);

    //授权失败
    void onDenied();

}
