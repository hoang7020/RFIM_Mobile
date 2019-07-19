package vn.com.rfim_mobile.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import vn.com.rfim_mobile.MainActivity;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.adapter.BluetoothDevicesAdapter;
import vn.com.rfim_mobile.adapter.ReceiptAdapter;
import vn.com.rfim_mobile.interfaces.OnDissmissBluetoothDialogListener;
import vn.com.rfim_mobile.interfaces.RecyclerViewClickListener;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;
import vn.com.rfim_mobile.utils.Bluetooth.DeviceInfo;
import vn.com.rfim_mobile.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

public class BluetoothFragment extends DialogFragment implements RecyclerViewClickListener {

    public static final String TAG = BluetoothFragment.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter;
    BroadcastReceiver mReceiver;
    private List<BluetoothDevice> mDevices;
    private BluetoothDevicesAdapter mBluetoothDevicesAdapter;
    private RecyclerView rcBluetoothDevice;
    private List<DeviceInfo> mDeviceInfos;
    private BluetoothUtil mBTUtil;

    private OnDissmissBluetoothDialogListener listener = (OnDissmissBluetoothDialogListener) getContext();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        initView();

        mBluetoothDevicesAdapter = new BluetoothDevicesAdapter(mDeviceInfos, this, mBTUtil);
        rcBluetoothDevice.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rcBluetoothDevice.setItemAnimator(new DefaultItemAnimator());
        rcBluetoothDevice.setAdapter(mBluetoothDevicesAdapter);


        mBluetoothAdapter.startDiscovery();
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.e("Device Name: ", "device " + deviceName);
                    Log.e("deviceHardwareAddress ", "hard" + deviceHardwareAddress);
                    DeviceInfo info = new DeviceInfo(deviceName, deviceHardwareAddress);
                    mDeviceInfos.add(info);
                    mBluetoothDevicesAdapter = new BluetoothDevicesAdapter(mDeviceInfos, BluetoothFragment.this, mBTUtil);
                    rcBluetoothDevice.setAdapter(mBluetoothDevicesAdapter);
                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, filter);

    }

    void bluetoothScanning(Context context) {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter);
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();

    }

//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//                Log.e("Device Name: " , "device " + deviceName);
//                Log.e("deviceHardwareAddress " , "hard"  + deviceHardwareAddress);
//            }
//        }
//    };

    private void initView() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTUtil = new BluetoothUtil(mBluetoothAdapter);
        rcBluetoothDevice = getView().findViewById(R.id.rc_bluetooth_device);
        mDevices = new ArrayList<>();
        mDeviceInfos = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDissmissBluetoothDialogListener) {
            ((OnDissmissBluetoothDialogListener) activity).onDissmissBluetoothDialog();
        }
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
//        Toast.makeText(getActivity(), PreferenceUtil.getInstance(getContext()).getStringValue("BLUETOOTH_ADDRESS", ""), Toast.LENGTH_SHORT).show();
//        BluetoothUtil.connectBluetoothDevice(PreferenceUtil.getInstance(getContext()).getStringValue("BLUETOOTH_ADDRESS", ""));
        mBluetoothAdapter.cancelDiscovery();
        dismiss();
    }
}
