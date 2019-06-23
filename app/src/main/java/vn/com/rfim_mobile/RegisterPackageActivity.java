package vn.com.rfim_mobile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.google.gson.Gson;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.json.ObjectResult;
import vn.com.rfim_mobile.models.json.Product;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class RegisterPackageActivity extends AppCompatActivity implements Observer, OnTaskCompleted {

    public static final String TAG = RegisterPackageActivity.class.getSimpleName();

    private List<String> mListScanRfid;
    private Button btnScanProductRfid, btnScanShelfRfid, btnSave, btnClear, btnCancel;
    private TextView txtBoxRfid, txtPackageRfid, txtProductName, txtProductDescription;
    private SmartMaterialSpinner snProductId;
    private StringBuffer sb;
    private List<Product> mProducts;
    private List<String> mListProductId;
    private RFIMApi mRfimApi;
    private String mProductId;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_package);

        initView();

        mRfimApi.getAllProduct();

        btnScanProductRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtil.tempScanResult.registerObserver(RegisterPackageActivity.this, Constant.SCAN_BOX_RFID);
            }
        });

        btnScanShelfRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtil.tempScanResult.registerObserver(RegisterPackageActivity.this, Constant.SCAN_PACKAGE_RFID);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mProductId.equals("")) {
                    Toast.makeText(RegisterPackageActivity.this, getText(R.string.not_choose_product), Toast.LENGTH_SHORT).show();
                } else if (txtPackageRfid.getText().toString().equals("")) {
                    Toast.makeText(RegisterPackageActivity.this, getText(R.string.not_scan_product_rfid), Toast.LENGTH_SHORT).show();
                } else {
                    mRfimApi.registerPackageAndBox(txtPackageRfid.getText().toString(), mProductId, mListScanRfid);
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListScanRfid.clear();
                snProductId.setSelection(0);
                txtPackageRfid.setText("");
                txtBoxRfid.setText("");
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        snProductId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtProductName.setText(mProducts.get(position).getProductName());
                mProductId = mListProductId.get(position);
                txtProductDescription.setText(mProducts.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initView() {
        btnScanProductRfid = findViewById(R.id.btn_scan_product_rfid);
        btnScanShelfRfid = findViewById(R.id.btn_scan_shelf_rfid);
        btnSave = findViewById(R.id.btn_save);
        btnClear = findViewById(R.id.btn_clear);
        btnCancel = findViewById(R.id.btn_cancel);
        txtBoxRfid = findViewById(R.id.txt_box_rfid);
        txtPackageRfid = findViewById(R.id.txt_package_rfid);
        txtProductName = findViewById(R.id.txt_product_name);
        txtProductDescription = findViewById(R.id.txt_product_description);
        snProductId = findViewById(R.id.sn_product_id);
        mListScanRfid = new ArrayList<>();
        sb = new StringBuffer();
        gson = new Gson();
        mRfimApi = new RFIMApi(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothUtil.tempScanResult.unregisterObserver(RegisterPackageActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void getNotification(String message, int type) {
        switch (type) {
            case Constant.SCAN_BOX_RFID:
                if (mListScanRfid.isEmpty()) {
                    mListScanRfid.add(BluetoothUtil.tempScanResult.getRfidID());
                } else {
                    if (mListScanRfid.indexOf(BluetoothUtil.tempScanResult.getRfidID()) < 0) {
                        mListScanRfid.add(BluetoothUtil.tempScanResult.getRfidID());
                    }
                }
                for (String s : mListScanRfid) {
                    sb.append(s + "\n");
                }
                Log.e(TAG, "getNotification: " + sb.toString());
                txtBoxRfid.setText(sb.toString());
                sb.delete(0, sb.length());
                break;
            case Constant.SCAN_PACKAGE_RFID:
                txtPackageRfid.setText(BluetoothUtil.tempScanResult.getRfidID());
                BluetoothUtil.tempScanResult.unregisterObserver(RegisterPackageActivity.this);
                break;
        }
    }

    @Override
    public void onTaskCompleted(String data, int type, int code) {
        Log.e(TAG, "onTaskCompleted: " + data);
        ObjectResult result = gson.fromJson(data, ObjectResult.class);
        switch (type) {
            case Constant.GET_ALL_PRODUCTS:
                mListProductId = new ArrayList<>();
                if (code == HttpURLConnection.HTTP_OK) {
                    mProducts = result.getData().getProducts();
                    for (Product p: mProducts) {
                        mListProductId.add(p.getProductId());
                    }
                }
                snProductId.setItem(mListProductId);
                break;
            case Constant.REGISTER_PACKAGE_AND_BOX:
                if (code == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    txtPackageRfid.setText("");
                    txtBoxRfid.setText("");
                    mListScanRfid.clear();
                }
                break;
        }
    }
}
