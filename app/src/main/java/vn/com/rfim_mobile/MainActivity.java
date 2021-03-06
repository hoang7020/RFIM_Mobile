package vn.com.rfim_mobile;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
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
import vn.com.rfim_mobile.receiver.BluetoothReceiver;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;
import vn.com.rfim_mobile.utils.NetworkUtil;
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
            btnFindPackage,
            btnTransferProduct,
            btnStocktakeInventory;
    private Toolbar mToolbar;
    private BluetoothFragment fragment;
    private BluetoothReceiver mBluetoothReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            initView();

            informBluetoothStatus();

            requestPermission();

            btnRegisterShelf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(MainActivity.this, RegisterShelfActivity.class);
                        startActivity(intent);
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                    }
                }
            });

            btnRegisterPackage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(MainActivity.this, RegisterPackageActivity.class);
//                Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
                        startActivity(intent);
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                    }
                }
            });

            btnStockInPackage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(MainActivity.this, StockInActivity.class);
                        startActivity(intent);
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                    }
                }
            });

            btnStockOutPackage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(MainActivity.this, StockOutActivity.class);
                        startActivity(intent);
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                    }
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
                    try {
                        Intent intent = new Intent(MainActivity.this, TransferProductActivity.class);
                        startActivity(intent);
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                    }
                }
            });

            btnStocktakeInventory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(MainActivity.this, StocktakeInventoryActivity.class);
                        startActivity(intent);
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                    }
                }
            });

            btnFindPackage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(MainActivity.this, FindPackageActivity.class);
                        startActivity(intent);
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                    }
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String address = PreferenceUtil.getInstance(context).getStringValue("BLUETOOTH_ADDRESS", "");
                        if (!address.equals("")) {
                            mBTUtil.connectBluetoothDevice(address);
                            mBTUtil.readBluetoothSerialData();
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                    }
                }
            }).start();
        } catch (Exception ex) {
            Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
        }
    }

    public void initView() {
        context = this.getApplicationContext();
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
        mBluetoothReceiver = new BluetoothReceiver();
        mBTUtil = new BluetoothUtil(mBTAdapter);
        btnRegisterShelf = findViewById(R.id.btn_shelf_register);
        btnRegisterPackage = findViewById(R.id.btn_package_register);
        btnStockInPackage = findViewById(R.id.btn_stock_in_package);
        btnStockOutPackage = findViewById(R.id.btn_stock_out_package);
//        btnClearRfidTag = findViewById(R.id.btn_clear_rfid_tag);
        btnTransferProduct = findViewById(R.id.btn_transfer_product);
        btnStocktakeInventory = findViewById(R.id.btn_stocktake_inventory);
        btnFindPackage = findViewById(R.id.btn_find_package);
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
                PreferenceUtil.getInstance(this).putStringValue("username", "");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBTUtil.closeBluetoothSocket();
        unregisterReceiver(mBluetoothReceiver);
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
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
    }

    @Override
    public void onDissmissBluetoothDialog() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String address = PreferenceUtil.getInstance(context).getStringValue("BLUETOOTH_ADDRESS", "");
                if (!address.equals("")) {
                    mBTUtil.connectBluetoothDevice(address);
                    mBTUtil.readBluetoothSerialData();
                }
            }
        }).start();
    }

    public void informBluetoothStatus() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mBluetoothReceiver, filter);
    }
}
