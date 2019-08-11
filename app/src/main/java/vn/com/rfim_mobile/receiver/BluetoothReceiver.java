package vn.com.rfim_mobile.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

public class BluetoothReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            BluetoothUtil.isConnected = true;
            Toast.makeText(context, "RFID Scanner device is connected.", Toast.LENGTH_SHORT).show();
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            BluetoothUtil.isConnected = false;
            Toast.makeText(context, "RFID scanner device is disconnected.", Toast.LENGTH_SHORT).show();
        }
    }
}
