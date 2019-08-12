package vn.com.rfim_mobile;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.airbnb.lottie.LottieAnimationView;
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import vn.com.rfim_mobile.adapter.IssueAdapter;
import vn.com.rfim_mobile.adapter.ListProductAdapter;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.fragments.IssueInvoiceFragment;
import vn.com.rfim_mobile.fragments.IssueInvoiceFragmentV2;
import vn.com.rfim_mobile.fragments.TransferBoxFragment;
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.InvoiceInfoItem;
import vn.com.rfim_mobile.models.ScannedProductItem;
import vn.com.rfim_mobile.models.json.Invoice;
import vn.com.rfim_mobile.models.json.Product;
import vn.com.rfim_mobile.models.json.ResponseMessage;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;
import vn.com.rfim_mobile.utils.NetworkUtil;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class StockOutActivity extends AppCompatActivity implements Observer, OnTaskCompleted {

    public static final String TAG = StockOutActivity.class.getSimpleName();

    private SmartMaterialSpinner snIssue;
    private RecyclerView rcListStockOutBox;
    //    private RecyclerView rcIssuseInvoice;
    private Button btnSave, btnClear, btnCancel;
    private LinearLayout btnScanBoxRfid;
    private ImageButton btnIssueInvoice;
    private Gson gson;
    private RFIMApi mRfimApi;
    private ListProductAdapter mProductAdapter;
    //    private IssueAdapter mIssueInvoiceAdapter;
    private List<String> mListScanBoxRfid;
    private List<ScannedProductItem> mListShowProduct;
    private List<String> mListInvoiceId;
    private MediaPlayer mBeepSound;
    private String mCurrentInvoiceId;
    private List<Invoice> mInvoices;
    private List<InvoiceInfoItem> mListShowIssue;
    private IssueInvoiceFragment mIssueInvoiceFragment;
    private IssueInvoiceFragmentV2 mIssueInvoiceFragmentV2;
    private boolean isEnough = false;
    private LottieAnimationView lavScanningBoxRfid;
    private boolean isScanningBoxRfid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_out);

        initView();

        mRfimApi.getIssuseInvoice();

        //show list product invoice item
//        mIssueInvoiceAdapter = new IssueAdapter(mListShowInvoice);
//        rcIssuseInvoice.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        rcIssuseInvoice.setItemAnimator(new DefaultItemAnimator());
//        rcIssuseInvoice.setAdapter(mIssueInvoiceAdapter);

        //show list scanned product
        mProductAdapter = new ListProductAdapter(mListShowProduct);
        rcListStockOutBox.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rcListStockOutBox.setItemAnimator(new DefaultItemAnimator());
        rcListStockOutBox.setAdapter(mProductAdapter);

        snIssue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isEnough = true;
                mListShowIssue.clear();
                mCurrentInvoiceId = mListInvoiceId.get(position);
                for (Invoice i : mInvoices) {
                    if (mCurrentInvoiceId.equals(i.getInvoiceId())) {
                        InvoiceInfoItem item = new InvoiceInfoItem(i.getProductId(), i.getProductName(), i.getQuantity(), "");
                        mListShowIssue.add(item);
                    }
                }
