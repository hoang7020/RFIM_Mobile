package vn.com.rfim_mobile;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.fragments.ReceiptInvoiceFragment;
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.InvoiceInfoItem;
import vn.com.rfim_mobile.models.json.Invoice;
import vn.com.rfim_mobile.models.json.Product;
import vn.com.rfim_mobile.models.json.ResponseMessage;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class RegisterPackageActivity extends AppCompatActivity implements Observer, OnTaskCompleted {

    public static final String TAG = RegisterPackageActivity.class.getSimpleName();

    private List<String> mListScanRfid;
    private Button btnScanProductRfid,
            btnScanShelfRfid,
            btnSave,
            btnClear,
            btnCancel;
    private ImageButton btnReceipt;
    private TextView txtBoxRfid,
            txtPackageRfid,
            txtProductName,
            txtProductDescription;
    private SmartMaterialSpinner snReceipt,
            snProductId;
    private StringBuffer sb;
    private RFIMApi mRfimApi;
    private String mProductId;
    private Gson gson;
    private MediaPlayer mBeepSound;

    private String mCurrentReceiptId;
    private int mCurrentReceiptStatus;
    private int mCurrentReceiptPossition;
    private InvoiceInfoItem mCurentReceipt;
    private List<Invoice> mInvoices;
    private List<String> mListInvoiceId;
    private List<Product> mProducts;
    private List<String> mListProductId;
    private List<InvoiceInfoItem> mListShowReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_package);

        initView();

        mRfimApi.getReceiptInvoice();
        mRfimApi.getAllProduct();


        snReceipt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mListShowReceipt.clear();
                mCurrentReceiptId = mListInvoiceId.get(position);
                for (Invoice i : mInvoices) {
                    if (mCurrentReceiptId.equals(i.getInvoiceId())) {
                        InvoiceInfoItem item = new InvoiceInfoItem(i.getProductId(), i.getProductName(), i.getQuantity(), i.getProcessQuantity(), i.getStatus());
                        mListShowReceipt.add(item);
                        mCurrentReceiptStatus = i.getStatus();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                if (mProductId.equals("")) {
                    Toast.makeText(RegisterPackageActivity.this, getText(R.string.not_choose_product), Toast.LENGTH_SHORT).show();
                } else if (txtPackageRfid.getText().toString().equals("")) {
                    Toast.makeText(RegisterPackageActivity.this, getText(R.string.not_scan_product_rfid), Toast.LENGTH_SHORT).show();
                } else {
                    mCurrentReceiptPossition = mListInvoiceId.indexOf(mCurrentReceiptId);
                    mRfimApi.registerPackageAndBox(txtPackageRfid.getText().toString(), mCurrentReceiptId, mProductId, mCurrentReceiptStatus, mListScanRfid);
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

        btnReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("LIST_RECEIPT_ITEM", (Serializable) mListShowReceipt);
                ReceiptInvoiceFragment fragment = new ReceiptInvoiceFragment();
                fragment.setArguments(bundle);
                fragment.show(getFragmentManager(), "RECEIPT");
            }
        });


    }

    private void initView() {
        btnScanProductRfid = findViewById(R.id.btn_scan_product_rfid);
        btnScanShelfRfid = findViewById(R.id.btn_scan_shelf_rfid);
        btnSave = findViewById(R.id.btn_save);
        btnClear = findViewById(R.id.btn_clear);
        btnCancel = findViewById(R.id.btn_cancel);
        btnReceipt = findViewById(R.id.btn_receipt);
        txtBoxRfid = findViewById(R.id.txt_box_rfid);
        txtPackageRfid = findViewById(R.id.txt_package_rfid);
        txtProductName = findViewById(R.id.txt_product_name);
        txtProductDescription = findViewById(R.id.txt_product_description);
        snReceipt = findViewById(R.id.sn_receipt);
        snProductId = findViewById(R.id.sn_product_id);
        mInvoices = new ArrayList<>();
        mListInvoiceId = new ArrayList<>();
        mListScanRfid = new ArrayList<>();
        mListShowReceipt = new ArrayList<>();
        mListProductId = new ArrayList<>();
        sb = new StringBuffer();
        gson = new Gson();
        mRfimApi = new RFIMApi(this);
        mBeepSound = MediaPlayer.create(this, R.raw.beep);
        mCurrentReceiptPossition = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothUtil.tempScanResult.unregisterObserver(RegisterPackageActivity.this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BluetoothUtil.tempScanResult.unregisterObserver(RegisterPackageActivity.this);
        finish();
    }

    @Override
    public void getNotification(int type) {
        mBeepSound.start();
        switch (type) {
            case Constant.SCAN_BOX_RFID:
                InvoiceInfoItem receipt = findInvoiceInfoItem(mProductId);
                if (receipt != null) {
                    if ((receipt.getProcessQuantity() + mListScanRfid.size()) < receipt.getQuantity()) {
                        if (mListScanRfid.indexOf(BluetoothUtil.tempScanResult.getRfidID()) < 0) {
                            mListScanRfid.add(BluetoothUtil.tempScanResult.getRfidID());
                        }
                        for (String s : mListScanRfid) {
                            sb.append(s + "\n");
                        }
                        Log.e(TAG, "getNotification: " + sb.toString());
                        txtBoxRfid.setText(sb.toString());
                        sb.delete(0, sb.length());
                    } else {
                        Toast.makeText(this, "Enough Product", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Select wrong product id for this Receipt", Toast.LENGTH_SHORT).show();
                }
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
        switch (type) {
            case Constant.GET_RECEIPT_INVOICE:
                if (code == HttpURLConnection.HTTP_OK) {
                    mInvoices = gson.fromJson(data, new TypeToken<List<Invoice>>() {
                    }.getType());
                    if (mInvoices.size() > 0) {
                        for (Invoice i : mInvoices) {
                            if (mListInvoiceId.isEmpty()) {
                                mListInvoiceId.add(i.getInvoiceId());
                            } else {
                                if (!mListInvoiceId.contains(i.getInvoiceId())) {
                                    mListInvoiceId.add(i.getInvoiceId());
                                }
                            }
                        }
                    } else {
                        mListInvoiceId.add("None!");
                    }
                }
                snReceipt.setItem(mListInvoiceId);
                snReceipt.setSelection(mCurrentReceiptPossition);
                break;
            case Constant.GET_ALL_PRODUCTS:
                if (code == HttpURLConnection.HTTP_OK) {
                    mProducts = gson.fromJson(data, new TypeToken<List<Product>>() {
                    }.getType());
                    for (Product p : mProducts) {
                        mListProductId.add(p.getProductId());
                    }
                }
                snProductId.setItem(mListProductId);
                break;
            case Constant.REGISTER_PACKAGE_AND_BOX:
                if (code == HttpURLConnection.HTTP_OK) {
                    showResponseMessage(data);
                    txtPackageRfid.setText("");
                    txtBoxRfid.setText("");
                    mListScanRfid.clear();
                    mRfimApi.getReceiptInvoice();
                } else {
                    Toast.makeText(this, "Register Fail", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public InvoiceInfoItem findInvoiceInfoItem(String productId) {
        for (InvoiceInfoItem i : mListShowReceipt) {
            if (i.getProductId().equals(productId)) {
                return i;
            }
        }
        return null;
    }

    //Show response message
    public void showResponseMessage(String data) {
        ResponseMessage message = gson.fromJson(data, ResponseMessage.class);
        if (message != null) {
            Toast.makeText(this, message.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
