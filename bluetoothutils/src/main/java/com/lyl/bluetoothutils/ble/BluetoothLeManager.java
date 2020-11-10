package com.lyl.bluetoothutils.ble;

import android.annotation.TargetApi;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.lyl.bluetoothutils.application.ApplicationUtils;
import com.lyl.bluetoothutils.listener.IBluetoothOperation;
import com.lyl.bluetoothutils.listener.IConnectStateListener;
import com.lyl.bluetoothutils.listener.IScanBluetoothListener;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

/**
 * <pre>
 *     author : 刘亚林
 *     e-mail : 41973266@qq.com
 *     time   : 2020/06/30
 *     desc   : 蓝牙使用工具
 *     version: 1.0
 * </pre>
 */
public class BluetoothLeManager implements IBluetoothOperation, BluetoothAdapter.LeScanCallback {
    private static final int AUTO_STOP_SCAN = 5000;
    //蓝牙状态保存，可以通过保存状态，进行设备连接数量管理
    private Map<String, Integer> mBluetoothGattStatusMap = new ConcurrentHashMap();
    //连接
    private Map<String, BluetoothGatt> mBluetoothGattMap = new ConcurrentHashMap();
    private Vector<IConnectStateListener> mConnectStateListeners = new Vector<>();
    Vector<IScanBluetoothListener> mIScanBluetoothListeners = new Vector<>();
    BluetoothGattCallback mBluetoothGattResponse;
    private static volatile BluetoothLeManager INSTANCE = null;

    private BluetoothLeManager() {
        /**
         * 设置默认mBluetoothGattResponse
         */
        setBluetoothGattResponse(BluetoothCallbackDeal.getInstance());
    }

    /**
     * 静态的管理对象
     *
     * @return
     */
    public static BluetoothLeManager getInstance() {
        if (INSTANCE == null) {
            synchronized (BluetoothLeManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BluetoothLeManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 本身回调会在主线程 所以不需要runOnUiThread
     */
    BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (getBluetoothGattResponse() != null)
                getBluetoothGattResponse().onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (getBluetoothGattResponse() != null)
                getBluetoothGattResponse().onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (getBluetoothGattResponse() != null)
                getBluetoothGattResponse().onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, final int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            //不同的手机当蓝牙关闭，设备断开（重启，远离）返回的状态不一样，newState都一样是DISCONNECTED，设备切换不会产生影响
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {//调用connect会调用
                    onDeviceConnected(gatt);
                    if (gatt != null && !gatt.discoverServices()) {
                    }

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {//调用disconnect会调用，设备断开或蓝牙关闭会进入
                    onDeviceDisconnect(gatt, newState);
                }
            } else { //调用connect和disconnect出错后会进入,设备断开或蓝牙关闭会进入
                onDeviceDisconnect(gatt, newState);
            }
            if (getBluetoothGattResponse() != null)
                getBluetoothGattResponse().onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            if (getBluetoothGattResponse() != null)
                getBluetoothGattResponse().onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (getBluetoothGattResponse() != null)
                getBluetoothGattResponse().onDescriptorWrite(gatt, descriptor, status);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            if (getBluetoothGattResponse() != null)
                getBluetoothGattResponse().onMtuChanged(gatt, mtu, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if (getBluetoothGattResponse() != null)
                getBluetoothGattResponse().onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            if (getBluetoothGattResponse() != null)
                getBluetoothGattResponse().onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                onDiscoverServicesSuccess(gatt);
            } else {
                onDiscoverServicesFail(gatt);
            }
            if (getBluetoothGattResponse() != null)
                getBluetoothGattResponse().onServicesDiscovered(gatt, status);
        }
    };


    @Override
    public void onDeviceDisconnect(BluetoothGatt gatt, int newState) {
        if (gatt != null) {
            //is bluetooth enable
            //可以不关闭，以便重用，因为在连接connect的时候可以快速连接
            if (!checkIsSamsung() || !isBluetoothIsEnable()) {//三星手机断开后直接连接
                close(gatt.getDevice().getAddress()); //防止出现status 133
            } else {
                updateConnectState(gatt.getDevice().getAddress(), IConnectStateListener.SERVICE_DISCONNECTED);
            }
        }
    }

    @Override
    public void onDiscoverServicesFail(BluetoothGatt gatt) {
        if (gatt != null) {
            if (!checkIsSamsung() || !isBluetoothIsEnable()) {//三星手机断开后直接连接
                close(gatt.getDevice().getAddress()); //防止出现status 133
            } else {
                updateConnectState(gatt.getDevice().getAddress(), IConnectStateListener.SERVICE_DISCONNECTED);
            }
        }
    }

    @Override
    public void onDiscoverServicesSuccess(BluetoothGatt gatt) {
        if (gatt != null) {
            updateConnectState(gatt.getDevice().getAddress(), IConnectStateListener.SERVICE_CONNECTED);
        }
    }

    @Override
    public void onDeviceConnected(BluetoothGatt gatt) {
        if (gatt != null) {
            updateConnectState(gatt.getDevice().getAddress(), IConnectStateListener.SERVICE_CONNECTED);
        }
    }

    @Override
    public boolean initialize() {
//        // For API level 18 and above, get a reference to BluetoothAdapter through
//        // BluetoothManager.
//        if (mBluetoothManager == null) {
//            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//            if (mBluetoothManager == null) {
//                return false;
//            }
//        }
//        BluetoothAdapter.getDefaultAdapter()  = mBluetoothManager.getAdapter();
//        if (BluetoothAdapter.getDefaultAdapter()  == null) {
//            return false;
//        }
        return true;
    }


    /**
     * 实现蓝牙连接
     *
     * @param address
     */
    @Override
    public boolean connectBluetooth(String address) {
        if (TextUtils.isEmpty(address)) return false;
        if (mBluetoothGattMap.containsKey(address)) {
            BluetoothGatt mBluetoothGatt = mBluetoothGattMap.get(address);
            if (mBluetoothGatt.connect()) {
                updateConnectState(address, IConnectStateListener.SERVICE_CONNECTED);
                return true;
            } else {
                close(address);
                return false;
            }
        }
        final BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        if (device != null) {
            BluetoothGatt bluetoothGatt;
            updateConnectState(address, IConnectStateListener.SERVICE_CONNECTING);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bluetoothGatt = device.connectGatt(ApplicationUtils.getApp(), false, mBluetoothGattCallback, TRANSPORT_LE);
            } else {
                bluetoothGatt = device.connectGatt(ApplicationUtils.getApp(), false, mBluetoothGattCallback);
            }
            if (bluetoothGatt != null) {
                mBluetoothGattMap.put(address, bluetoothGatt);
                return true;
            }

        }
        updateConnectState(address, IConnectStateListener.SERVICE_DISCONNECTED);
        return false;
    }


