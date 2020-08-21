package com.lyl.bluetoothutils.spp;

import android.bluetooth.BluetoothA2dp;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lyl.bluetoothutils.listener.IMediaPlayListener;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     name   : A2dpBroadcastReceiver
 *     author : 刘亚林
 *     e-mail : 41973266@qq.com
 *     time   : 2020/8/7 14:18
 *     desc   : 蓝牙音乐设备状态通知
 *     version: 1.0
 * </pre>
 */
public class A2dpBroadcastReceiver extends BroadcastReceiver {
    List<IMediaPlayListener> mIMediaPlayListeners = new ArrayList<>();

    public void addMediaPlayListener(IMediaPlayListener iMediaPlayListener) {
        if (!mIMediaPlayListeners.contains(iMediaPlayListener))
            mIMediaPlayListeners.add(iMediaPlayListener);
    }

    public void removeMediaPlayListener(IMediaPlayListener iMediaPlayListener) {
        if (mIMediaPlayListeners.contains(iMediaPlayListener))
            mIMediaPlayListeners.remove(iMediaPlayListener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //A2DP连接状态改变
        if (action != null) {
            if (action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
                if (mIMediaPlayListeners != null) {
                    for (IMediaPlayListener iMediaPlayListener : mIMediaPlayListeners) {
                        iMediaPlayListener.connectStatus(state);
                    }
                }
            } else if (action.equals(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED)) {
                //A2DP播放状态改变
                int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_NOT_PLAYING);
                if (mIMediaPlayListeners != null) {
                    for (IMediaPlayListener iMediaPlayListener : mIMediaPlayListeners) {
                        iMediaPlayListener.playStatus(state);
                    }
                }

            }
        }
    }
}
