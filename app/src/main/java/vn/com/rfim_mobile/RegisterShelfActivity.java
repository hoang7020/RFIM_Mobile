package vn.com.rfim_mobile;

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

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.fragments.ScanningFragment;
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.json.*;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

public class RegisterShelfActivity extends AppCompatActivity implements Observer, OnTaskCompleted {

    public static final String TAG = RegisterShelfActivity.class.getSimpleName();
    private TextView tvCellRfid;
    private Button btnScanShelfRfid, btnSave, btnClear, btnCancel;
    private SmartMaterialSpinner snShelfID, snFloorId, snCellId;
    private RFIMApi mRfimApi;
    private List<String> listShelvesId, listFloorsId, listCellsId;
    private String mCellId;
    private ScanningFragment mScanning;
    private Gson gson;
    private MediaPlayer mBeepSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_shelf);

        initView();
        mRfimApi.getAllShelves();
        btnScanShelfRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtil.tempScanResult.registerObserver(RegisterShelfActivity.this, Constant.SCAN_SHELF_RFID);
                initScanningFragment();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCellId.equals(getString(R.string.not_found_item))) {
                    Toast.makeText(RegisterShelfActivity.this, getString(R.string.not_choose_cell), Toast.LENGTH_SHORT).show();
                } else if (tvCellRfid.getText().toString().equals("")) {
                    Toast.makeText(RegisterShelfActivity.this, getString(R.string.not_scan_cell_rfid), Toast.LENGTH_SHORT).show();
                } else {
                    mRfimApi.registerCell(mCellId, tvCellRfid.getText().toString());
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snShelfID.setSelection(0);
                snFloorId.setSelection(0);
                snCellId.setSelection(0);
                tvCellRfid.setText("");
            }
        });

        //Event choose shelf item
        snShelfID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sheflId = listShelvesId.get(position);
                mRfimApi.getFloorsByShelfId(sheflId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Event choose floor item
        snFloorId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String floorId = listFloorsId.get(position);
                mRfimApi.getCellsByFloorId(floorId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Event choose cell item
        snCellId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCellId = listCellsId.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initView() {
        tvCellRfid = findViewById(R.id.tv_cell_rfid);
        btnScanShelfRfid = findViewById(R.id.btn_scan_cell_rfid);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnClear = findViewById(R.id.btn_clear);
        snShelfID = findViewById(R.id.sn_shelf_id);
        snFloorId = findViewById(R.id.sn_floor_id);
        snCellId = findViewById(R.id.sn_cell_id);
        mRfimApi = new RFIMApi(this);
        gson = new Gson();
        mBeepSound = MediaPlayer.create(this, R.raw.beep);
    }

    private void initScanningFragment() {
        mScanning = new ScanningFragment();
//        mScanning.setCancelable(false);
        mScanning.show(getFragmentManager(), "Scanning");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothUtil.tempScanResult.unregisterObserver(RegisterShelfActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BluetoothUtil.tempScanResult.unregisterObserver(RegisterShelfActivity.this);
        finish();
    }

    //get notification from Observerable(TempScanResult) and unregister Observer
    @Override
    public void getNotification(int type) {
        mBeepSound.start();
        if (type == Constant.SCAN_SHELF_RFID) {
            Log.e(TAG, "getNotification: " + BluetoothUtil.tempScanResult.getRfidID());
            tvCellRfid.setText(BluetoothUtil.tempScanResult.getRfidID());
            BluetoothUtil.tempScanResult.unregisterObserver(RegisterShelfActivity.this);
            mScanning.dismiss();
        }
    }

    //Callback get result from ShelfApi, FloorApi, CellApi
    @Override
    public void onTaskCompleted(String data, int type, int code) {
        Log.e(TAG, "onTaskCompleted: " + data);
        switch (type) {
            case Constant.GET_ALL_SHELVES:
                listShelvesId = new ArrayList();
                if (code == HttpURLConnection.HTTP_OK) {
                    List<Shelf> shelves = gson.fromJson(data, new TypeToken<List<Shelf>>(){}.getType());
                    for (Shelf s : shelves) {
                        listShelvesId.add(s.getShelfId());
                    }
                } else {
                    listShelvesId.add(getString(R.string.not_found_item));
                }
                snShelfID.setItem(listShelvesId);
                break;
            case Constant.GET_ALL_FLOORS_BY_SHELF_ID:
                listFloorsId = new ArrayList<>();
                if (code == HttpURLConnection.HTTP_OK) {
                    List<Floor> floors = gson.fromJson(data, new TypeToken<List<Floor>>(){}.getType());
                    for (Floor f : floors) {
                        listFloorsId.add(f.getFloorId());
                    }
                } else {
                    listFloorsId.add(getString(R.string.not_found_item));
                }
                snFloorId.setItem(listFloorsId);
                break;
            case Constant.GET_ALL_CELLS_BY_FLOOR_ID:
                listCellsId = new ArrayList<>();
                if (code == HttpURLConnection.HTTP_OK) {
                    List<Cell> cells = gson.fromJson(data, new TypeToken<List<Cell>>(){}.getType());
                    for (Cell c : cells) {
                        listCellsId.add(c.getCellId());
                    }
                } else {
                    listCellsId.add(getString(R.string.not_found_item));
                }
                snCellId.setItem(listCellsId);
                break;
            case Constant.REGISTER_CELL:
                ResponseMessage message = gson.fromJson(data, ResponseMessage.class);
                if (code == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(this, message.getMessage(), Toast.LENGTH_SHORT).show();
                    tvCellRfid.setText("");
                } else {
                    Toast.makeText(this, message.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
