package com.lyl.bluetoothutils.ble;

import android.app.Service;
import android.bluetooth.BluetoothGattCallback;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


/**
 * <pre>
 *     author : 刘亚林
 *     e-mail : 41973266@qq.com
 *     time   : 2020/06/29
 *     desc   : 通用的蓝牙服务
 *     version: 1.0
 * </pre>
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private final IBinder mBinder = new BluetoothLeService.LocalBinder();
    BluetoothGattCallback mBluetoothGattCallback;

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    /**
     * 实现蓝牙连接
     */

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
