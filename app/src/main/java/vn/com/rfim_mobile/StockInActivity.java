package vn.com.rfim_mobile;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.airbnb.lottie.LottieAnimationView;
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
import vn.com.rfim_mobile.utils.NetworkUtil;

import java.net.HttpURLConnection;

public class StockInActivity extends AppCompatActivity implements Observer, OnTaskCompleted {

    public static final String TAG = StockInActivity.class.getSimpleName();
    private TextView tvCellRfid, tvPackageRfid, tvFloorId, tvShelfId, tvCellId;
    private Button
//            btnScanCellRfid,
//            btnScanPackageRfid,
            btnSave,
            btnClear,
            btnCancel;
    private LinearLayout
            btnScanCellRfid,
            btnScanPackageRfid;
    private Gson gson;
    private String mCellId;
    private RFIMApi mRfimApi;
    private MediaPlayer mBeepSound;
    private LottieAnimationView lavScanningCellRfid, lavScanningPackageRfid;
    private boolean isScanningCellRfid = false;
    private boolean isScanningPackageRfid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_in);

        try {

            initView();

            btnScanCellRfid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!BluetoothUtil.isConnected) {
                            Toast.makeText(StockInActivity.this, getString(R.string.no_bluetooth_connection), Toast.LENGTH_SHORT).show();
                        } else {
                            isScanningCellRfid = !isScanningCellRfid;
                            if (isScanningCellRfid) {
                                startAmination(lavScanningCellRfid);
                                if (isScanningPackageRfid) {
                                    stopAmination(lavScanningPackageRfid);
                                    isScanningPackageRfid = false;
                                }
                                BluetoothUtil.tempScanResult.registerObserver(StockInActivity.this, Constant.SCAN_CELL_RFID);
                            } else {
                                stopAmination(lavScanningCellRfid);
                                BluetoothUtil.tempScanResult.unregisterObserver(StockInActivity.this);
                            }
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnScanPackageRfid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!BluetoothUtil.isConnected) {
                            Toast.makeText(StockInActivity.this, getString(R.string.no_bluetooth_connection), Toast.LENGTH_SHORT).show();
                        } else {
                            isScanningPackageRfid = !isScanningPackageRfid;
                            if (isScanningPackageRfid) {
                                startAmination(lavScanningPackageRfid);
                                if (isScanningCellRfid) {
                                    stopAmination(lavScanningCellRfid);
                                    isScanningCellRfid = false;
                                }
                                BluetoothUtil.tempScanResult.registerObserver(StockInActivity.this, Constant.SCAN_PACKAGE_RFID);
                            } else {
                                stopAmination(lavScanningPackageRfid);
                                BluetoothUtil.tempScanResult.unregisterObserver(StockInActivity.this);
                            }
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!NetworkUtil.isOnline(StockInActivity.this)) {
                            Toast.makeText(StockInActivity.this, getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                        } else if (tvCellRfid.getText().toString().equals("")) {
                            Toast.makeText(StockInActivity.this, getString(R.string.not_scan_cell_rfid), Toast.LENGTH_SHORT).show();
                        } else if (tvPackageRfid.getText().toString().equals("")) {
                            Toast.makeText(StockInActivity.this, getString(R.string.not_scan_package_rfid), Toast.LENGTH_SHORT).show();
                        } else {
                            mRfimApi.stockInPackage(tvPackageRfid.getText().toString(), mCellId, System.currentTimeMillis());
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        tvCellRfid.setText("");
                        tvCellId.setText("");
                        tvFloorId.setText("");
                        tvShelfId.setText("");
                        tvPackageRfid.setText("");
                        stopAmination(lavScanningCellRfid);
                        stopAmination(lavScanningPackageRfid);
                        isScanningCellRfid = false;
                        isScanningPackageRfid = false;
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        } catch (Exception ex) {
            Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
            Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        tvCellRfid = findViewById(R.id.tv_cell_rfid);
        tvPackageRfid = findViewById(R.id.tv_package_rfid);
        tvFloorId = findViewById(R.id.tv_floor_id);
        tvShelfId = findViewById(R.id.tv_shelf_id);
        tvCellId = findViewById(R.id.tv_cell_id);
        btnScanCellRfid = findViewById(R.id.btn_scan_cell_rfid);
        btnScanPackageRfid = findViewById(R.id.btn_scan_package_rfid);
        btnSave = findViewById(R.id.btn_save);
        btnClear = findViewById(R.id.btn_clear);
        btnCancel = findViewById(R.id.btn_cancel);
        gson = new Gson();
        mRfimApi = new RFIMApi(this);
        mBeepSound = MediaPlayer.create(this, R.raw.beep);
        lavScanningCellRfid = findViewById(R.id.lav_scanning_cell_rfid);
        lavScanningCellRfid.setAnimation(R.raw.scan);
        lavScanningCellRfid.loop(true);
        lavScanningPackageRfid = findViewById(R.id.lav_scanning_package_rfid);
        lavScanningPackageRfid.setAnimation(R.raw.scan);
        lavScanningPackageRfid.loop(true);
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
//                mRfimApi.getCellByCellRfid(rfid);
                mRfimApi.checkCellisEmpty(rfid);
                stopAmination(lavScanningCellRfid);
                isScanningCellRfid = false;
                break;
            case Constant.SCAN_PACKAGE_RFID:
                mRfimApi.getPackageByPackageRfid(rfid);
                BluetoothUtil.tempScanResult.unregisterObserver(StockInActivity.this);
                stopAmination(lavScanningPackageRfid);
                isScanningPackageRfid = false;
                break;
        }
    }

    @Override
    public void onTaskCompleted(String data, int type, int code) {
        Log.e(TAG, "onTaskCompleted: " + data + " code: " + code);
        switch (type) {
            case Constant.CHECK_CELL_IS_EMTPY:
                if (code == HttpURLConnection.HTTP_OK) {
                    mRfimApi.getCellByCellRfid(BluetoothUtil.tempScanResult.getRfidID());
                } else {
                    showResponseMessage(data);
                }
                break;
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
//                        BluetoothUtil.tempScanResult.unregisterObserver(StockInActivity.this);
                    }
                } else {
//                    BluetoothUtil.tempScanResult.unregisterObserver(StockInActivity.this);
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
                    stopAmination(lavScanningCellRfid);
                    stopAmination(lavScanningPackageRfid);
                    isScanningCellRfid = false;
                    isScanningPackageRfid = false;
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
