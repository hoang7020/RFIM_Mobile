package vn.com.rfim_mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

public class PackageRegisterActivity extends AppCompatActivity implements Observer {

    public static final String TAG = PackageRegisterActivity.class.getSimpleName();

    private ArrayList<String> listScanRfid;
    private Button btnScanProductRfid;
    private TextView txtProductRfid;
    private StringBuffer sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_register);
        
        initView();

        btnScanProductRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtil.tempScanResult.registerObserver(PackageRegisterActivity.this);
            }
        });
    }

    private void initView() {
        btnScanProductRfid = findViewById(R.id.btn_scan_product_rfid);
        txtProductRfid = findViewById(R.id.txt_product_rfid);
        listScanRfid = new ArrayList<>();
        sb = new StringBuffer();
    }

    @Override
    public void getNotification(String message) {
        Log.e(TAG, "getNotification: " + "Scan Result Change");
        if (listScanRfid.isEmpty()) {
            listScanRfid.add(BluetoothUtil.tempScanResult.getRfidID());
        } else {
            if (listScanRfid.indexOf(BluetoothUtil.tempScanResult.getRfidID()) < 0) {
                listScanRfid.add(BluetoothUtil.tempScanResult.getRfidID());
            }
        }
        for (String s: listScanRfid) {
            sb.append(s + "\n");
        }
        Log.e(TAG, "getNotification: " + sb.toString());
        txtProductRfid.setText(sb.toString());
        sb.delete(0, sb.length());
    }
}
