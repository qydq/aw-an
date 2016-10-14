package com.lyue.aw_an.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.lyue.aw_an.MainActivity;
import com.lyue.aw_an.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyBluetoothLE implements Serializable {

    private String mDeviceName;
    private String mDeviceAddress;
    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeService mBluetoothLeService;
    private Context context;
    private Activity activity;
    private MainActivity.BluetoothListener listener = null;
    private boolean mScanning;
    private static final int REQUEST_ENABLE_BT = 1;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    public static boolean bluetoothConnected = false;
    public static boolean isConnecting = false;// 正在连接中
    public static boolean isRequesting = false;// 正在请求用户重连
    public static boolean isRemindRecon = true;// 是否提醒重连
    static public BluetoothGattCharacteristic connectedCharacteristic = null;
    private boolean isFoundDevice = false;
    private LeDeviceListAdapter mLeDeviceListAdapter;

    private static final String CONNECT_UUID_LIST_YELLOW = "00001000-0000-1000-8000-00805f9b34fb";
    private static final String CONNECT_UUID_YELLOW = "00001002-0000-1000-8000-00805f9b34fb";

    private static final String CONNECT_UUID_LIST_BLUE = "0000fff0-0000-1000-8000-00805f9b34fb";
    private static final String CONNECT_UUID_BLUE = "0000fff1-0000-1000-8000-00805f9b34fb";

    private static final String CONNECT_NAME = "";
    private static String deviceName;
    private static String deviceUuid;
    private static ProgressDialog pd;
    private AlertDialog.Builder builder;

    public MyBluetoothLE(Context context, Activity activity,
                         MainActivity.BluetoothListener listener) {
        this.context = context;
        this.activity = activity;
        this.listener = listener;
        mLeDeviceListAdapter = new LeDeviceListAdapter();
    }

    public static boolean isRuning = false;

    public void start() {
        isConnecting = true;
        close();
        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
        }
        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) (context
                .getSystemService(Context.BLUETOOTH_SERVICE));
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            if (activity != null) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent,
                        REQUEST_ENABLE_BT);
                System.out.println("vik...startBluetooth");
            }
        }
        if (isRuning == false) {
            mHandler = new Handler();
            scanLeDevice(true);
            isRuning = true;
        }
    }

    Runnable stopSccan = new Runnable() {
        @Override
        public void run() {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            pd.dismiss();
            // Toast.makeText(context, "设备连接失败，请检查设备蓝牙",
            // Toast.LENGTH_LONG).show();
            isConnecting = false;
        }
    };

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // pd = ProgressDialog.show(context, "connecting", "连接设备中");
            pd = ProgressDialog.show(context, "connecting", "设备搜索中");
            pd.setCancelable(true);
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(stopSccan, 12000);// 12s

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            System.out.println("vik..startLeScan");

        } else {
            if (pd != null) {
                pd.dismiss();
            }
            mScanning = false;
            if (mLeScanCallback != null && mBluetoothAdapter != null) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            if (device.getName() == null) {
                return;
            }

            if (listener != null) {
                listener.addDevice(device);
            }
        }
    };


    public void connect(BluetoothDevice device) {
        connectBluetooth(device);
    }

    private void connectBluetooth(BluetoothDevice device) {
        if (device != null) {
            pd.dismiss();
            pd = ProgressDialog.show(context, "connecting", "正在连接蓝牙");
            pd.setCancelable(true);
            if (device.getName() != null) {
                System.out.println("vik...scandevice...." + device.getName());
                System.out.print("getBluetooth ..." + device.getName());
                mDeviceName = device.getName();
                mDeviceAddress = device.getAddress();
              /*  if (mScanning) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }*/

                System.out.println("vik...address..." + mDeviceAddress);
                Intent gattServiceIntent = new Intent(context,
                        BluetoothLeService.class);
                context.bindService(gattServiceIntent, mServiceConnection,
                        Context.BIND_AUTO_CREATE);
                System.out.println("vik...after bind service");
                context.registerReceiver(mGattUpdateReceiver,
                        makeGattUpdateIntentFilter());
                if (mBluetoothLeService != null) {
                    final boolean result = mBluetoothLeService
                            .connect(mDeviceAddress);
                    System.out.println("vik..result" + result);
                    if (result == false) {
                        System.out
                                .println("vik..mBluetoothLeService.connect(mDeviceAddress)..fail");
                    }
                } else {
                }
            }
        } else {
            return;
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                System.out
                        .println("vik...mGattUpdateReceiver..ACTION_GATT_CONNECTED");
                Toast.makeText(context, "Bluetooth connected",
                        Toast.LENGTH_SHORT).show();
                bluetoothConnected = true;
                pd.dismiss();
                isConnecting = false;
                BluetoothLeService.setIsStart(true);
                if (listener != null) {
                    listener.connectSuccess();
                }

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) {
                Toast.makeText(context, "Bluetooth disconnected",
                        Toast.LENGTH_SHORT).show();
                bluetoothConnected = false;
                // pd.dismiss();
                isConnecting = false;

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                System.out.println("vik...ACTION_GATT_SERVICES_DISCOVERED");
                connectGattServices(mBluetoothLeService
                        .getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

            }
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            System.out.println("vik..mBluetoothLeService.onServiceConnected");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                // finish();
                return;
            }
            System.out.println("vik..mBluetoothLeService.connect.."
                    + mDeviceAddress);
            // 自动连接到装置上成功启动初始化。
            mBluetoothLeService.connect(mDeviceAddress);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            System.out.println("vik...onServiceDisconnected");
            mBluetoothLeService = null;
        }
    };

    private void connectGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid = null;
        String unknownServiceString = "unknown service";
        String unknownCharaString = "Unknown characteristic";
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();


        // GATT协议循环服务提供
        for (BluetoothGattService gattService : gattServices) {

            uuid = gattService.getUuid().toString();

            if (uuid.equals(CONNECT_UUID_LIST_YELLOW)
                    || uuid.equals(CONNECT_UUID_LIST_BLUE)) {// 对只连接指定UUID的设备所添加的

                System.out.println("vik..uuid:" + uuid + "\n");
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                        .getCharacteristics();

                // 循环通过可用的特性。
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    uuid = gattCharacteristic.getUuid().toString();
                    System.out.println("vik...readCharacteristic..uuid..."
                            + uuid);
                    if (uuid.equals(CONNECT_UUID_YELLOW)
                            || uuid.equals(CONNECT_UUID_BLUE)) {
                        mBluetoothLeService
                                .readCharacteristic(gattCharacteristic);
                        mBluetoothLeService.setCharacteristicNotification(
                                gattCharacteristic, true);
                        connectedCharacteristic = gattCharacteristic;
                        pd.dismiss();
                    }
                }
            }
        }
    }

    public void close() {
        if (isRuning) {
            scanLeDevice(false);
            mHandler = null;
            isFoundDevice = false;
            try {
                context.unbindService(mServiceConnection);
                mBluetoothLeService = null;
                context.unregisterReceiver(mGattUpdateReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
            MyBluetoothLE.connectedCharacteristic = null;
            isRuning = false;
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = activity.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
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
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view
                        .findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view
                        .findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName.substring(4,
                        deviceName.length() - 1));
            } else
                viewHolder.deviceName.setText("未知设备");
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }

    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    public static void sendBytesByBluetooth(final byte[] array, final int delayms) {
        if (array == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delayms);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (MyBluetoothLE.connectedCharacteristic == null || BluetoothLeService.mBluetoothGatt == null)
                    ;

                MyBluetoothLE.connectedCharacteristic.setValue(array);
                MyBluetoothLE.connectedCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                boolean b = BluetoothLeService.mBluetoothGatt.writeCharacteristic(MyBluetoothLE.connectedCharacteristic);
            }

        }).start();
    }
}
