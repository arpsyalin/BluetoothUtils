package com.lyl.bluetoothutils.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.text.TextUtils;


import com.lyl.bluetoothutils.listener.IBluetoothDeal;

import java.util.List;
import java.util.Vector;

/**
 * <pre>
 *     author : 刘亚林
 *     e-mail : 41973266@qq.com
 *     time   : 2020/06/30
 *     desc   : 蓝牙数据回调处理
 *     version: 1.0
 * </pre>
 */
public class BluetoothCallbackDeal extends BluetoothGattCallback {
    Vector<IBluetoothDeal> mIBluetoothDeals;
    private static Object obj = new Object();
    private static BluetoothCallbackDeal INSTANCE = null;

    public static BluetoothCallbackDeal getInstance() {
        if (INSTANCE == null) {
            synchronized (obj) {
                if (INSTANCE == null) {
                    INSTANCE = new BluetoothCallbackDeal();
                }
            }
        }
        return INSTANCE;
    }

    public BluetoothCallbackDeal() {
        mIBluetoothDeals = new Vector<>();
    }

    public void addIBluetoothDeal(IBluetoothDeal iBluetoothDeal) {
        if (!mIBluetoothDeals.contains(iBluetoothDeal))
            mIBluetoothDeals.add(iBluetoothDeal);
    }

    public void removeIBluetoothDeal(IBluetoothDeal iBluetoothDeal) {
        if (mIBluetoothDeals.contains(iBluetoothDeal))
            mIBluetoothDeals.remove(iBluetoothDeal);

    }

    public void clear() {
        mIBluetoothDeals.clear();
    }

    /**
     * 读取
     *
     * @param gatt
     * @param characteristic
     * @param status
     */
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        for (IBluetoothDeal iBluetoothDeal : mIBluetoothDeals) {
            if (!TextUtils.isEmpty(iBluetoothDeal.getMac()) && !gatt.getDevice().getAddress().equals(iBluetoothDeal.getMac())) {
                continue;
            }
            iBluetoothDeal.readBluetoothGattCharacteristic(characteristic);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        for (IBluetoothDeal iBluetoothDeal : mIBluetoothDeals) {
            if (!TextUtils.isEmpty(iBluetoothDeal.getMac()) && !gatt.getDevice().getAddress().equals(iBluetoothDeal.getMac())) {
                continue;
            }
            iBluetoothDeal.changeBluetoothGattCharacteristic(characteristic);
        }

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        for (IBluetoothDeal iBluetoothDeal : mIBluetoothDeals) {
            if (!TextUtils.isEmpty(iBluetoothDeal.getMac()) && !gatt.getDevice().getAddress().equals(iBluetoothDeal.getMac())) {
                continue;
            }
            if (iBluetoothDeal.checkWriteCharacteristicDeal(characteristic.getUuid())) {
                if (status == BluetoothGatt.GATT_SUCCESS) {//写入成功
                    iBluetoothDeal.writeSuccess();
                } else if (status == BluetoothGatt.GATT_FAILURE) {
                    iBluetoothDeal.writeFail();
                } else if (status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED) {
                    iBluetoothDeal.writeNoPermitted();
                }
            }
        }

    }

    /**
     * 发现了蓝牙服务
     * 在这里保存需要用到的写入蓝牙服务
     *
     * @param gatt
     * @param status
     */
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        List<BluetoothGattService> bluetoothGattServices = gatt.getServices();
        for (BluetoothGattService bluetoothGattService : bluetoothGattServices) {
            for (IBluetoothDeal iBluetoothDeal : mIBluetoothDeals) {
                if (!TextUtils.isEmpty(iBluetoothDeal.getMac()) && !gatt.getDevice().getAddress().equals(iBluetoothDeal.getMac())) {
                    continue;
                }
                if (iBluetoothDeal.serviceUUId() == null || (iBluetoothDeal.serviceUUId() != null && bluetoothGattService.getUuid().equals(iBluetoothDeal.serviceUUId()))) {
                    iBluetoothDeal.initGattCharacteristic(gatt, bluetoothGattService);
                    iBluetoothDeal.setCharacteristicNotification(gatt);
                }
            }
        }
    }

}
