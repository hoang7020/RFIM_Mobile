package vn.com.rfim_mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.gson.Gson;
import vn.com.rfim_mobile.adapter.ListProductAdapter;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.ScannedProductItem;
import vn.com.rfim_mobile.models.json.Product;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class StockOutActivity extends AppCompatActivity implements Observer, OnTaskCompleted {
    
    public static final String TAG = StockOutActivity.class.getSimpleName();

    private RecyclerView rcListStockOutBox;
    private Button btnScanBoxRfid, btnSave, btnClear, btnCancel;
    private Gson gson;
    private RFIMApi mRfimApi;
    private ListProductAdapter mProductAdapter;
    private List<String> mListScanBoxRfid;
    private List<ScannedProductItem> mListShowProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_out);
        
        initView();

        mProductAdapter = new ListProductAdapter(mListShowProduct);
        rcListStockOutBox.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rcListStockOutBox.setItemAnimator(new DefaultItemAnimator());
        rcListStockOutBox.setAdapter(mProductAdapter);

        btnScanBoxRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtil.tempScanResult.registerObserver(StockOutActivity.this, Constant.SCAN_BOX_RFID);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListShowProduct.isEmpty()) {
                    Toast.makeText(StockOutActivity.this, getText(R.string.not_scan_product_rfid), Toast.LENGTH_SHORT).show();
                } else {
                    mRfimApi.stockOutBox(mListScanBoxRfid);
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
        rcListStockOutBox = findViewById(R.id.rc_list_stock_out_box);
        btnScanBoxRfid = findViewById(R.id.btn_scan_box_rfid);
        btnSave = findViewById(R.id.btn_save);
        btnClear = findViewById(R.id.btn_clear);
        btnCancel = findViewById(R.id.btn_cancel);
        mListScanBoxRfid = new ArrayList<>();
        gson = new Gson();
        mListShowProduct = new ArrayList<>();
        mRfimApi = new RFIMApi(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothUtil.tempScanResult.unregisterObserver(StockOutActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //Get notification from Observerable(TempScanResult) and unregister Observer
    @Override
    public void getNotification(String message, int type) {
        switch (type) {
            case Constant.SCAN_BOX_RFID:
//                Log.e(TAG, "getNotification: " + message);
                String rfid = BluetoothUtil.tempScanResult.getRfidID();
                if (mListScanBoxRfid.isEmpty()) {
//                    mListScanBoxRfid.add(rfid);
                    mRfimApi.getProductByBoxRfid(rfid);
                } else {
                    if (mListScanBoxRfid.indexOf(rfid) < 0) {
//                        mListScanBoxRfid.add(rfid);
                        mRfimApi.getProductByBoxRfid(rfid);
                    }
                }
                break;
        }
    }

    //Call back function when call api
    @Override
    public void onTaskCompleted(String data, int type, int code) {
        switch (type) {
            case Constant.GET_PRODUCT_BY_BOX_ID:
                if (code == HttpURLConnection.HTTP_OK) {
                    Product product = gson.fromJson(data, Product.class);
                    if (product.getProductId() != null) {
                        mListScanBoxRfid.add(BluetoothUtil.tempScanResult.getRfidID());
                    }
                    if (mListShowProduct.isEmpty()) {
                        mListShowProduct.add(new ScannedProductItem(product.getProductId(), product.getProductName(), 1));
                    } else {
                        int posision = isProductExitInShowList(product.getProductId());
                        if (posision != -1) {
                            ScannedProductItem item = mListShowProduct.get(posision);
                            item.setQuantity(item.getQuantity() + 1);
                        } else {
                            mListShowProduct.add(new ScannedProductItem(product.getProductId(), product.getProductName(), 1));
                        }
                    }
                    mProductAdapter = new ListProductAdapter(mListShowProduct);
                    rcListStockOutBox.setAdapter(mProductAdapter);
                } else if (code == HttpURLConnection.HTTP_NO_CONTENT) {
                    Toast.makeText(this, getString(R.string.can_not_find_box), Toast.LENGTH_SHORT).show();
                }
                break;
            case Constant.STOCK_OUT_BOXES:
                if (code == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(this, getString(R.string.stock_out_successfull), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.stock_out_error), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private int isProductExitInShowList(String productId) {
        int position = -1;
        for (ScannedProductItem item: mListShowProduct) {
            if (item.getProductId().equalsIgnoreCase(productId)) {
                position = mListShowProduct.indexOf(item);
            }
        }
        return position;
    }

}
