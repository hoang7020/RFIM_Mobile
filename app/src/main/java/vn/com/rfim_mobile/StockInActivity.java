package vn.com.rfim_mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

public class StockInActivity extends AppCompatActivity implements Observer {

    public static final String TAG = StockInActivity.class.getSimpleName();
    TextView txtShelfRfid, txtPackageRfid;
    Button btnScanShelfRfid, btnScanPackageRfid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in);

        initView();

        btnScanShelfRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtil.tempScanResult.registerObserver(StockInActivity.this, Constant.SCAN_SHELF_RFID);
            }
        });

        btnScanPackageRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtil.tempScanResult.registerObserver(StockInActivity.this, Constant.SCAN_PACKAGE_RFID);
            }
        });
    }

    private void initView() {
        txtShelfRfid = findViewById(R.id.txt_shelf_rfid);
        txtPackageRfid = findViewById(R.id.txt_package_rfid);
        btnScanShelfRfid = findViewById(R.id.btn_scan_shelf_rfid);
        btnScanPackageRfid = findViewById(R.id.btn_scan_pakage_rfid);
    }

    @Override
    public void getNotification(String message, int type) {
        switch (type) {
            case Constant.SCAN_SHELF_RFID:
                Log.e(TAG, "SCAN_SHELF_RFID: " + message);
                txtShelfRfid.setText(BluetoothUtil.tempScanResult.getRfidID());
                BluetoothUtil.tempScanResult.unregisterObserver(StockInActivity.this);
                break;
            case Constant.SCAN_PACKAGE_RFID:
                Log.e(TAG, "SCAN_PACKAGE_RFID: " + message);
                txtPackageRfid.setText(BluetoothUtil.tempScanResult.getRfidID());
                BluetoothUtil.tempScanResult.unregisterObserver(StockInActivity.this);
                break;
        }
    }
}
