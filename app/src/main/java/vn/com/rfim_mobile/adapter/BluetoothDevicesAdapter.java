package vn.com.rfim_mobile.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.interfaces.RecyclerViewClickListener;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;
import vn.com.rfim_mobile.utils.Bluetooth.DeviceInfo;
import vn.com.rfim_mobile.utils.PreferenceUtil;

import java.util.List;

public class BluetoothDevicesAdapter extends RecyclerView.Adapter<BluetoothDevicesAdapter.RecyclerViewHolder>{

    public static final String TAG = BluetoothDevice.class.getSimpleName();

    private List<DeviceInfo> devices;
    private RecyclerViewClickListener listener;
    private BluetoothUtil mBTUtil;

    public BluetoothDevicesAdapter(List<DeviceInfo> devices, RecyclerViewClickListener listener, BluetoothUtil mBTUtil) {
        this.devices = devices;
        this.listener = listener;
        this.mBTUtil = mBTUtil;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_bluetooth_devices_layout, viewGroup, false);
        return new BluetoothDevicesAdapter.RecyclerViewHolder(view, view.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int position) {
        DeviceInfo device = devices.get(position);
        String name = device.getName();
        if (name == null) {
            recyclerViewHolder.tvDeviceName.setText("No Name");
        } else {
            recyclerViewHolder.tvDeviceName.setText(device.getName());
        }
        recyclerViewHolder.tvDeviceAddress.setText(device.getAddress());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvDeviceName, tvDeviceAddress;
        private Context context;

        public RecyclerViewHolder(View itemView, Context context) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tv_device_name);
            tvDeviceAddress = itemView.findViewById(R.id.tv_device_address);
            this.context = context;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            PreferenceUtil.getInstance(context).putStringValue("BLUETOOTH_ADDRESS", tvDeviceAddress.getText().toString());
            listener.recyclerViewListClicked(v, this.getLayoutPosition());
        }
    }
}
