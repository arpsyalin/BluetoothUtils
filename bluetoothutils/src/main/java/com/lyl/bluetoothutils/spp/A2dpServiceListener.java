package com.lyl.bluetoothutils.spp;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothProfile;

/**
 * <pre>
 *     name   : A2dpServiceListener
 *     author : 刘亚林
 *     e-mail : 41973266@qq.com
 *     time   : 2020/8/7 14:18
 *     desc   : 蓝牙音乐服务监听
 *     version: 1.0
 * </pre>
 */
public abstract class A2dpServiceListener implements BluetoothProfile.ServiceListener {
    BluetoothA2dp mmBluetoothA2dp;

    @Override
    public void onServiceDisconnected(int profile) {
        if (profile == BluetoothProfile.A2DP) {
            mmBluetoothA2dp = null;
            onA2dpServiceDisconnected();
        }
    }

    protected abstract void onA2dpServiceDisconnected();

    protected abstract void onA2dpServiceConnected(BluetoothA2dp bluetoothA2dp);

    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        if (profile == BluetoothProfile.A2DP) {
            mmBluetoothA2dp = (BluetoothA2dp) proxy; //转换
            onA2dpServiceConnected(mmBluetoothA2dp);
        }
    }


}
