package vn.com.rfim_mobile;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.airbnb.lottie.LottieAnimationView;
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
import vn.com.rfim_mobile.models.json.Package;
import vn.com.rfim_mobile.models.json.Product;
import vn.com.rfim_mobile.models.json.ResponseMessage;
import vn.com.rfim_mobile.receiver.BluetoothReceiver;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;
import vn.com.rfim_mobile.utils.NetworkUtil;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class RegisterPackageActivity extends AppCompatActivity implements Observer, OnTaskCompleted {

    public static final String TAG = RegisterPackageActivity.class.getSimpleName();

    private List<String> mListScanRfid;
    private Button
            btnSave,
            btnClear,
            btnCancel;
    private LinearLayout btnScanPackageRfid, btnScanProductRfid;
    private ImageButton btnReceipt;
    private TextView txtBoxRfid,
            txtPackageRfid,
            txtProductId;
    //            txtProductDescription;
    private SmartMaterialSpinner snReceipt,
            snProductName;
    private StringBuffer sb;
    private RFIMApi mRfimApi;
    private String mSelectedProductId;
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
    private List<String> mListProductName;

    private boolean isScanningPackageRfid = false;
    private boolean isScanningBoxRfid = false;
    private LottieAnimationView lavScanningPackageRfid, lavScanningBoxRfid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_package);

        initView();

        mRfimApi.getReceiptInvoice();
