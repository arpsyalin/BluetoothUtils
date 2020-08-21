package com.lyl.bluetoothutils.listener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;

/**
 * <pre>
 *     author : 刘亚林
 *     e-mail : 41973266@qq.com
 *     time   : 2020/06/30
 *     desc   :蓝牙操作接口
 *     version: 1.0
 * </pre>
 */
public interface IBluetoothOperation {
    boolean initialize();

    boolean connectBluetooth(BluetoothDevice device);

    boolean connectBluetooth(String address);

    void addIConnectStateListener(IConnectStateListener listener);

    void removeIConnectStateListener(IConnectStateListener listener);

    void updateConnectState(String address, int status);

    boolean close(String address);

    void closeAll();

    void release();

    void setBluetoothGattResponse(BluetoothGattCallback callback);

    BluetoothGattCallback getBluetoothGattResponse();

    void onDeviceDisconnect(BluetoothGatt gatt, int newState);

    void onDiscoverServicesFail(BluetoothGatt gatt);

    void onDiscoverServicesSuccess(BluetoothGatt gatt);

    void onDeviceConnected(BluetoothGatt gatt);

    void startScanBluetooth(boolean enable);

    void addIScanBluetoothListeners(IScanBluetoothListener iScanBluetoothListener);

    void removeIScanBluetoothListeners(IScanBluetoothListener iScanBluetoothListener);

    void removeAllIScanBluetoothListeners();

    int connectState(String address);
}
