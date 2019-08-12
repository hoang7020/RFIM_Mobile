package vn.com.rfim_mobile.utils.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import vn.com.rfim_mobile.constants.Constant;

public class BluetoothUtil {

    public static final String TAG = BluetoothUtil.class.getSimpleName();

    public static TempScanResult tempScanResult = new TempScanResult();
    public static boolean isConnected = false;

    private BluetoothAdapter mBTAdapter;
    private BluetoothSocket mBTSocket;
    private Handler mBTHanler;
    private StringBuffer sb;

    public BluetoothUtil(BluetoothAdapter adapter) {
        mBTAdapter = adapter;
        mBTHanler = new Handler();
        sb = new StringBuffer();
    }

    //Create Bluetooth socket
//    public BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
//        if (Build.VERSION.SDK_INT >= 10) {
//            try {
//                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[]{UUID.class});
//                return (BluetoothSocket) m.invoke(device, Constant.MY_UUID);
//            } catch (Exception e) {
//                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
//            }
//        }
//        return device.createRfcommSocketToServiceRecord(Constant.MY_UUID);
//    }

    //Create connection to the Bluetooth device
    public void connectBluetoothDevice(String address) {
        BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
        try {
            mBTSocket = device.createRfcommSocketToServiceRecord(Constant.MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "connectBluetoothDevice: " + e.getMessage());
        }
        mBTAdapter.cancelDiscovery();
        try {
            mBTSocket.connect();
            if (mBTSocket.isConnected()) {
                isConnected = true;
            }
            Log.d(TAG, "....Connection ok...");
        } catch (IOException e) {
            try {
                mBTSocket.close();
            } catch (IOException e2) {
                Log.e(TAG, "connectBluetoothDevice: " + e2.getMessage());
            }
        }

    }

    //Read serial data from bluetooth device
    public void readBluetoothSerialData() {
        if (mBTSocket != null) {
            ConnectedThread thread = new ConnectedThread(mBTSocket);
            thread.start();
        }
    }

    //Disconnect bluetooth device
    public void closeBluetoothSocket() {
        if (mBTSocket != null) {
            try {
                mBTSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "closeBluetoothSocket: " + e.getMessage());
            }
        }
    }

    //Thread handle read serial data from bluetooth device
    public class ConnectedThread extends Thread {
        private final InputStream is;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIs = null;
            try {
                tmpIs = socket.getInputStream();
            } catch (IOException e) {
            }
            is = tmpIs;
        }

        public void run() {
            try {
                while (true) {
                    int byteCount = is.available();
                    if (byteCount > 0) {
                        byte[] rawBytes = new byte[byteCount];
                        is.read(rawBytes);
                        final String tmpData = new String(rawBytes, "UTF-8");
                        mBTHanler.post(new Runnable() {
                            @Override
                            public void run() {
                                sb.append(tmpData);
                                int endOfLineIndex = sb.indexOf("\r\n");
                                if (endOfLineIndex > 0) {
                                    String data = sb.substring(0, endOfLineIndex);
                                    sb.delete(0, sb.length());
                                    Log.e(TAG, "run: " + data);
                                    tempScanResult.setRfidID(data);
                                }
                            }
                        });
                    }
                }

            } catch (IOException e) {
                Log.e(TAG, "ConnectedThread: " + e.getMessage());
            }
        }
    }
}
