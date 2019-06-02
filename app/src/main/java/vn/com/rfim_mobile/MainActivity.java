package vn.com.rfim_mobile;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import vn.com.rfim_mobile.utils.BluetoothUtil;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private BluetoothAdapter mBTAdapter;
    private BluetoothUtil mBTUtil;
    private Button btnShelfRegister;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        mBTUtil.connectBluetoothDevice();
        mBTUtil.readBluetoothSerialData();

        btnShelfRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShelfRegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    public void initView() {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
        mBTUtil = new BluetoothUtil(mBTAdapter);
        btnShelfRegister = findViewById(R.id.btn_shelf_register);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
//        mBTUtil.closeBluetoothSocket();
    }

    private void checkBTState() {
        if (mBTAdapter == null) {
            Log.e(TAG, "checkBTState: " + "Bluetooth not support");
        } else {
            if (mBTAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }
}
