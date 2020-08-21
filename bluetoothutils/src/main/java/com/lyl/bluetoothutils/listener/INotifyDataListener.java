package com.lyl.bluetoothutils.listener;

/**
 * <pre>
 *     author : 刘亚林
 *     e-mail : 41973266@qq.com
 *     time   : 2020/06/30
 *     desc   : 分发数据
 *     version: 1.0
 * </pre>
 */
public interface INotifyDataListener {
    /**
     * 第一参数是标识
     *
     * @param tag
     * @param buf
     */
    void notifyData(String tag, byte[] buf);
}
