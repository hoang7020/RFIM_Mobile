package vn.com.rfim_mobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.json.Product;
import vn.com.rfim_mobile.models.json.ResponseMessage;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;
import vn.com.rfim_mobile.utils.NetworkUtil;
import vn.com.rfim_mobile.utils.PreferenceUtil;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class StocktakeInventoryActivity extends AppCompatActivity implements Observer, OnTaskCompleted {

    public static final String TAG = StocktakeInventoryActivity.class.getSimpleName();

    private TextView tvProductId;
    private SmartMaterialSpinner snProductName;
    private Button btnReport,
            btnClear,
            btnCancel;
    private LinearLayout btnScanBoxRfid;
    private Gson gson;
    private RFIMApi mRfimApi;
    private List<Product> mProducts;
    private List<String> mListProductName;
    private List<String> mListBoxRfids;
    private List<String> lostBox;
    private TextView tvProductQuantity,
            tvScannedProduct;
    private StringBuffer sbLostBox;
    private StringBuffer sbFoundBox;

    private List<String> mListScannedRfid;
    private MediaPlayer mBeepSound;
    private LottieAnimationView lavScanningBoxRfid;
    private boolean isScanningBoxRfid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocktake_inventory);

        initView();

        mRfimApi.getAllProduct();

        snProductName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mProducts != null) {
                    tvProductId.setText(mProducts.get(position).getProductId());
                    mRfimApi.getBoxRfidsByProductId(mProducts.get(position).getProductId());
                    mListBoxRfids.clear();
                    mListScannedRfid.clear();
                    tvScannedProduct.setTextColor(Color.RED);
                    tvScannedProduct.setText(mListScannedRfid.size() + "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnScanBoxRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BluetoothUtil.isConnected) {
                    Toast.makeText(StocktakeInventoryActivity.this, getString(R.string.no_bluetooth_connection), Toast.LENGTH_SHORT).show();
                } else {
                    isScanningBoxRfid = !isScanningBoxRfid;
                    if (isScanningBoxRfid) {
                        mListScannedRfid.clear();
                        tvScannedProduct.setTextColor(Color.RED);
                        tvScannedProduct.setText(mListScannedRfid.size() + "");
                        startAmination(lavScanningBoxRfid);
                        BluetoothUtil.tempScanResult.registerObserver(StocktakeInventoryActivity.this, Constant.SCAN_STOCKTAKE_RFID);
                    } else {
                        stopAmination(lavScanningBoxRfid);
                        BluetoothUtil.tempScanResult.unregisterObserver(StocktakeInventoryActivity.this);
                    }
                }
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isOnline(StocktakeInventoryActivity.this)) {
                    Toast.makeText(StocktakeInventoryActivity.this, getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                } else {
                    lostBox.clear();
                    lostBox.addAll(mListBoxRfids);
                    lostBox.removeAll(mListScannedRfid);
                    sbLostBox = new StringBuffer();
                    for (String s : lostBox) {
                        if (lostBox.indexOf(s) < lostBox.size() - 1) {
                            sbLostBox.append(s + ",");
                        } else {
                            sbLostBox.append(s);
                        }
                    }
                    sbFoundBox = new StringBuffer();
                    for (String s : mListScannedRfid) {
                        if (mListScannedRfid.indexOf(s) < mListScannedRfid.size() - 1) {
                            sbFoundBox.append(s + ",");
                        } else {
                            sbFoundBox.append(s);
                        }
                    }
                    new AlertDialog.Builder(StocktakeInventoryActivity.this)
                            .setTitle("Report")
                            .setMessage("Are you sure to report?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.e(TAG, "LB: " + sbLostBox.toString() + " FB:" + sbFoundBox.toString());
                                    mRfimApi.saveStocktakeHistory(
                                            PreferenceUtil.getInstance(getApplicationContext()).getIntValue("userid", 0),
                                            tvProductId.getText().toString(),
                                            mListScannedRfid.size(),
                                            System.currentTimeMillis(),
                                            sbLostBox.toString(),
                                            sbFoundBox.toString());
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvScannedProduct.setText(0 + "");
                snProductName.setSelection(0);
                mListScannedRfid.clear();
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
    }

    private void initView() {
        tvProductId = findViewById(R.id.tv_product_id);
        tvProductQuantity = findViewById(R.id.tv_product_quantity);
        tvScannedProduct = findViewById(R.id.tv_scanned_product);
        btnScanBoxRfid = findViewById(R.id.btn_scan_box_rfid);
        btnReport = findViewById(R.id.btn_report);
        btnClear = findViewById(R.id.btn_clear);
        btnCancel = findViewById(R.id.btn_cancel);
        snProductName = findViewById(R.id.sn_product_name);
        gson = new Gson();
        mRfimApi = new RFIMApi(this);
        mListBoxRfids = new ArrayList<>();
        mListProductName = new ArrayList<>();
        sbLostBox = new StringBuffer();
        sbFoundBox = new StringBuffer();
        mListScannedRfid = new ArrayList<>();
        lostBox = new ArrayList<>();
        mBeepSound = MediaPlayer.create(this, R.raw.beep);
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
    public void getNotification(int type) {
        switch (type) {
            case Constant.SCAN_STOCKTAKE_RFID:
                String rfid = BluetoothUtil.tempScanResult.getRfidID();
                if (mListBoxRfids.contains(rfid)) {
                    if (!mListScannedRfid.contains(rfid)) {
                        mBeepSound.start();
                        mListScannedRfid.add(rfid);
                    }
                }
                tvScannedProduct.setText(mListScannedRfid.size() + "");
                if (mListBoxRfids.size() == mListScannedRfid.size()) {
                    tvScannedProduct.setTextColor(Color.GREEN);
                    stopAmination(lavScanningBoxRfid);
                    isScanningBoxRfid = false;
                    BluetoothUtil.tempScanResult.unregisterObserver(StocktakeInventoryActivity.this);
                }
                break;
        }
    }

    @Override
    public void onTaskCompleted(String data, int type, int code) {
        Log.e(TAG, "onTaskCompleted: " + data);
        switch (type) {
            case Constant.GET_ALL_PRODUCTS:
                if (code == HttpURLConnection.HTTP_OK) {
                    mProducts = gson.fromJson(data, new TypeToken<List<Product>>() {
                    }.getType());
                    for (Product p : mProducts) {
                        mListProductName.add(p.getProductName());
                    }
                } else {
                    mListProductName.add("None!");
                }
                snProductName.setItem(mListProductName);
                break;
            case Constant.GET_BOX_RFIDS_BY_PRODUCT_ID:
                if (code == HttpURLConnection.HTTP_OK) {
                    mListBoxRfids = gson.fromJson(data, new TypeToken<List<String>>() {
                    }.getType());
                    tvProductQuantity.setText(mListBoxRfids.size() + "");
                } else {
                    tvProductQuantity.setText("0");
                }
                break;
            case Constant.SAVE_STOCKTAKE_HISTORY:
                if (code == HttpURLConnection.HTTP_OK) {
                    tvScannedProduct.setText(0 + "");
                    snProductName.setSelection(0);
                    mListScannedRfid.clear();
                    lostBox.clear();
                    tvScannedProduct.setTextColor(Color.RED);
                    stopAmination(lavScanningBoxRfid);
                    isScanningBoxRfid = false;
                    showResponseMessage(data);
                    stopAmination(lavScanningBoxRfid);
                    isScanningBoxRfid = false;
                } else {
                    showResponseMessage(data);
                }
                break;
        }
    }

    public void showResponseMessage(String data) {
        ResponseMessage message = gson.fromJson(data, ResponseMessage.class);
        if (message != null) {
            Toast.makeText(this, message.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
