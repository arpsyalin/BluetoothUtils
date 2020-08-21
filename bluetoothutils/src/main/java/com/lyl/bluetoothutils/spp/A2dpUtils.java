package com.lyl.bluetoothutils.spp;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import java.lang.reflect.Method;

/**
 * <pre>
 *     name   : A2dpUtils
 *     author : 刘亚林
 *     e-mail : 41973266@qq.com
 *     time   : 2020/8/7 14:17
 *     desc   : 蓝牙音乐工具类
 *     version: 1.0
 * </pre>
 */
public class A2dpUtils {
    /**
     * <pre>
     *     author : 刘亚林
     *     e-mail : 41973266@qq.com
     *     funName: 注册监听回调
     *     params ：
     *     return :
     *     time   : 2020/8/7
     *     desc   :
     *     version: 1.0
     * </pre>
     */
    public static void registerReceiver(Context context, BroadcastReceiver broadcastReceiver) {
        IntentFilter filter = new IntentFilter(BluetoothA2dp.
                ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
        context.registerReceiver(broadcastReceiver, filter);
    }

    /**
     * <pre>
     *     author : 刘亚林
     *     e-mail : 41973266@qq.com
     *     funName: 解除注册监听回调
     *     params ：
     *     return :
     *     time   : 2020/8/7
     *     desc   :
     *     version: 1.0
     * </pre>
     */
    public static void unregisterReceiver(Context context, BroadcastReceiver broadcastReceiver) {
        context.unregisterReceiver(broadcastReceiver);
    }

    /**
     * <pre>
     *     author : 刘亚林
     *     e-mail : 41973266@qq.com
     *     funName: initA2dp
     *     params ：
     *     return :
     *     time   : 2020/8/7
     *     desc   :
     *     version: 1.0
     * </pre>
     */
    public static void initA2dp(Context context, BluetoothProfile.ServiceListener listener) {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            return;
        }
        //获取A2DP代理对象
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, listener, BluetoothProfile.A2DP);
    }

    /**
     * <pre>
     *     author : 刘亚林
     *     e-mail : 41973266@qq.com
     *     funName: 连接a2dp
     *     params ：
     *     return :
     *     time   : 2020/8/7
     *     desc   :
     *     version: 1.0
     * </pre>
     */
    public static boolean connectA2dp(BluetoothA2dp bluetoothA2dp, BluetoothDevice device) {
//        if (!setPriority(bluetoothA2dp, device, 100)) {
//            return false;
//        }
        try {
            //通过反射获取BluetoothA2dp中connect方法（hide的），进行连接。
            Method connectMethod = BluetoothA2dp.class.getMethod("connect",
                    BluetoothDevice.class);
            connectMethod.invoke(bluetoothA2dp, device);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * <pre>
     *     author : 刘亚林
     *     e-mail : 41973266@qq.com
     *     funName: 连接A2dp
     *     params ：
     *     return :
     *     time   : 2020/8/7
     *     desc   :
     *     version: 1.0
     * </pre>
     */
    public static boolean connectA2dp(BluetoothA2dp bluetoothA2dp, String device) {
        if (bluetoothA2dp == null) return false;
        BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device);
        disConnectA2dp(bluetoothA2dp, bluetoothDevice);
        return connectA2dp(bluetoothA2dp, bluetoothDevice);
    }

    /**
     * <pre>
     *     author : 刘亚林
     *     e-mail : 41973266@qq.com
     *     funName: 断开连接
     *     params ：
     *     return :
     *     time   : 2020/8/7
     *     desc   :
     *     version: 1.0
     * </pre>
     */
    public static boolean disConnectA2dp(BluetoothA2dp bluetoothA2dp, BluetoothDevice device) {
        if (bluetoothA2dp == null) return false;
//        if (!setPriority(bluetoothA2dp, device, 0)) {
//            return false;
//        }
        try {
            //通过反射获取BluetoothA2dp中connect方法（hide的），断开连接。
            Method connectMethod = BluetoothA2dp.class.getMethod("disconnect",
                    BluetoothDevice.class);
            connectMethod.invoke(bluetoothA2dp, device);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /**
     * <pre>
     *     author : 刘亚林
     *     e-mail : 41973266@qq.com
     *     funName: 设置优先级
     *     params ：
     *     return :
     *     time   : 2020/8/7
     *     desc   :
     *     version: 1.0
     * </pre>
     */
    public static boolean setPriority(BluetoothA2dp bluetoothA2dp, BluetoothDevice device, int priority) {
        if (bluetoothA2dp == null) return false;
        try {//通过反射获取BluetoothA2dp中setPriority方法（hide的），设置优先级
            Method connectMethod = BluetoothA2dp.class.getMethod("setPriority",
                    BluetoothDevice.class, int.class);
            connectMethod.invoke(bluetoothA2dp, device, priority);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
