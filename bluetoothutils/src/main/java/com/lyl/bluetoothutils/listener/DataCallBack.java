package com.lyl.bluetoothutils.listener;

/**
 * <pre>
 *     name   : DataCallBack
 *     author : 刘亚林
 *     e-mail : 41973266@qq.com
 *     time   : 2020/8/3 8:40
 *     desc   :  數據回調
 *     version: 1.0
 * </pre>
 */
public interface DataCallBack {
    void readData(byte[] data, int size);

    void writeData();
}
