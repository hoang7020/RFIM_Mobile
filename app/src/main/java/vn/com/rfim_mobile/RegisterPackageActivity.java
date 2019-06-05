package vn.com.rfim_mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

public class RegisterPackageActivity extends AppCompatActivity implements Observer {

    public static final String TAG = RegisterPackageActivity.class.getSimpleName();

    private ArrayList<String> listScanRfid;
    private Button btnScanProductRfid, btnScanShelfRfid;
    private TextView txtProductRfid, txtShelfRfid;
    private StringBuffer sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_package);

        initView();

        btnScanProductRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtil.tempScanResult.registerObserver(RegisterPackageActivity.this, Constant.SCAN_BOX_RFID);
            }
        });

        btnScanShelfRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtil.tempScanResult.registerObserver(RegisterPackageActivity.this, Constant.SCAN_SHELF_RFID);
            }
        });
    }

    private void initView() {
        btnScanProductRfid = findViewById(R.id.btn_scan_product_rfid);
        btnScanShelfRfid = findViewById(R.id.btn_scan_shelf_rfid);
        txtProductRfid = findViewById(R.id.txt_product_rfid);
        txtShelfRfid = findViewById(R.id.txt_shelf_rfid);
        listScanRfid = new ArrayList<>();
        sb = new StringBuffer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothUtil.tempScanResult.unregisterObserver(RegisterPackageActivity.this);
    }

    @Override
    public void getNotification(String message, int type) {
        switch (type) {
            case Constant.SCAN_BOX_RFID:
                Log.e(TAG, "SCAN_BOX_RFID: " + message);
                if (listScanRfid.isEmpty()) {
                    listScanRfid.add(BluetoothUtil.tempScanResult.getRfidID());
                } else {
                    if (listScanRfid.indexOf(BluetoothUtil.tempScanResult.getRfidID()) < 0) {
                        listScanRfid.add(BluetoothUtil.tempScanResult.getRfidID());
                    }
                }
                for (String s : listScanRfid) {
                    sb.append(s + "\n");
                }
                Log.e(TAG, "getNotification: " + sb.toString());
                txtProductRfid.setText(sb.toString());
                sb.delete(0, sb.length());
                break;
            case Constant.SCAN_SHELF_RFID:
                Log.e(TAG, "SCAN_SHELF_RFID: " + message);
                txtShelfRfid.setText(BluetoothUtil.tempScanResult.getRfidID());
                BluetoothUtil.tempScanResult.unregisterObserver(RegisterPackageActivity.this);
                break;
        }
    }
}
