package com.lyl.demo;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
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

import com.lyl.bluetoothutils.ble.BaseBluetoothDeal;
import com.lyl.bluetoothutils.ble.BluetoothCallbackDeal;
import com.lyl.bluetoothutils.ble.BluetoothLeManager;
import com.lyl.bluetoothutils.listener.IConnectStateListener;
import com.lyl.bluetoothutils.listener.IScanBluetoothListener;
import com.lyl.demo.rv.BaseRecyclerAdapter;
import com.lyl.demo.rv.SmartViewHolder;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BLEActivity extends BaseActivity implements View.OnClickListener, IScanBluetoothListener, IConnectStateListener, OnRefreshListener, AdapterView.OnItemClickListener {

    /**
     * 输入过滤MAC属性只有包含的时候才会搜索到
     */
    private EditText mEdtInputFilterAddress;
    /**
     * 输入过滤名称属性只有包含的时候才会搜索到
     */
    private EditText mEdtInputFilterName;
    /**
     * 输入读UUID
     */
    private EditText mEdtInputReadUuid;
    /**
     * 输入读Descriptor UUID
     */
    private EditText mEdtInputReadDescriptorUuid;
    /**
     * 输入写UUID
     */
    private EditText mEdtInputWriteUuid;
    private EditText mEdtSendCmd;

    /**
     * 输入Pin
     */
    private EditText mEdtInputPin;
    private ClassicsHeader mMhHead;
    private RecyclerView mRvList;
    private SmartRefreshLayout mSrlLayout;
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
    /**
     *
     */
    private TextView mLogTextview;
    private Button mBtnDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_l_e);
        setTitle("BLE");
        initView();
        initAdapter();
        BluetoothLeManager.getInstance().addIScanBluetoothListeners(this);
        BluetoothLeManager.getInstance().addIConnectStateListener(this);
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
        mEdtInputReadUuid = (EditText) findViewById(R.id.edt_input_read_uuid);
        mEdtInputReadDescriptorUuid = (EditText) findViewById(R.id.edt_input_read_descriptor_uuid);
        mEdtInputWriteUuid = (EditText) findViewById(R.id.edt_input_write_uuid);
        mEdtInputPin = (EditText) findViewById(R.id.edt_input_pin);
        mEdtSendCmd = (EditText) findViewById(R.id.edt_sendCom);
        mMhHead = (ClassicsHeader) findViewById(R.id.mh_head);
        mRvList = (RecyclerView) findViewById(R.id.rv_list);
        mSrlLayout = (SmartRefreshLayout) findViewById(R.id.srl_layout);
        mTvDeviceInfo = (TextView) findViewById(R.id.tv_deviceInfo);
        mRb16 = (RadioButton) findViewById(R.id.rb_16);
        mRbNo16 = (RadioButton) findViewById(R.id.rb_no16);
        mBtnSend = (Button) findViewById(R.id.btn_send);

        mBtnDisconnect = (Button) findViewById(R.id.btn_disconnect);
        mBtnSend.setOnClickListener(this);
        mBtnClear = (Button) findViewById(R.id.btn_clear);
        mBtnClear.setOnClickListener(this);
        mLogTextview = (TextView) findViewById(R.id.log_textview);

        initData();
    }

    private void initData() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_send:
                String cmd = mEdtSendCmd.getText().toString();
                if (mRb16.isChecked()) {
                    byte[] cmdBytes = HexUtils.string2Hex(cmd);
                    if (cmdBytes != null) {
//                        cmdBytes = GRHL3BluetoothDeal.getCmd(0xF0, 0xC0);
                        if (mBaseBluetoothDeal.writeData(cmdBytes)) {
                            showShort("已经写入");
                        } else {
                            showShort("未发现特征码或者写入失败");
                        }
                    } else {
                        showShort("您的输入有误！请重试");
                    }
                } else {
                    mBaseBluetoothDeal.writeData(cmd.getBytes());
                }
                break;
            case R.id.btn_clear:
                mLogTextview.setText("");
                break;
            case R.id.btn_disconnect:
                if (mBluetoothDevice != null)
                    BluetoothLeManager.getInstance().close(mBluetoothDevice.getAddress());
                break;
        }
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
    public void onFoundDevice(BluetoothDevice device) {
        mSrlLayout.finishRefresh();
        if (!mAdapter.contains(device)) {
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
        BluetoothLeManager.getInstance().startScanBluetooth(true);
    }

    String mSelectWriteUUID;
    String mSelectReadUUID;
    String mReadDescriptorUuid;
    String mSetPin;
    BluetoothDevice mBluetoothDevice;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        mSelectWriteUUID = mEdtInputWriteUuid.getText().toString();
        mSelectReadUUID = mEdtInputReadUuid.getText().toString();
        mReadDescriptorUuid = mEdtInputReadDescriptorUuid.getText().toString();
        mSetPin = mEdtInputPin.getText().toString();
        String prompt = (!vUUid(mSelectWriteUUID) ? "您未设置或设置了错误的writeUUID，连接成功也将无法使用发送命令\n" : "您设置的writeUUID:" + mSelectWriteUUID + "如果在该设备未查找到该UUID连接成功也将无法使用发送命令\n")
                + ((!vUUid(mSelectReadUUID) || (!vUUid(mReadDescriptorUuid))) ? "您未设置或设置了错误的readUUID 或readDescriptorUuid，连接成功也将无法接收到数据\n" : "您设置的readUUID:" + mSelectWriteUUID + ";readDescriptorUuid:" + mReadDescriptorUuid + "\n 如果在该设备未查找到该UUID连接成功也将无法使用发送命令\n");
        prompt += (TextUtils.isEmpty(mSetPin) ? "您未设置配对密码，如果没有可以不设置" : "您设置了配对密码:" + mSetPin + "请确保密码正确才能连接成功！") + " \n 是否继续连接？";

        showDialog("温馨提示", prompt, "连接", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (mBluetoothDevice != null) {
                    BluetoothLeManager.getInstance().close(mBluetoothDevice.getAddress());
                }
                mBluetoothDevice = (BluetoothDevice) mAdapter.get(position);
                if (!TextUtils.isEmpty(mSetPin))
                    mBluetoothDevice.setPin(mSetPin.getBytes());
                mBaseBluetoothDeal.setMac(mBluetoothDevice.getAddress());
                BluetoothCallbackDeal.getInstance().addIBluetoothDeal(mBaseBluetoothDeal);
                BluetoothLeManager.getInstance().connectBluetooth(mBluetoothDevice);

            }
        }, "去填写", null);
    }

    public boolean vUUid(String name) {
        if (TextUtils.isEmpty(name)) return false;
        String[] components = name.split("-");
        if (components.length != 5)
            return false;
        return true;
    }

    BaseBluetoothDeal mBaseBluetoothDeal = new BaseBluetoothDeal() {
        @Override
        public UUID readUUId() {
            if (vUUid(mSelectReadUUID)) {
                return UUID.fromString(mSelectReadUUID);
            }
            return null;
        }

        @Override
        public UUID writeUUID() {
            if (vUUid(mSelectWriteUUID)) {
                return UUID.fromString(mSelectWriteUUID);
            }
            return null;
        }

        @Override
        public void findReadCharacteristic() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showShort("发现可用读特征码，如设置了通知权限将有数据回调！");
                }
            });
        }

        @Override
        public void findWriteCharacteristic() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showShort("发现可用写特征码，可进行写入！");
                }
            });
        }

        @Override
        public UUID serviceUUId() {
            return null;
        }


        @Override
        public boolean checkWriteCharacteristicDeal(UUID uuid) {
            return false;
        }

        @Override
        public void newDataChange(byte[] data) {
            final String hex = new BigInteger(1, data).toString(16);
//            logHtml.append(hex);
//            if (logHtml.length() > hex.length() * 20) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLogTextview.setText(hex);
//                        logHtml.setLength(0);
//                        final int scrollAmount = mLogTextview.getLayout().getLineTop(mLogTextview.getLineCount()) - mLogTextview.getHeight();
//                        if (scrollAmount > 0)
//                            mLogTextview.scrollTo(0, scrollAmount);
//                        else mLogTextview.scrollTo(0, 0);
                }
            });
//            }
        }

        @Override
        protected UUID readDescriptorUuid() {
            if (vUUid(mReadDescriptorUuid)) {
                return UUID.fromString(mReadDescriptorUuid);
            }
            return null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothLeManager.getInstance().closeAll();
        BluetoothCallbackDeal.getInstance().removeIBluetoothDeal(mBaseBluetoothDeal);
        mBaseBluetoothDeal = null;
    }
}