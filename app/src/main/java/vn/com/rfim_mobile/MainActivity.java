package vn.com.rfim_mobile;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private BluetoothAdapter mBTAdapter;
    private BluetoothUtil mBTUtil;
    private Button btnRegisterShelf,
            btnRegisterPackage,
            btnStockInPackage,
            btnStockOutPackage,
            btnClearRfidTag,
            btnTransferProduct,
            btnStocktakeInventory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        mBTUtil.connectBluetoothDevice();
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

        btnClearRfidTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ClearTagActivity.class);
                startActivity(intent);
            }
        });

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
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
        mBTUtil = new BluetoothUtil(mBTAdapter);
        btnRegisterShelf = findViewById(R.id.btn_shelf_register);
        btnRegisterPackage = findViewById(R.id.btn_package_register);
        btnStockInPackage = findViewById(R.id.btn_stock_in_package);
        btnStockOutPackage = findViewById(R.id.btn_stock_out_package);
        btnClearRfidTag = findViewById(R.id.btn_clear_rfid_tag);
        btnTransferProduct = findViewById(R.id.btn_transfer_product);
        btnStocktakeInventory = findViewById(R.id.btn_transfer_product);
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
