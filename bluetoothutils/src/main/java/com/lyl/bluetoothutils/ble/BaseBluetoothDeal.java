package com.lyl.bluetoothutils.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;


import com.lyl.bluetoothutils.listener.IBluetoothDeal;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 *     author : 刘亚林
 *     e-mail : 41973266@qq.com
 *     time   : 2020/07/10
 *     desc   : 基础的蓝牙处理类
 *     version: 1.0
 * </pre>
 */
public abstract class BaseBluetoothDeal implements IBluetoothDeal {
    protected String mMac;
    protected BluetoothGatt mBluetoothGatt;
    protected boolean isEnableNotification = true;
    protected BluetoothGattCharacteristic mReadBluetoothData;
    protected BluetoothGattCharacteristic mWriteBluetoothData;

    public BaseBluetoothDeal() {
    }

    public BaseBluetoothDeal(String mac) {
        this.mMac = mac;
    }

    public void setMac(String mac) {
        mMac = mac;
    }

    @Override
    public void changeBluetoothGattCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (characteristic.getUuid().equals(readUUId())) {
            final byte[] data = characteristic.getValue();
            newDataChange(data);
        }
    }

    @Override
    public void readBluetoothGattCharacteristic(BluetoothGattCharacteristic characteristic) {

    }

    /**
     * <pre>
     *     author : 刘亚林
     *     e-mail : 41973266@qq.com
     *     funName: 初始化特征码，如果读写是一个特征码则只需要一个mReadBluetoothData就可以了
     *     params ：
     *     return :
     *     time   : 2020/7/31
     *     desc   :
     *     version: 1.0
     * </pre>
     */
    @Override
    public void initGattCharacteristic(BluetoothGatt bluetoothGatt, BluetoothGattService gatt) {
        if (gatt != null) {
            mBluetoothGatt = bluetoothGatt;
            List<BluetoothGattCharacteristic> characteristics =
                    gatt.getCharacteristics();
            for (BluetoothGattCharacteristic ch : characteristics) {
                if (readUUId() != null && writeUUID() != null && readUUId() == writeUUID()) {
                    mReadBluetoothData = ch;
                    findReadCharacteristic();
                    findWriteCharacteristic();
                } else {
                    if (ch.getUuid().equals(readUUId())) {
                        mReadBluetoothData = ch;
                        findReadCharacteristic();
                    }
                    if (ch.getUuid().equals(writeUUID())) {
                        mWriteBluetoothData = ch;
                        findWriteCharacteristic();
                    }
                }
            }
        }
    }

    @Override
    public boolean writeData(byte[] data) {
        if (readUUId() != null && writeUUID() != null && readUUId() == writeUUID()) {
            if (mReadBluetoothData != null && mBluetoothGatt != null) {
                mReadBluetoothData.setValue(data);
                return mBluetoothGatt.writeCharacteristic(mReadBluetoothData);
            }
        } else {
            if (mWriteBluetoothData != null && mBluetoothGatt != null) {
                mWriteBluetoothData.setValue(data);
                return mBluetoothGatt.writeCharacteristic(mWriteBluetoothData);
            }
        }
        return false;
    }

    @Override
    public void setCharacteristicNotification(BluetoothGatt gatt) {
        if (mReadBluetoothData != null && readDescriptorUuid() != null) {
            gatt.setCharacteristicNotification(mReadBluetoothData, isEnableNotification);
            if (readUUId().equals(mReadBluetoothData.getUuid())) {
                BluetoothGattDescriptor descriptor = mReadBluetoothData.getDescriptor(readDescriptorUuid());
                if (isEnableNotification) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                } else {
                    descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                }
                gatt.writeDescriptor(descriptor);

                onSetCharacteristicNotificationEnd(isEnableNotification);
//                int properties = mReadBluetoothData.getProperties();
//                if (properties == PROPERTY_NOTIFY) {
//                }
            }
        }
    }

    protected void onSetCharacteristicNotificationEnd(boolean isEnableNotification) {
    }


    protected abstract UUID readDescriptorUuid();

    @Override
    public String getMac() {
        return mMac;
    }

    @Override
    public void writeSuccess() {

    }

    @Override
    public void writeFail() {

    }

    @Override
    public void writeNoPermitted() {

    }

    @Override
    public void release() {
        mReadBluetoothData = null;
        mWriteBluetoothData = null;
    }
}
