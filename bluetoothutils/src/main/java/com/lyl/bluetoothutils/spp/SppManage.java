package com.lyl.bluetoothutils.spp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.lyl.bluetoothutils.listener.IConnectStateListener;
import com.lyl.bluetoothutils.listener.IScanBluetoothListener;
import com.lyl.bluetoothutils.listener.DataCallBack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <pre>
 *     name   : SppThread
 *     author : 刘亚林
 *     e-mail : 41973266@qq.com
 *     time   : 2020/8/1
 *     desc   : spp线程
 *     version: 1.0
 * </pre>
 */
public class SppManage {
    ExecutorService mSingleThreadExecutor = Executors.newSingleThreadExecutor();
    private int mState;
    private static SppManage INSTANCE = null;
    private BluetoothDevice mDevice;
    ConnectTask mConnectTask;
    ConnectedTask mConnectedTask;
    IConnectStateListener mConnectCallback;
    DataCallBack mDataCallBack;
    private static final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String mMac;
    String mName;

    private SppManage() {
    }

    /**
     * 静态的管理对象
     *
     * @return
     */
    public static SppManage getInstance() {
        if (INSTANCE == null) {
            synchronized (SppManage.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SppManage();
                }
            }
        }
        return INSTANCE;
    }

    public void setConnectCallback(IConnectStateListener connectCallback) {
        mConnectCallback = connectCallback;
    }

    public void setDataCallback(DataCallBack dataCallback) {
        mDataCallBack = dataCallback;
    }

    public int connectStatus() {
        return mState;
    }

    private class ConnectTask implements Runnable {
        private BluetoothSocket mmSocket;

        public ConnectTask(BluetoothDevice device) {
            try {
                mmSocket = device.createRfcommSocketToServiceRecord(DEFAULT_UUID);
            } catch (IOException e) {
                e.printStackTrace();
                setState(IConnectStateListener.SERVICE_DISCONNECTED);
            }
        }


        public void cancel() {
            if (mmSocket == null) return;
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }

        @Override
        public void run() {
            if (mmSocket == null) {
                return;
            }
            try {
                mmSocket.connect();
            } catch (IOException e) {
                try {
                    mmSocket.close();
                } catch (IOException e2) {

                }
                setState(IConnectStateListener.SERVICE_DISCONNECTED);
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (SppManage.this) {
                mConnectTask = null;
            }

            // Start the connected thread
            connected(mmSocket);

            if (mmSocket == null) {
                return;
            }

        }
    }

    private void connected(BluetoothSocket socket) {
        // Cancel the thread that completed the connection
        if (mConnectTask != null) {
            mConnectTask.cancel();
            mConnectTask = null;
        }

        if (mConnectedTask != null) {
            mConnectedTask.cancel();
            mConnectedTask = null;
        }
        setState(IConnectStateListener.SERVICE_CONNECTED);
        mConnectedTask = new ConnectedTask(socket);
        mSingleThreadExecutor.execute(mConnectedTask);
    }


    private class ConnectedTask implements Runnable {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedTask(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void writeData(byte[] chunk) {
            try {
                mmOutStream.write(chunk);
                mmOutStream.flush();
                // Share the sent message back to the UI Activity

                if (mDataCallBack != null)
                    mDataCallBack.writeData();
            } catch (IOException e) {
            }
        }

        public void write(byte command) {
            byte[] buffer = new byte[1];
            buffer[0] = command;
            try {
                mmOutStream.write(buffer);
                // Share the sent message back to the UI Activity
            } catch (IOException e) {
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[512];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    if (mDataCallBack != null) {
                        final byte[] result = new byte[bytes];
                        System.arraycopy(buffer, 0, result, 0, bytes);
                        mDataCallBack.readData(result, bytes);
                    }
                } catch (IOException e) {
                    setState(IConnectStateListener.SERVICE_DISCONNECTED);
                    break;
                }
            }

        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    public synchronized void connect(String address) {
        connect(address, null);
    }

    public synchronized void connect(String address, String bin) {
        BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        if (!TextUtils.isEmpty(bin))
            bluetoothDevice.setPin(bin.getBytes());
        connect(bluetoothDevice);
    }

    public synchronized void connect(BluetoothDevice device) {
        this.mMac = device.getAddress();
        this.mName = TextUtils.isEmpty(device.getName()) ? device.getAddress() : device.getName();
        this.mDevice = device;
        if (mState == IConnectStateListener.SERVICE_CONNECTING) {
            if (mConnectTask != null) {
                mConnectTask.cancel();
                mConnectTask = null;
            }
        }
        if (mConnectedTask != null) {
            mConnectedTask.cancel();
            mConnectedTask = null;
        }
        mConnectTask = new ConnectTask(device);
        mSingleThreadExecutor.execute(mConnectTask);
        setState(IConnectStateListener.SERVICE_CONNECTING);
    }

    public synchronized void stop() {
        if (mConnectTask != null) {
            mConnectTask.cancel();
            mConnectTask = null;
        }
        if (mConnectedTask != null) {
            mConnectedTask.cancel();
            mConnectedTask = null;
        }
//        setState(IConnectStateListener.SERVICE_DISCONNECTED);
    }

    public boolean write(byte[] data) {
        ConnectedTask r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != IConnectStateListener.SERVICE_CONNECTED) return false;
            r = mConnectedTask;
        }

        // Perform the write unsynchronized
        if (data.length == 1) r.write(data[0]);
        else r.writeData(data);
        return true;
    }

    private synchronized void setState(int state) {
        mState = state;
        if (mConnectCallback != null && mDevice != null)
            mConnectCallback.onConnectStateChanged(mDevice.getAddress(), state);
    }

    public boolean isConnect() {
        return (mState != IConnectStateListener.SERVICE_CONNECTED);
    }


    BroadcastReceiver mFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //找到设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 添加进一个设备列表，进行显示。
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (mIScanBluetoothListener != null)
                        mIScanBluetoothListener.onFoundDevice(device);
                }
            }
            //搜索完成
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                if (mIScanBluetoothListener != null) {
                    mIScanBluetoothListener.onScanStop();
                }
            }
        }
    };
    IScanBluetoothListener mIScanBluetoothListener;

    public void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();//开启搜索
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mFoundReceiver, filter);
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(mFoundReceiver);
    }

    public void addIScanBluetoothListener(IScanBluetoothListener iScanBluetoothListener) {
        mIScanBluetoothListener = iScanBluetoothListener;
    }

    public void removeIScanBluetoothListener() {
        mIScanBluetoothListener = null;
    }


    public void startDiscovery() {
        if (!BluetoothAdapter.getDefaultAdapter().isDiscovering()) {
            BluetoothAdapter.getDefaultAdapter().startDiscovery();
        }
    }

    public void stopDiscovery() {
        if (BluetoothAdapter.getDefaultAdapter().isDiscovering()) {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        }
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
    }

    /**
     * <pre>
     *     author : 刘亚林
     *     e-mail : 41973266@qq.com
     *     funName: 解除配对
     *     params ：
     *     return :
     *     time   : 2020/8/18
     *     desc   :
     *     version: 1.0
     * </pre>
     */
    public void unPairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.setAccessible(true);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e("unPairDevice", e.toString());
        }
    }
}
