package com.lyl.bluetoothutils.listener;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

/**
 * <pre>
 *     author : 刘亚林
 *     e-mail : 41973266@qq.com
 *     time   : 2020/06/30
 *     desc   : 蓝牙读取数据处理
 *     version: 1.0
 * </pre>
 */
public interface IBluetoothDeal {
    /**
     * 蓝牙读取
     *
     * @param characteristic
     */
    void readBluetoothGattCharacteristic(final BluetoothGattCharacteristic characteristic);

    /**
     * 数据通知改变
     *
     * @param characteristic
     */
    void changeBluetoothGattCharacteristic(final BluetoothGattCharacteristic characteristic);

    /**
     * 初始化特征码
     *
     * @param bluetoothGatt
     * @param gatt
     */
    void initGattCharacteristic(BluetoothGatt bluetoothGatt, final BluetoothGattService gatt);

    void setCharacteristicNotification(final BluetoothGatt gatt);

    /**
     * 读取数据的UUID
     *
     * @return
     */
    UUID readUUId();

    /**
     * 写数据UUID
     *
     * @return
     */
    UUID writeUUID();

    /**
     * 发现读特征码
     */
    void findReadCharacteristic();

    /**
     * 发现写特征码
     */
    void findWriteCharacteristic();

    /**
     * 服务UUID
     *
     * @return
     */
    UUID serviceUUId();

    /**
     * 写数据
     *
     * @param data
     * @return
     */
    boolean writeData(byte[] data);

    /**
     * 发送成功
     */
    void writeSuccess();

    /**
     * 发送失败
     */
    void writeFail();

    /**
     * 没权限
     */
    void writeNoPermitted();

    /**
     * 检查UUID是否匹配
     *
     * @param uuid
     * @return
     */
    boolean checkWriteCharacteristicDeal(UUID uuid);

    /**
     * 获取mac地址
     *
     * @return
     */
    String getMac();

    /**
     * 有新的数据
     *
     * @param data
     */
    void newDataChange(byte[] data);

    void release();
}
