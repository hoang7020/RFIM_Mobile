package vn.com.rfim_mobile;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import android.widget.Toast;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.fragments.BluetoothFragment;
import vn.com.rfim_mobile.interfaces.OnDissmissBluetoothDialogListener;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;
import vn.com.rfim_mobile.utils.PreferenceUtil;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements OnDissmissBluetoothDialogListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static Context context;

    private BluetoothAdapter mBTAdapter;
    private BluetoothUtil mBTUtil;
    private LinearLayout btnRegisterShelf,
            btnRegisterPackage,
            btnStockInPackage,
            btnStockOutPackage,
//            btnClearRfidTag,
            btnTransferProduct,
            btnStocktakeInventory;
    private Toolbar mToolbar;
    private BluetoothFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        requestPermission();
        String address = PreferenceUtil.getInstance(context).getStringValue("BLUETOOTH_ADDRESS","");
        mBTUtil.connectBluetoothDevice(Constant.address);
        mBTUtil.readBluetoothSerialData();

        btnRegisterShelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterShelfActivity.class);
                startActivity(intent);
            }
        });

        btnRegisterPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterPackageActivity.class);
//                Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
                startActivity(intent);
            }
        });

        btnStockInPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StockInActivity.class);
                startActivity(intent);
            }
        });

        btnStockOutPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StockOutActivity.class);
                startActivity(intent);
            }
        });

//        btnClearRfidTag.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, ClearTagActivity.class);
//                startActivity(intent);
//            }
//        });

        btnTransferProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TransferProductActivity.class);
                startActivity(intent);
            }
        });

        btnStocktakeInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StocktakeInventoryActivity.class);
                startActivity(intent);
            }
        });

    }

    public void initView() {
        context = this.getApplicationContext();
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
        mBTUtil = new BluetoothUtil(mBTAdapter);
        btnRegisterShelf = findViewById(R.id.btn_shelf_register);
        btnRegisterPackage = findViewById(R.id.btn_package_register);
        btnStockInPackage = findViewById(R.id.btn_stock_in_package);
        btnStockOutPackage = findViewById(R.id.btn_stock_out_package);
//        btnClearRfidTag = findViewById(R.id.btn_clear_rfid_tag);
        btnTransferProduct = findViewById(R.id.btn_transfer_product);
        btnStocktakeInventory = findViewById(R.id.btn_stocktake_inventory);
        mToolbar = findViewById(R.id.tb_main);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sub_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mn_bluetooth:
                fragment = new BluetoothFragment();
                try {
                    fragment.show(getFragmentManager(), "BLUETOOTH");
                } catch (Exception e) {
                    Log.e(TAG, "onOptionsItemSelected: " + e.getMessage());
                }
                break;
            case R.id.mn_logout:
                break;
        }
        return super.onOptionsItemSelected(item);
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


    //Enable bluetooth on android device
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

    private void requestPermission() {
        int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
    }

    @Override
    public void onDissmissBluetoothDialog() {
        String address = PreferenceUtil.getInstance(context).getStringValue("BLUETOOTH_ADDRESS","");
        mBTUtil.connectBluetoothDevice(address);
        mBTUtil.readBluetoothSerialData();
    }
}