    /**
     * 实现蓝牙连接
     */
    @Override
    public boolean connectBluetooth(BluetoothDevice device) {
        String address = device.getAddress();
        if (mBluetoothGattMap.containsKey(address)) {
//            BluetoothGatt mBluetoothGatt = mBluetoothGattMap.get(address);
            if (mBluetoothGattStatusMap.containsKey(address)) {
                int status = mBluetoothGattStatusMap.get(address);
                if (status != IConnectStateListener.SERVICE_DISCONNECTED) {
                    updateConnectState(address, status);
                    return true;
                } else {
                    close(address);
                    return false;
                }
            }
//            //这个地方会有问题...当onDeviceDisconnect回调时connect会返回true
//            if (mBluetoothGatt.connect()) {
//                updateConnectState(address, IConnectStateListener.SERVICE_CONNECTED);
//                return true;
//            } else {
//                close(address);
//                return false;
//            }
            //这个也无法区分
//            int bindState = mBluetoothGattMap.get(address).getDevice().getBondState();
//            if (bindState == BluetoothDevice.BOND_BONDED) {
//                updateConnectState(address, IConnectStateListener.SERVICE_CONNECTED);
//                return true;
//            } else if (bindState == BluetoothDevice.BOND_BONDING) {
//                updateConnectState(address, IConnectStateListener.SERVICE_CONNECTING);
//                return true;
//            } else if (bindState == BluetoothDevice.BOND_NONE) {
//                updateConnectState(address, IConnectStateListener.SERVICE_DISCONNECTED);
//                return false;
//            }
        }

        if (device != null) {
            BluetoothGatt bluetoothGatt;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bluetoothGatt = device.connectGatt(ApplicationUtils.getApp(), false, mBluetoothGattCallback, TRANSPORT_LE);
            } else {
                bluetoothGatt = device.connectGatt(ApplicationUtils.getApp(), false, mBluetoothGattCallback);
            }
            if (bluetoothGatt != null) {
                mBluetoothGattMap.put(address, bluetoothGatt);
                updateConnectState(address, IConnectStateListener.SERVICE_CONNECTING);
                return true;
            }
        }
        updateConnectState(address, IConnectStateListener.SERVICE_DISCONNECTED);
        return false;
    }

    @Override
    public boolean close(String address) {
        if (!TextUtils.isEmpty(address) && mBluetoothGattMap.containsKey(address)) {
            BluetoothGatt mBluetoothGatt = mBluetoothGattMap.get(address);
            mBluetoothGatt.close();
            mBluetoothGattMap.remove(address);
            updateConnectState(address, IConnectStateListener.SERVICE_DISCONNECTED);
            mBluetoothGattStatusMap.remove(address);
            return true;
        }
        return false;
    }

    /**
     * release resource
     */
    @Override
    public void release() {
//        mBluetoothGattStatusMap.clear();
        closeAll();
        mBluetoothGattMap.clear();
    }


    /**
     * 关闭所有蓝牙设备
     */
    @Override
    public void closeAll() {
        for (String address : mBluetoothGattMap.keySet()) {
            close(address);
        }
    }

    @Override
    public void setBluetoothGattResponse(BluetoothGattCallback callback) {
        mBluetoothGattResponse = callback;
    }

    @Override
    public BluetoothGattCallback getBluetoothGattResponse() {
        return mBluetoothGattResponse;
    }


    /**
     * 更新状态
     *
     * @param address
     * @param status
     */
    @Override
    public void updateConnectState(String address, int status) {
//        if (!mBluetoothGattStatusMap.containsKey(address)) {
        mBluetoothGattStatusMap.put(address, status);
//        }
        for (IConnectStateListener connectStateListener :
                mConnectStateListeners) {
            connectStateListener.onConnectStateChanged(address, status);
        }
    }


    /**
     * 添加连接和断开监听
     *
     * @param listener
     * @see #removeIConnectStateListener(IConnectStateListener)
     */
    @Override
    public void addIConnectStateListener(IConnectStateListener listener) {
        if (!mConnectStateListeners.contains(listener))
            mConnectStateListeners.add(listener);
    }

    /**
     * remove listener
     *
     * @param listener
     * @see #addIConnectStateListener(IConnectStateListener)
     */
    @Override
    public void removeIConnectStateListener(IConnectStateListener listener) {
        if (mConnectStateListeners.contains(listener))
            mConnectStateListeners.remove(listener);
    }

    @Override
    public void addIScanBluetoothListeners(IScanBluetoothListener iScanBluetoothListener) {
        if (iScanBluetoothListener != null && !mIScanBluetoothListeners.contains(iScanBluetoothListener)) {
            mIScanBluetoothListeners.add(iScanBluetoothListener);
        }
    }

    @Override
    public void removeIScanBluetoothListeners(IScanBluetoothListener iScanBluetoothListener) {
        if (iScanBluetoothListener != null && mIScanBluetoothListeners.contains(iScanBluetoothListener)) {
            mIScanBluetoothListeners.remove(iScanBluetoothListener);
        }
    }

    @Override
    public void removeAllIScanBluetoothListeners() {
        if (mIScanBluetoothListeners != null) {
            mIScanBluetoothListeners.clear();
        }
    }

    @Override
    public int connectState(String address) {
        if (mBluetoothGattStatusMap.containsKey(address)) {
            return mBluetoothGattStatusMap.get(address);
        }
        return IConnectStateListener.SERVICE_DISCONNECTED;
    }

    @Override
    public void startScanBluetooth(boolean enable) {
        if (BluetoothAdapter.getDefaultAdapter() != null) {
            if (enable) {
                Timer timer = new Timer();// 实例化Timer类
                timer.schedule(new TimerTask() {
                    public void run() {
                        BluetoothAdapter.getDefaultAdapter().stopLeScan(BluetoothLeManager.this);
                        notifyScanStop();
                        this.cancel();
                    }
                }, AUTO_STOP_SCAN);// 这里百毫秒
                BluetoothAdapter.getDefaultAdapter().startLeScan(BluetoothLeManager.this);
            } else {
                BluetoothAdapter.getDefaultAdapter().stopLeScan(BluetoothLeManager.this);
                notifyScanStop();
            }
        } else {
            notifyScanStop();
        }
    }


    /**
     * 通知搜索停止
     */
    private void notifyScanStop() {
        for (IScanBluetoothListener iScanBluetoothListener : mIScanBluetoothListeners) {
            iScanBluetoothListener.onScanStop();
        }
    }


    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        for (IScanBluetoothListener iScanBluetoothListener : mIScanBluetoothListeners) {
            iScanBluetoothListener.onFoundDevice(device);
        }
    }

    /**
     * 判断手机类型是否是三星
     *
     * @return
     */
    protected boolean checkIsSamsung() {
        String brand = Build.BRAND;
        if (brand.toLowerCase().equals("samsung")) {
            return true;
        }
        return false;
    }

    /**
     * bluetooth is enable
     */
    public boolean isBluetoothIsEnable() {
        if (BluetoothAdapter.getDefaultAdapter() == null || !ApplicationUtils.getApp().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }
}
