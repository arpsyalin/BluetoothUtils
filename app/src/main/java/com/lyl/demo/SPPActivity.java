package com.lyl.demo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lyl.bluetoothutils.ble.BluetoothCallbackDeal;
import com.lyl.bluetoothutils.ble.BluetoothLeManager;
import com.lyl.bluetoothutils.listener.DataCallBack;
import com.lyl.bluetoothutils.listener.IConnectStateListener;
import com.lyl.bluetoothutils.listener.IScanBluetoothListener;
import com.lyl.bluetoothutils.spp.SppManage;
import com.lyl.demo.rv.BaseRecyclerAdapter;
import com.lyl.demo.rv.SmartViewHolder;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SPPActivity extends BaseActivity implements View.OnClickListener, OnRefreshListener, AdapterView.OnItemClickListener, IScanBluetoothListener, IConnectStateListener, DataCallBack {


    /**
     * 输入过滤MAC属性只有包含的时候才会搜索到
     */
    private EditText mEdtInputFilterAddress;
    /**
     * 输入过滤名称属性只有包含的时候才会搜索到
     */
    private EditText mEdtInputFilterName;
    /**
     * 输入Pin
     */
    private EditText mEdtInputPin, mEdtSendCmd;
    private RecyclerView mRvList;
    private SmartRefreshLayout mSrlLayout;
    /**
     * 断开连接
     */
    private Button mBtnDisconnect;
    /**
     * -
     */
    private TextView mTvDeviceInfo;
    /**
     * 16进制
     */
    private RadioButton mRb16;
    /**
     * 非16进制
     */
    private RadioButton mRbNo16;
    /**
     * 发送
     */
    private Button mBtnSend;
    /**
     * 清除接收区
     */
    private Button mBtnClear;
    String mSetPin;
    /**
     *
     */
    private TextView mLogTextview;
    private BluetoothDevice mBluetoothDevice;
    private BroadcastReceiver mFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //找到设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 添加进一个设备列表，进行显示。
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    onFoundDevice(device);
                }
            }
            //搜索完成
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_p_p);
        setTitle("SPP");
        initView();
        SppManage.getInstance().setConnectCallback(this);
        SppManage.getInstance().setDataCallback(this);

        IntentFilter filter = new IntentFilter();//开启搜索
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mFoundReceiver, filter);
        initAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SppManage.getInstance().stop();
        SppManage.getInstance().setConnectCallback(null);
        SppManage.getInstance().setDataCallback(null);
        unregisterReceiver(mFoundReceiver);

    }

    private void startDiscovery() {
        stopDiscovery();
        if (!BluetoothAdapter.getDefaultAdapter().isDiscovering()) {
            BluetoothAdapter.getDefaultAdapter().startDiscovery();
        }
    }

    private void stopDiscovery() {
        if (BluetoothAdapter.getDefaultAdapter().isDiscovering()) {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopDiscovery();

    }

    BaseRecyclerAdapter mAdapter;
    int mTop;

    private void initAdapter() {
        mAdapter = new BaseRecyclerAdapter<BluetoothDevice>(R.layout.itme_bluetooth) {
            @Override
            protected void onBindViewHolder(SmartViewHolder holder, BluetoothDevice model, int position) {
                holder.text(R.id.tv_title, model.getName() + "     " + model.getAddress());
            }
        };
        mAdapter.setOnItemClickListener(this);
        mRvList.setAdapter(mAdapter);
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        mTop = 5;
        mRvList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = mTop;
            }
        });
        mSrlLayout.setEnableLoadMore(false);
        mSrlLayout.setOnRefreshListener(this);
        mSrlLayout.autoRefresh();
    }

    private void initView() {
        mEdtInputFilterAddress = (EditText) findViewById(R.id.edt_input_filter_address);
        mEdtInputFilterName = (EditText) findViewById(R.id.edt_input_filter_name);
        mEdtInputPin = (EditText) findViewById(R.id.edt_input_pin);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mSrlLayout = (SmartRefreshLayout) findViewById(R.id.srl_layout);
        mBtnDisconnect = (Button) findViewById(R.id.btn_disconnect);
        mBtnDisconnect.setOnClickListener(this);
        mTvDeviceInfo = (TextView) findViewById(R.id.tv_deviceInfo);
        mEdtSendCmd = (EditText) findViewById(R.id.edt_sendCom);
        mRb16 = (RadioButton) findViewById(R.id.rb_16);
        mRbNo16 = (RadioButton) findViewById(R.id.rb_no16);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);
        mBtnClear = (Button) findViewById(R.id.btn_clear);
        mBtnClear.setOnClickListener(this);
        mLogTextview = (TextView) findViewById(R.id.log_textview);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_disconnect:
                SppManage.getInstance().stop();
                break;
            case R.id.btn_send:
                String cmd = mEdtSendCmd.getText().toString();
                if (mRb16.isChecked()) {
                    byte[] cmdBytes = HexUtils.string2Hex(cmd.trim());
                    if (cmdBytes != null) {
//                        cmdBytes = GRHL3BluetoothDeal.getCmd(0xF0, 0xC0);
                        if (SppManage.getInstance().write(cmdBytes)) {
                            showShort("已经写入");
                        } else {
                            showShort("未连接写入失败");
                        }
                    } else {
                        showShort("您的输入有误！请重试");
                    }
                } else {
                    byte[] b = null;

//                        cmd = cmd.replace("\n", "\r\n");
                    //若接收端必须接受ANCI编码采用GBK将默认的Unicode转ANCI
//                        b = cmd.getBytes("GBK");
                    b = cmd.getBytes();
                    if (SppManage.getInstance().write(b)) {
                        showShort("已经写入");
                    } else {
                        showShort("未连接写入失败");
                    }
                }
                break;
            case R.id.btn_clear:
                mLogTextview.setText("");
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        mSetPin = mEdtInputPin.getText().toString();
        String prompt = (TextUtils.isEmpty(mSetPin) ? "您未设置配对密码，如果没有可以不设置" : "您设置了配对密码:" + mSetPin + "请确保密码正确才能连接成功！") + " \n 是否继续连接？";
        showDialog("温馨提示", prompt, "连接", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mBluetoothDevice = (BluetoothDevice) mAdapter.get(position);
                SppManage.getInstance().connect(mBluetoothDevice.getAddress(), mSetPin);
            }
        }, "去填写", null);
    }

    @Override
    public void onFoundDevice(BluetoothDevice device) {
        mSrlLayout.finishRefresh();
        device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.getAddress());
        if (!mAdapter.contains(device)) {
            if (!TextUtils.isEmpty(device.getName()) && device.getName().endsWith("BLE")) {
                return;
            }
            String filterName = mEdtInputFilterName.getText().toString();
            String filterAddress = mEdtInputFilterAddress.getText().toString();

            if (TextUtils.isEmpty(device.getName()) && !TextUtils.isEmpty(device.getAddress()) && device.getAddress().toUpperCase().contains(filterAddress.toUpperCase())) {
                addDevice(device);
            }

            if (!TextUtils.isEmpty(device.getName()) && !TextUtils.isEmpty(device.getAddress()) && device.getName().toUpperCase().contains(filterName.toUpperCase()) && device.getAddress().toUpperCase().contains(filterAddress.toUpperCase())) {
                addDevice(device);
            }

        }
    }

    public void addDevice(BluetoothDevice device) {
        List<BluetoothDevice> devices = new ArrayList<>();
        devices.add(device);
        mAdapter.insert(devices);
    }

    @Override
    public void onScanStop() {
        mSrlLayout.finishRefresh();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mAdapter.refresh(new ArrayList());
        startDiscovery();

    }

    @Override
    public void onConnectStateChanged(final String address, int status) {
        String statusStr = "";
        switch (status) {
            case IConnectStateListener.SERVICE_CONNECTED:
                statusStr = "已连接";
                break;
            case IConnectStateListener.SERVICE_CONNECTING:
                statusStr = "连接中";
                break;
            case IConnectStateListener.SERVICE_DISCONNECTED:
                statusStr = "断开连接";
                break;
        }
        showShort("mac:" + address + ";status:" + statusStr);
        final String finalStatusStr = statusStr;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvDeviceInfo.setText("mac:" + address + ";status:" + finalStatusStr);
            }
        });
    }


    @Override
    public void readData(final byte[] data, int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLogTextview.setText(HexUtils.bytes2Hex(data));
            }
        });
    }

    @Override
    public void writeData() {
        showShort("发送写入成功");
    }

}