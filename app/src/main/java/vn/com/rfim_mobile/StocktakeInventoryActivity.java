package vn.com.rfim_mobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
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
import com.google.gson.reflect.TypeToken;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.json.Product;
import vn.com.rfim_mobile.models.json.ResponseMessage;
import vn.com.rfim_mobile.models.json.StocktakeType;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;
import vn.com.rfim_mobile.utils.PreferenceUtil;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class StocktakeInventoryActivity extends AppCompatActivity implements Observer, OnTaskCompleted {

    public static final String TAG = StocktakeInventoryActivity.class.getSimpleName();

    private SmartMaterialSpinner snProductId, snStocktakeType;
    private Button btnScanBoxRfid,
            btnReport,
            btnClear,
            btnCancel;
    private Gson gson;
    private RFIMApi mRfimApi;
    private List<String> mListProductId;
    private List<Product> mProducts;
    private List<String> mListBoxRfids;
    private TextView tvProductQuantity, tvScannedProduct;
//    private List<StocktakeType> mStocktakeTypes;
//    private List<String> mListStocktakeType;
//    private int mStocktakeTypeIdSelected;
    private String mProductIdSelected;
    private List<String> mListScannedRfid;
    private MediaPlayer mBeepSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocktake_inventory);

        initView();

        mRfimApi.getAllProduct();
//        mRfimApi.getAllStocktakeType();

        snProductId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mProductIdSelected = mListProductId.get(position);
                mRfimApi.getBoxRfidsByProductId(mProductIdSelected);
                mListBoxRfids.clear();
                mListScannedRfid.clear();
                tvScannedProduct.setTextColor(Color.RED);
                tvScannedProduct.setText(mListScannedRfid.size() + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        snStocktakeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (mStocktakeTypes.size() > 0) {
//                    mStocktakeTypeIdSelected = mStocktakeTypes.get(position).getStocktakeTypeId();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        btnScanBoxRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtil.tempScanResult.registerObserver(StocktakeInventoryActivity.this, Constant.SCAN_STOCKTAKE_RFID);
                mListScannedRfid.clear();
                tvScannedProduct.setTextColor(Color.RED);
                tvScannedProduct.setText(mListScannedRfid.size() + "");
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(StocktakeInventoryActivity.this)
                        .setTitle("Report")
                        .setMessage("Do you want to report")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mRfimApi.saveStocktakeHistory(
                                        PreferenceUtil.getInstance(getApplicationContext()).getIntValue("userid", 0),
                                        mProductIdSelected,
                                        mListScannedRfid.size(),
                                        System.currentTimeMillis(),
                                        "nothing");
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snStocktakeType.setSelection(0);
                snProductId.setSelection(0);
                mListScannedRfid.clear();
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
        tvProductQuantity = findViewById(R.id.tv_product_quantity);
        tvScannedProduct = findViewById(R.id.tv_scanned_product);
        btnScanBoxRfid = findViewById(R.id.btn_scan_box_rfid);
        btnReport = findViewById(R.id.btn_report);
        btnClear = findViewById(R.id.btn_clear);
        btnCancel = findViewById(R.id.btn_cancel);
        snProductId = findViewById(R.id.sn_product_id);
//        snStocktakeType = findViewById(R.id.sn_stocktake_type);
        gson = new Gson();
        mRfimApi = new RFIMApi(this);
        mListProductId = new ArrayList<>();
        mListBoxRfids = new ArrayList<>();
//        mListStocktakeType = new ArrayList<>();
//        mStocktakeTypes = new ArrayList<>();
        mListScannedRfid = new ArrayList<>();
        mBeepSound = MediaPlayer.create(this, R.raw.beep);
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
                        mListProductId.add(p.getProductId());
                    }
                } else {
                    mListProductId.add("None!");
                }
                snProductId.setItem(mListProductId);
                break;
//            case Constant.GET_ALL_STOCKTAKE_TYPE:
//                if (code == HttpURLConnection.HTTP_OK) {
//                    mStocktakeTypes = gson.fromJson(data, new TypeToken<List<StocktakeType>>() {
//                    }.getType());
//                    for (StocktakeType s : mStocktakeTypes) {
//                        mListStocktakeType.add(s.getStocktakeType());
//                    }
//                } else {
//                    mListStocktakeType.add("None!");
//                }
//                snStocktakeType.setItem(mListStocktakeType);
//                break;
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
//                    snStocktakeType.setSelection(0);
                    snProductId.setSelection(0);
                    mListScannedRfid.clear();
                    showResponseMessage(data);
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
