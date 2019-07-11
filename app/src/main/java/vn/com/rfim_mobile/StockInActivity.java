package vn.com.rfim_mobile;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.json.Cell;
import vn.com.rfim_mobile.models.json.Floor;
import vn.com.rfim_mobile.models.json.Package;
import vn.com.rfim_mobile.models.json.ResponseMessage;
import vn.com.rfim_mobile.models.json.Shelf;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

import java.net.HttpURLConnection;

public class StockInActivity extends AppCompatActivity implements Observer, OnTaskCompleted {

    public static final String TAG = StockInActivity.class.getSimpleName();
    private TextView tvCellRfid, tvPackageRfid, tvFloorId, tvShelfId, tvCellId;
    private Button btnScanCellRfid,
            btnScanPackageRfid,
            btnSave,
            btnClear,
            btnCancel;
    private Gson gson;
    private String mCellId;
    private RFIMApi mRfimApi;
    private MediaPlayer mBeepSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in);

        initView();

        btnScanCellRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtil.tempScanResult.registerObserver(StockInActivity.this, Constant.SCAN_CELL_RFID);
            }
        });

        btnScanPackageRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtil.tempScanResult.registerObserver(StockInActivity.this, Constant.SCAN_PACKAGE_RFID);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvCellRfid.getText().toString().equals("")) {
                    Toast.makeText(StockInActivity.this, getString(R.string.not_scan_cell_rfid), Toast.LENGTH_SHORT).show();
                } else if (tvPackageRfid.getText().toString().equals("")) {
                    Toast.makeText(StockInActivity.this, getString(R.string.not_scan_package_rfid), Toast.LENGTH_SHORT).show();
                } else {
                    mRfimApi.stockInPackage(tvPackageRfid.getText().toString(), mCellId, System.currentTimeMillis());
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCellRfid.setText("");
                tvCellId.setText("");
                tvFloorId.setText("");
                tvShelfId.setText("");
                tvPackageRfid.setText("");
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
        tvCellRfid = findViewById(R.id.tv_cell_rfid);
        tvPackageRfid = findViewById(R.id.tv_package_rfid);
        tvFloorId = findViewById(R.id.tv_floor_id);
        tvShelfId = findViewById(R.id.tv_shelf_id);
        tvCellId = findViewById(R.id.tv_cell_id);
        btnScanCellRfid = findViewById(R.id.btn_scan_cell_rfid);
        btnScanPackageRfid = findViewById(R.id.btn_scan_pakage_rfid);
        btnSave = findViewById(R.id.btn_save);
        btnClear = findViewById(R.id.btn_clear);
        btnCancel = findViewById(R.id.btn_cancel);
        gson = new Gson();
        mRfimApi = new RFIMApi(this);
        mBeepSound = MediaPlayer.create(this, R.raw.beep);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothUtil.tempScanResult.unregisterObserver(StockInActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BluetoothUtil.tempScanResult.unregisterObserver(StockInActivity.this);
        finish();
    }

    @Override
    public void getNotification(int type) {
        mBeepSound.start();
        String rfid = BluetoothUtil.tempScanResult.getRfidID();
        switch (type) {
            case Constant.SCAN_CELL_RFID:
                mRfimApi.getCellByCellRfid(rfid);
                break;
            case Constant.SCAN_PACKAGE_RFID:
                mRfimApi.getPackageByPackageRfid(rfid);
                break;
        }
    }

    @Override
    public void onTaskCompleted(String data, int type, int code) {
        Log.e(TAG, "onTaskCompleted: " + data + " code: " + code);
        switch (type) {
            case Constant.GET_CELL_BY_CELL_RFID:
                if (code == HttpURLConnection.HTTP_OK) {
                    Cell cell = gson.fromJson(data, Cell.class);
                    if (cell != null) {
                        tvCellRfid.setText(BluetoothUtil.tempScanResult.getRfidID());
                        BluetoothUtil.tempScanResult.unregisterObserver(StockInActivity.this);
                        tvCellId.setText(cell.getCellId());
                        mCellId = cell.getCellId();
                        mRfimApi.getFloorByCellId(cell.getCellId());
                    }
                } else {
                    BluetoothUtil.tempScanResult.unregisterObserver(StockInActivity.this);
                    showResponseMessage(data);
                }
                break;
            case Constant.GET_PACKAGE_BY_PACKAGE_RFID:
                if (code == HttpURLConnection.HTTP_OK) {
                    Package pac = gson.fromJson(data, Package.class);
                    if (pac != null) {
                        tvPackageRfid.setText(BluetoothUtil.tempScanResult.getRfidID());
                        BluetoothUtil.tempScanResult.unregisterObserver(StockInActivity.this);
                    }
                } else {
                    BluetoothUtil.tempScanResult.unregisterObserver(StockInActivity.this);
                    showResponseMessage(data);
                }
                break;
            case Constant.GET_FLOOR_BY_CELL_ID:
                if (code == HttpURLConnection.HTTP_OK) {
                    Floor floor = gson.fromJson(data, Floor.class);
                    if (floor != null) {
                        tvFloorId.setText(floor.getFloorId());
                        mRfimApi.getShelfByFloorId(floor.getFloorId());
                    }
                }
                break;
            case Constant.GET_SHELF_BY_FLOOR_ID:
                if (code == HttpURLConnection.HTTP_OK) {
                    Shelf shelf = gson.fromJson(data, Shelf.class);
                    if (shelf != null) {
                        tvShelfId.setText(shelf.getShelfId());
                    }
                }
                break;
            case Constant.STOCK_IN_PACKAGE:
                ResponseMessage message = gson.fromJson(data, ResponseMessage.class);
                if (code == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(this, message.getMessage(), Toast.LENGTH_SHORT).show();
                    tvCellRfid.setText("");
                    tvCellId.setText("");
                    tvFloorId.setText("");
                    tvShelfId.setText("");
                    tvPackageRfid.setText("");
                } else {
                    Toast.makeText(this, message.getMessage(), Toast.LENGTH_SHORT).show();
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