//        mRfimApi.getAllProduct();


        snReceipt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mListShowReceipt.clear();
                mListProductName.clear();
                mCurrentReceiptId = mListInvoiceId.get(position);
                for (Invoice i : mInvoices) {
                    if (mCurrentReceiptId.equals(i.getInvoiceId())) {
                        InvoiceInfoItem item = new InvoiceInfoItem(i.getProductId(), i.getProductName(), i.getQuantity(), i.getProcessQuantity(), i.getStatus());
                        mListShowReceipt.add(item);
                        mCurrentReceiptStatus = i.getStatus();
                        mListProductName.add(i.getProductName());
                    }
                }
                snProductName.setItem(mListProductName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        snProductName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                txtProductName.setText(mProducts.get(position).getProductName());
                mSelectedProductId = mListShowReceipt.get(position).getProductId();
                txtProductId.setText(mListShowReceipt.get(position).getProductId());
//                txtProductDescription.setText(mProducts.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnScanProductRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BluetoothUtil.isConnected) {
                    Toast.makeText(RegisterPackageActivity.this, getString(R.string.no_bluetooth_connection), Toast.LENGTH_SHORT).show();
                } else {
                    isScanningBoxRfid = !isScanningBoxRfid;
                    if (isScanningBoxRfid) {
                        startAmination(lavScanningBoxRfid);
                        if (isScanningPackageRfid) {
                            stopAmination(lavScanningPackageRfid);
                            isScanningPackageRfid = false;
                        }
                        BluetoothUtil.tempScanResult.registerObserver(RegisterPackageActivity.this, Constant.SCAN_BOX_RFID);
                    } else {
                        stopAmination(lavScanningBoxRfid);
                        BluetoothUtil.tempScanResult.unregisterObserver(RegisterPackageActivity.this);
                    }
                }
            }
        });

        btnScanPackageRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BluetoothUtil.isConnected) {
                    Toast.makeText(RegisterPackageActivity.this, getString(R.string.no_bluetooth_connection), Toast.LENGTH_SHORT).show();
                } else {
                    isScanningPackageRfid = !isScanningPackageRfid;
                    if (isScanningPackageRfid) {
                        startAmination(lavScanningPackageRfid);
                        if (isScanningBoxRfid) {
                            stopAmination(lavScanningBoxRfid);
                            isScanningBoxRfid = false;
                        }
                        BluetoothUtil.tempScanResult.registerObserver(RegisterPackageActivity.this, Constant.SCAN_PACKAGE_RFID);
                    } else {
                        stopAmination(lavScanningPackageRfid);
                        BluetoothUtil.tempScanResult.unregisterObserver(RegisterPackageActivity.this);
                    }
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isOnline(RegisterPackageActivity.this)) {
                    Toast.makeText(RegisterPackageActivity.this, getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                } else {
                    if (mSelectedProductId != null) {
                        if (mSelectedProductId.equals("")) {
                            Toast.makeText(RegisterPackageActivity.this, getText(R.string.not_choose_product), Toast.LENGTH_SHORT).show();
                        } else if (txtPackageRfid.getText().toString().equals("")) {
                            Toast.makeText(RegisterPackageActivity.this, getText(R.string.not_scan_package_rfid), Toast.LENGTH_SHORT).show();
                        } else if (mListScanRfid.isEmpty()) {
                            Toast.makeText(RegisterPackageActivity.this, getText(R.string.not_scan_product_rfid), Toast.LENGTH_SHORT).show();
                        } else {
                            mCurrentReceiptPossition = mListInvoiceId.indexOf(mCurrentReceiptId);
//                    mRfimApi.registerPackageAndBox(txtPackageRfid.getText().toString(), mCurrentReceiptId, mProductId, mCurrentReceiptStatus, mListScanRfid);
                            mRfimApi.registerPackageAndBox(txtPackageRfid.getText().toString(),
                                    mCurrentReceiptId,
                                    txtProductId.getText().toString(),
                                    mCurrentReceiptStatus,
                                    mListScanRfid,
                                    System.currentTimeMillis());
                        }
                    } else {
                        Toast.makeText(RegisterPackageActivity.this, getString(R.string.not_choose_product), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListScanRfid.clear();
                snProductName.setSelection(0);
                txtPackageRfid.setText("");
                txtBoxRfid.setText("");
                stopAmination(lavScanningBoxRfid);
                stopAmination(lavScanningPackageRfid);
                isScanningBoxRfid = false;
                isScanningPackageRfid = false;
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
        btnScanPackageRfid = findViewById(R.id.btn_scan_package_rfid);
        btnSave = findViewById(R.id.btn_save);
        btnClear = findViewById(R.id.btn_clear);
        btnCancel = findViewById(R.id.btn_cancel);
        btnReceipt = findViewById(R.id.btn_receipt);
        txtBoxRfid = findViewById(R.id.txt_box_rfid);
        txtPackageRfid = findViewById(R.id.txt_package_rfid);
        txtProductId = findViewById(R.id.txt_product_id);
//        txtProductDescription = findViewById(R.id.txt_product_description);
        snReceipt = findViewById(R.id.sn_receipt);
        snProductName = findViewById(R.id.sn_product_name);
        mInvoices = new ArrayList<>();
        mListInvoiceId = new ArrayList<>();
        mListScanRfid = new ArrayList<>();
        mListShowReceipt = new ArrayList<>();
        mListProductId = new ArrayList<>();
        mListProductName = new ArrayList<>();
        sb = new StringBuffer();
        gson = new Gson();
        mRfimApi = new RFIMApi(this);
        mBeepSound = MediaPlayer.create(this, R.raw.beep);
        mCurrentReceiptPossition = 0;
        lavScanningPackageRfid = findViewById(R.id.lav_scanning_package_rfid);
        lavScanningPackageRfid.setAnimation(R.raw.scan);
        lavScanningPackageRfid.loop(true);
        lavScanningBoxRfid = findViewById(R.id.lav_scanning_box_rfid);
        lavScanningBoxRfid.setAnimation(R.raw.scan);
        lavScanningBoxRfid.loop(true);
    }


    public void startAmination(LottieAnimationView lav) {
        lav.playAnimation();
    }

    public void stopAmination(LottieAnimationView lav) {
        lav.pauseAnimation();
        lav.setProgress(0);
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

    //All observer notification will return at here
    @Override
    public void getNotification(int type) {
        mBeepSound.start();
        switch (type) {
            case Constant.SCAN_BOX_RFID:
                InvoiceInfoItem receipt = findInvoiceInfoItem(mSelectedProductId);
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
                mRfimApi.getPackageByPackageRfid(BluetoothUtil.tempScanResult.getRfidID());
                BluetoothUtil.tempScanResult.unregisterObserver(RegisterPackageActivity.this);
                stopAmination(lavScanningPackageRfid);
                isScanningPackageRfid = false;
                break;
        }
    }

    //All api result will return at here
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
//                            if (mListInvoiceId.isEmpty()) {
//                                mListInvoiceId.add(i.getInvoiceId());
//                            } else {
//                                if (!mListInvoiceId.contains(i.getInvoiceId())) {
//                                    mListInvoiceId.add(i.getInvoiceId());
//                                }
//                            }
                            if (!mListInvoiceId.contains(i.getInvoiceId())) {
                                mListInvoiceId.add(i.getInvoiceId());
                            }
                        }
                    } else {
                        mListInvoiceId.add("None!");
                    }
                }
                snReceipt.setItem(mListInvoiceId);
                snReceipt.setSelection(mCurrentReceiptPossition);
                break;
//            case Constant.GET_ALL_PRODUCTS:
//                if (code == HttpURLConnection.HTTP_OK) {
//                    mProducts = gson.fromJson(data, new TypeToken<List<Product>>() {
//                    }.getType());
//                    for (Product p : mProducts) {
////                        mListProductId.add(p.getProductId());
//                        mListProductName.add(p.getProductName());
//                    }
//                }
//                snProductName.setItem(mListProductName);
//                break;
            case Constant.GET_PACKAGE_BY_PACKAGE_RFID:
                if (code == HttpURLConnection.HTTP_OK) {
                    Package pac = gson.fromJson(data, Package.class);
                    if (!pac.getProductId().equals(mSelectedProductId)) {
                        Toast.makeText(this, "Package already register with product " + pac.getProductId(), Toast.LENGTH_SHORT).show();
                    } else {
                        txtPackageRfid.setText(BluetoothUtil.tempScanResult.getRfidID());
                    }
                } else {
                    txtPackageRfid.setText(BluetoothUtil.tempScanResult.getRfidID());
                }
                break;
            case Constant.REGISTER_PACKAGE_AND_BOX:
                if (code == HttpURLConnection.HTTP_OK) {
                    showResponseMessage(data);
                    txtPackageRfid.setText("");
                    txtBoxRfid.setText("");
                    mListScanRfid.clear();
                    mRfimApi.getReceiptInvoice();
                    stopAmination(lavScanningBoxRfid);
                    isScanningBoxRfid = false;
                } else {
                    Toast.makeText(this, "Register Fail", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //find invoice info in list that show in recyclerview
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