//                mIssueInvoiceAdapter = new IssueAdapter(mListShowInvoice);
//                rcIssuseInvoice.setAdapter(mIssueInvoiceAdapter);
                mListShowProduct.clear();
                mProductAdapter = new ListProductAdapter(mListShowProduct);
                rcListStockOutBox.setAdapter(mProductAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnScanBoxRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BluetoothUtil.isConnected) {
                    Toast.makeText(StockOutActivity.this, getString(R.string.no_bluetooth_connection), Toast.LENGTH_SHORT).show();
                } else if (mCurrentInvoiceId.equals("None!")) {
                    Toast.makeText(StockOutActivity.this, "Please select an issue.", Toast.LENGTH_SHORT).show();
                } else {
                    isScanningBoxRfid = !isScanningBoxRfid;
                    if (isScanningBoxRfid) {
                        startAmination(lavScanningBoxRfid);
                        BluetoothUtil.tempScanResult.registerObserver(StockOutActivity.this, Constant.SCAN_BOX_RFID);
                    } else {
                        stopAmination(lavScanningBoxRfid);
                        BluetoothUtil.tempScanResult.unregisterObserver(StockOutActivity.this);
                    }
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isOnline(StockOutActivity.this)) {
                    Toast.makeText(StockOutActivity.this, getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                } else if (mListShowProduct.isEmpty()) {
                    Toast.makeText(StockOutActivity.this, getText(R.string.not_scan_product_rfid), Toast.LENGTH_SHORT).show();
                } else {
                    if (isIssueEnough(mListShowIssue, mListShowProduct)) {
                        mRfimApi.stockOutBox(mListScanBoxRfid, mCurrentInvoiceId);
                    } else {
                        Toast.makeText(StockOutActivity.this, getString(R.string.not_engouh_product), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListShowProduct.clear();
                mListScanBoxRfid.clear();
                mProductAdapter = new ListProductAdapter(mListShowProduct);
                rcListStockOutBox.setAdapter(mProductAdapter);
                stopAmination(lavScanningBoxRfid);
                isScanningBoxRfid = false;
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnIssueInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("LIST_INVOICE_ITEM", (Serializable) mListShowIssue);
//                mIssueInvoiceFragment = new IssueInvoiceFragment();
//                mIssueInvoiceFragment.setArguments(bundle);
//                mIssueInvoiceFragment.show(getFragmentManager(), "ISSUE");
                mIssueInvoiceFragmentV2 = new IssueInvoiceFragmentV2();
                mIssueInvoiceFragmentV2.setArguments(bundle);
                mIssueInvoiceFragmentV2.show(getSupportFragmentManager(), "ISSUE");
            }
        });
    }

    private boolean isIssueEnough(List<InvoiceInfoItem> invoices, List<ScannedProductItem> listShowProduct) {
        int sumInvoiceQuantity = 0;
        int sumScannedProductQuantity = 0;
        for (int i = 0; i < invoices.size(); i++) {
            sumInvoiceQuantity += invoices.get(i).getQuantity();
        }
        for (int i = 0; i < listShowProduct.size(); i++) {
            sumScannedProductQuantity += listShowProduct.get(i).getQuantity();
        }
        if (sumInvoiceQuantity == sumScannedProductQuantity) {
            return true;
        } else {
            return false;
        }
    }

    private void initView() {
        snIssue = findViewById(R.id.sn_invoice);
        rcListStockOutBox = findViewById(R.id.rc_list_stock_out_box);
        btnScanBoxRfid = findViewById(R.id.btn_scan_box_rfid);
        btnIssueInvoice = findViewById(R.id.btn_issuse_invoice);
        btnSave = findViewById(R.id.btn_save);
        btnClear = findViewById(R.id.btn_clear);
        btnCancel = findViewById(R.id.btn_cancel);
        mListScanBoxRfid = new ArrayList<>();
        gson = new Gson();
        mListShowProduct = new ArrayList<>();
        mRfimApi = new RFIMApi(this);
        mBeepSound = MediaPlayer.create(this, R.raw.beep);
        mInvoices = new ArrayList<>();
        mListInvoiceId = new ArrayList<>();
        mListShowIssue = new ArrayList<>();
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
        BluetoothUtil.tempScanResult.unregisterObserver(StockOutActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BluetoothUtil.tempScanResult.unregisterObserver(StockOutActivity.this);
        finish();
    }


    private Invoice invoiceItem;

    //Get notification from Observerable(TempScanResult) and unregister Observer
    @Override
    public void getNotification(int type) {
        mBeepSound.start();
        switch (type) {
            case Constant.SCAN_BOX_RFID:
                if (mListScanBoxRfid.isEmpty()) {
                    mRfimApi.getProductByBoxRfid(BluetoothUtil.tempScanResult.getRfidID());
                } else {
                    if (mListScanBoxRfid.indexOf(BluetoothUtil.tempScanResult.getRfidID()) < 0) {
                        mRfimApi.getProductByBoxRfid(BluetoothUtil.tempScanResult.getRfidID());
                    }
                }
                break;
        }
    }

    //Call back function when call api
    @Override
    public void onTaskCompleted(String data, int type, int code) {
        switch (type) {
            case Constant.CHECK_BOX_INFO:
                if (code == HttpURLConnection.HTTP_OK) {
                    Product product = gson.fromJson(data, Product.class);
                    InvoiceInfoItem invoiceItem = findInvoice(product.getProductId());
                    if (invoiceItem != null) {
                        Log.e(TAG, "Invoice: " + invoiceItem.getProductId() + " " + invoiceItem.getQuantity());
//                        if (product.getProductId() != null) {
//                            mListScanBoxRfid.add(BluetoothUtil.tempScanResult.getRfidID());
//                        }
//                        if (mListShowProduct.isEmpty()) {
//                            mListScanBoxRfid.add(BluetoothUtil.tempScanResult.getRfidID());
//                            mListShowProduct.add(new ScannedProductItem(product.getProductId(), product.getProductName(), 1));
//                        } else {
                        int posision = isProductExitInShowList(product.getProductId());
                        if (posision != -1) {
                            ScannedProductItem item = mListShowProduct.get(posision);
                            if (item.getQuantity() < invoiceItem.getQuantity()) {
                                mListScanBoxRfid.add(BluetoothUtil.tempScanResult.getRfidID());
                                item.setQuantity(item.getQuantity() + 1);
                                Log.e(TAG, "Invoice: " + item.getQuantity());
                            } else {
                                Toast.makeText(this, getString(R.string.engouh_product), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            mListScanBoxRfid.add(BluetoothUtil.tempScanResult.getRfidID());
                            mListShowProduct.add(new ScannedProductItem(product.getProductId(), product.getProductName(), 1));
                        }
//                        }
                    } else {
                        Toast.makeText(this, getString(R.string.not_corrent_product_invoice), Toast.LENGTH_SHORT).show();
                    }
                    mProductAdapter = new ListProductAdapter(mListShowProduct);
                    rcListStockOutBox.setAdapter(mProductAdapter);
                } else {
                    showResponseMessage(data);
                }
                break;
            case Constant.STOCK_OUT_BOXES:
                if (code == HttpURLConnection.HTTP_OK) {
                    mListScanBoxRfid.clear();
                    mListShowProduct.clear();
                    mProductAdapter = new ListProductAdapter(mListShowProduct);
                    rcListStockOutBox.setAdapter(mProductAdapter);
                    mListInvoiceId.clear();
                    mRfimApi.getIssuseInvoice();
                    stopAmination(lavScanningBoxRfid);
                    isScanningBoxRfid = false;
                    Toast.makeText(this, getString(R.string.stock_out_successfull), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.stock_out_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.GET_ISSUE_INVOICE:
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
                snIssue.setItem(mListInvoiceId);
                break;
        }
    }

    //Check product is exit in list scanned product or not. If exit return position
    private int isProductExitInShowList(String productId) {
        int position = -1;
        for (ScannedProductItem item : mListShowProduct) {
            if (item.getProductId().equalsIgnoreCase(productId)) {
                position = mListShowProduct.indexOf(item);
            }
        }
        return position;
    }

    //Show response message
    public void showResponseMessage(String data) {
        ResponseMessage message = gson.fromJson(data, ResponseMessage.class);
        if (message != null) {
            Toast.makeText(this, message.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Find invoice by product id
    public InvoiceInfoItem findInvoice(String productId) {
        InvoiceInfoItem invoice = null;
        for (InvoiceInfoItem i : mListShowIssue) {
            if (i.getProductId().equals(productId)) {
                invoice = i;
            }
        }
        return invoice;
    }

}
