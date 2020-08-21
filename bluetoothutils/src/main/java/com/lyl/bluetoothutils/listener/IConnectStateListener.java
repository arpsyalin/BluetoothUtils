package com.lyl.bluetoothutils.listener;

/**
 * <pre>
 *     author : 刘亚林
 *     e-mail : 41973266@qq.com
 *     time   : 2020/06/30
 *     desc   :连接状态接口
 *     version: 1.0
 * </pre>
 */
public interface IConnectStateListener {
    int SERVICE_DISCONNECTED = 0;
    int SERVICE_CONNECTING = 2;
    int SERVICE_CONNECTED = 1;

    void onConnectStateChanged(String address, int status);
}
