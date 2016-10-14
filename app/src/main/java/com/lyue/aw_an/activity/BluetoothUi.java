package com.lyue.aw_an.activity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lyue.aw_an.MainActivity;
import com.lyue.aw_an.R;
import com.lyue.aw_an.ndk.CthData;
import com.lyue.aw_an.ndk.MyUtil;
import com.lyue.aw_an.ndk.ProtocalC;
import com.lyue.aw_an.ndk.SleepUnpack;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wyliu on 2016/8/30.
 */
public class BluetoothUi extends Activity {

    private List<String> mConnBlue = new ArrayList<>();
    private List<String> mDisConnBlue = new ArrayList<>();
    private List<String> mConnWifi = new ArrayList<>();
    private List<String> mDisConnWifi = new ArrayList<>();
    private ArrayList<BluetoothDevice> mLeDevices;
    private ListView mBlueList;
    private ImageView mSaomiao;
    private TextView mMsg;
    Timer timer;
    private MyBluetoothLE bluetoothLE;
    private LeDeviceListAdapter bluetoothAdapter = null;
    private List<BluetoothDevice> mBlueDevice = new ArrayList<>();
    private MainActivity.BluetoothListener bleListener;
    private String wifiname = "lyue";
    private String wifipwd = "781160116";


    private List<String> isClick = new ArrayList();
    private ArrayList<BluetoothDevice> mConnDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shebei_peizhi_ui);

        _initView();
    }

    Handler connBlueHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 1:
                    mConnBlue.add(isClick.get(0));
                    break;
                case 0:
                    mDisConnBlue.add(isClick.get(0));
                    isClick.remove(0);
                    break;
            }
        }
    };

    private void SendData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (bluetoothLE != null) {
                    Message message = connBlueHandler.obtainMessage();
                    message.what = 1;
                    connBlueHandler.sendMessage(message);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String str = "\"" + wifiname + "\",\"" + wifipwd + "\"";
                    byte[] tmp = str.getBytes();
                    MyUtil.ByteArrayToHex(tmp);
                    int index = 0;
                    byte[] send;
                    while (tmp.length > 5) {
                        byte[] cmd = MyUtil.subBytes(tmp, 0, 5);
                        send = getWifiCfgCmdFromJni(cmd, 5, 0, index);
                        bluetoothLE.sendBytesByBluetooth(send, 200 * index);
                        index++;
                        tmp = MyUtil.subBytes(tmp, 5, tmp.length - 5);
                    }
                    send = getWifiCfgCmdFromJni(tmp, tmp.length, 1, index);
                    bluetoothLE.sendBytesByBluetooth(send, 200 * index);
//                    WifiState();
                }
            }
        }).start();
    }

    private void WifiState() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dispHandler.sendEmptyMessage(0);
                if ((SleepUnpack.wifiFailCnt > 5) || SleepUnpack.serverFailCnt > 5) {
                    //出现3次才判定是异常
                    switch (SleepUnpack.mConnStatus) {
                        case 4:
                            SleepUnpack.serverFailCnt = 0;
                            dispHandler.sendEmptyMessage(1);
                            break;

                        case 5:
                            SleepUnpack.wifiFailCnt = 0;
                            dispHandler.sendEmptyMessage(2);
                            break;
                    }
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                }

            }
        }, 2000, 1000);//2s后进入
        cancelTimerHandler.sendEmptyMessageDelayed(0, 15 * 1000);//25s
    }

    Handler cancelTimerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (timer != null) {
                dispHandler.sendEmptyMessage(3);
                try {
                    timer.cancel();
                } catch (Exception e) {

                }
                timer = null;
            }
        }
    };
    private Handler deviceListRefreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BluetoothDevice device = (BluetoothDevice) msg.obj;
            if (bluetoothLE != null) {
                bluetoothAdapter.addDevice(device);
                bluetoothAdapter.notifyDataSetChanged();
            }
        }
    };

    private void _initView() {
        bleListener = new MainActivity.BluetoothListener() {

            @Override
            public void addDevice(BluetoothDevice device) {

                if (!mBlueDevice.contains(device)) {
                    Message msg = new Message();
                    msg.obj = device;
                    deviceListRefreshHandler.sendMessage(msg);
                }
            }

            @Override
            public void connectSuccess() {
                SendData();
            }

            @Override
            public void disconnect() {
                Message message = connBlueHandler.obtainMessage();
                message.what = 0;

                connBlueHandler.sendMessage(message);
            }
        };

        bluetoothLE = new MyBluetoothLE(this, this, bleListener);
        mBlueList = (ListView) findViewById(R.id.blue_list);
        mSaomiao = (ImageView) findViewById(R.id.blue_saomiao);
        mSaomiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothLE.start();
            }
        });
        mMsg = (TextView) findViewById(R.id.blueui_msg);
        bluetoothAdapter = new LeDeviceListAdapter();
        mBlueList.setAdapter(bluetoothAdapter);

        mConnDevices = new ArrayList<BluetoothDevice>();
        mBlueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
                final int position = (int) l;
                String name = mLeDevices.get(position).getName();
                if (isClick.size() == 0) {
                    isClick.add(name);
                } else {
                    return;
                }

                ProgressBar mJindu = (ProgressBar) view.findViewById(R.id.blue_jindu);
                TextView mStatus = (TextView) view.findViewById(R.id.device_status);
                mJindu.setVisibility(ProgressBar.VISIBLE);
                mStatus.setText("配置中...");
                mBlueList.getItemAtPosition(position);
                BluetoothDevice bluetoothDevice = mLeDevices.get(position);
                if (bluetoothLE != null) {
                    if (!mConnDevices.contains(bluetoothDevice)) {
                        bluetoothLE.connect(bluetoothDevice);
//                        mConnDevices.add(bluetoothDevice);
                    } else {
                        SendData();
                    }
                }

            }
        });
    }

    Handler dispHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    mConnWifi.add(isClick.get(0));
                    bluetoothAdapter.notifyDataSetChanged();
                    isClick.remove(0);
                    Toast.makeText(BluetoothUi.this, "配置成功", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    mDisConnWifi.add(isClick.get(0));
                    bluetoothAdapter.notifyDataSetChanged();
                    isClick.remove(0);
                    Toast.makeText(BluetoothUi.this, "服务器连接异常", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    mDisConnWifi.add(isClick.get(0));
                    bluetoothAdapter.notifyDataSetChanged();
                    isClick.remove(0);
                    Toast.makeText(BluetoothUi.this, "WIFI连接失败", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    mDisConnWifi.add(isClick.get(0));
                    bluetoothAdapter.notifyDataSetChanged();
                    isClick.remove(0);
                    Toast.makeText(BluetoothUi.this, "配置wifi异常", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private byte[] getWifiCfgCmdFromJni(byte[] cmd, int cnt, int flag, int no) {
        ProtocalC protocalC = new ProtocalC();
        byte[] tmp = new byte[5];
        for (int i = 0; i < cmd.length; i++) {
            tmp[i] = cmd[i];
        }
        protocalC.SendWifiCwjap(cnt, flag, no, tmp[0], tmp[1], tmp[2], tmp[3],
                tmp[4]);

        CthData cmdData = protocalC.ReadMulCycQue();
        return cmdData.cmd;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private class LeDeviceListAdapter extends BaseAdapter {

        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            mMsg.setVisibility(View.GONE);
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.devicejindu = (ProgressBar) view.findViewById(R.id.blue_jindu);
                viewHolder.deviceAddress = (TextView) view
                        .findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view
                        .findViewById(R.id.device_name);
                viewHolder.deviceStatus = (TextView) view.findViewById(R.id.device_status);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText("未知设备");
            viewHolder.deviceAddress.setText(device.getAddress());


            boolean isConnSuccess = false;
            if (mDisConnBlue.size() != 0) {
                for (String name : mDisConnBlue) {
                    if (name.equals(deviceName)) {
                        isConnSuccess = true;
                    }
                }
            }
            if (isConnSuccess == true) {
                viewHolder.deviceStatus.setText("连接蓝牙失败");
                viewHolder.devicejindu.setVisibility(ProgressBar.INVISIBLE);
            }

            boolean isConn = false;
            if (mConnBlue.size() != 0) {
                for (String name : mConnBlue) {
                    if (name.equals(deviceName)) {
                        isConn = true;
                    }
                }
            }

            if (isConn)
                viewHolder.deviceStatus.setText("正在配置...");
            if (mDisConnWifi.size() != 0) {
                for (String name : mDisConnWifi) {
                    if (deviceName.equals(name)) {
                        viewHolder.deviceStatus.setText("配置失败");
                        viewHolder.devicejindu.setVisibility(ProgressBar.INVISIBLE);
                    }
                }
            }
            if (mConnWifi.size() != 0) {
                for (String name : mConnWifi) {
                    if (deviceName.equals(name)) {
                        viewHolder.deviceStatus.setText("配置成功");
                        viewHolder.devicejindu.setVisibility(ProgressBar.INVISIBLE);
                    }
                }

            }
            return view;
        }

        class ViewHolder {
            TextView deviceName;
            TextView deviceAddress;
            TextView deviceStatus;
            ProgressBar devicejindu;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothLE.close();
    }
}
