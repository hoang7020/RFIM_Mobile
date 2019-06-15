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

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import vn.com.rfim_mobile.api.CellApi;
import vn.com.rfim_mobile.api.FloorApi;
import vn.com.rfim_mobile.api.ShelfApi;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.fragments.ScanningFragment;
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.json.Cell;
import vn.com.rfim_mobile.models.json.Floor;
import vn.com.rfim_mobile.models.json.ObjectResult;
import vn.com.rfim_mobile.models.json.Shelf;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

public class RegisterShelfActivity extends AppCompatActivity implements Observer, OnTaskCompleted {

    public static final String TAG = RegisterShelfActivity.class.getSimpleName();
    private TextView tvCellRfid;
    private Button btnScanShelfRfid, btnSave, btnClear, btnCancel;
    private SmartMaterialSpinner snShelfID, snFloorId, snCellId;
    private ShelfApi mShelfApi;
    private FloorApi mFloorApi;
    private CellApi mCellApi;
    private List<String> listShelvesId, listFloorsId, listCellsId;
    private String mCellId;
    private ScanningFragment mScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_shelf);

        initView();
        mShelfApi.getAllShelves();
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
                    Toast.makeText(RegisterShelfActivity.this, getString(R.string.no_cell_found), Toast.LENGTH_SHORT).show();
                } else if (tvCellRfid.getText().toString().equals("")) {
                    Toast.makeText(RegisterShelfActivity.this, getString(R.string.no_rfid), Toast.LENGTH_SHORT).show();
                } else {
                    mCellApi.registerCell(mCellId, tvCellRfid.getText().toString());
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
                mFloorApi.getFloorsByShelfId(sheflId);
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
                mCellApi.getCellsByFloorId(floorId);
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
        mShelfApi = new ShelfApi(this);
        mFloorApi = new FloorApi(this);
        mCellApi = new CellApi(this);
    }

    private void initScanningFragment() {
        mScanning = new ScanningFragment();
        mScanning.setCancelable(false);
        mScanning.show(getFragmentManager(), "Scanning");
    }

    //get notification from Observerable(TempScanResult) and unregister Observer
    @Override
    public void getNotification(String message, int type) {
        if (type == Constant.SCAN_SHELF_RFID) {
            Log.e(TAG, "getNotification: " + message);
            tvCellRfid.setText(BluetoothUtil.tempScanResult.getRfidID());
            BluetoothUtil.tempScanResult.unregisterObserver(RegisterShelfActivity.this);
            mScanning.dismiss();
        }
    }

    //Callback get result from ShelfApi, FloorApi, CellApi
    @Override
    public void onTaskCompleted(ObjectResult result, int type, int code) {
        if (type == Constant.GET_ALL_SHELVES) {
            listShelvesId = new ArrayList();
            if (code == HttpURLConnection.HTTP_OK) {
                for (Shelf s : result.getData().getShelves()) {
                    listShelvesId.add(s.getShelfId());
                }
            } else {
                listCellsId.add(getString(R.string.not_found_item));
            }
            snShelfID.setItem(listShelvesId);
        } else if (type == Constant.GET_ALL_FLOORS_BY_SHELF_ID) {
            listFloorsId = new ArrayList<>();
            if (code == HttpURLConnection.HTTP_OK) {
                for (Floor f : result.getData().getFloors()) {
                    listFloorsId.add(f.getFloorId());
                }
            } else {
                listFloorsId.add(getString(R.string.not_found_item));
            }
            snFloorId.setItem(listFloorsId);
        } else if (type == Constant.GET_ALL_CELLS_BY_FLOOR_ID) {
            listCellsId = new ArrayList<>();
            if (code == HttpURLConnection.HTTP_OK) {
                for (Cell c : result.getData().getCells()) {
                    listCellsId.add(c.getCellId());
                }
            } else {
                listCellsId.add(getString(R.string.not_found_item));
            }
            snCellId.setItem(listCellsId);
        } else if (type == Constant.REGISTER_CELL) {
            if (code == HttpURLConnection.HTTP_OK) {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                tvCellRfid.setText("");
            } else {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
