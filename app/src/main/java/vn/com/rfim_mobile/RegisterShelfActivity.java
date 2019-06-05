package vn.com.rfim_mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

public class RegisterShelfActivity extends AppCompatActivity implements Observer {

    public static final String TAG = RegisterShelfActivity.class.getSimpleName();
    private TextView txtShelfRfid;
    private Button btnScanShelfRfid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_shelf);

        initView();

        btnScanShelfRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtil.tempScanResult.registerObserver(RegisterShelfActivity.this, Constant.SCAN_SHELF_RFID);
            }
        });

    }

    private void initView() {
        txtShelfRfid = findViewById(R.id.txt_shelf_rfid);
        btnScanShelfRfid = findViewById(R.id.btn_scan_shelf_rfid);
    }

    //get notification from Observerable(TempScanResult) and unregister Observer
    @Override
    public void getNotification(String message, int type) {
        if (type == Constant.SCAN_SHELF_RFID) {
            Log.e(TAG, "getNotification: " + message);
            txtShelfRfid.setText(BluetoothUtil.tempScanResult.getRfidID());
            BluetoothUtil.tempScanResult.unregisterObserver(RegisterShelfActivity.this);
        }
    }
}