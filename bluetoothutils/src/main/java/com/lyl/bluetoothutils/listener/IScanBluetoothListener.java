package com.lyl.bluetoothutils.listener;

import android.bluetooth.BluetoothDevice;

/**
 * <pre>
 *     author : 刘亚林
 *     e-mail : 41973266@qq.com
 *     time   : 2020/06/30
 *     desc   : 扫描蓝牙监听
 *     version: 1.0
 * </pre>
 */
public interface IScanBluetoothListener {
    void onFoundDevice(BluetoothDevice device);

    void onScanStop();
}
