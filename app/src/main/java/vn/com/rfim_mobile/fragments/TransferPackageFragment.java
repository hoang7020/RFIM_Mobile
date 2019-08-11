package vn.com.rfim_mobile.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.StockInActivity;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.json.Cell;
import vn.com.rfim_mobile.models.json.Floor;
import vn.com.rfim_mobile.models.json.ResponseMessage;
import vn.com.rfim_mobile.models.json.Shelf;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

import java.net.HttpURLConnection;

public class TransferPackageFragment extends Fragment implements Observer, OnTaskCompleted {

    public static final String TAG = TransferBoxFragment.class.getSimpleName();

    private TextView tvCellRfid, tvPackageRfid, tvFloorId, tvShelfId, tvCellId;
    private Button
//            btnScanCellRfid,
//            btnScanPackageRfid,
            btnSave,
            btnClear,
            btnCancel;
    private LinearLayout btnScanCellRfid, btnScanPackageRfid;
    private Gson gson;
    private String mCellId;
    private RFIMApi mRfimApi;
    private MediaPlayer mBeepSound;
    private LottieAnimationView lavScanningCellRfid, lavScanningPackageRfid;
    private boolean isScanningCellRfid = false;
    private boolean isScanningPackageRfid = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer_package, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

        btnScanCellRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isScanningCellRfid = !isScanningCellRfid;
                if (isScanningCellRfid) {
                    startAmination(lavScanningCellRfid);
                    if (isScanningPackageRfid) {
                        stopAmination(lavScanningPackageRfid);
                        isScanningPackageRfid = false;
                    }
                    BluetoothUtil.tempScanResult.registerObserver(TransferPackageFragment.this, Constant.SCAN_CELL_RFID);
                } else {
                    stopAmination(lavScanningCellRfid);
                    BluetoothUtil.tempScanResult.unregisterObserver(TransferPackageFragment.this);
                }
            }
        });

        btnScanPackageRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isScanningPackageRfid = !isScanningPackageRfid;
                if (isScanningPackageRfid) {
                    startAmination(lavScanningPackageRfid);
                    if (isScanningCellRfid) {
                        stopAmination(lavScanningCellRfid);
                        isScanningCellRfid = false;
                    }
                    BluetoothUtil.tempScanResult.registerObserver(TransferPackageFragment.this, Constant.SCAN_PACKAGE_RFID);
                } else {
                    stopAmination(lavScanningPackageRfid);
                    BluetoothUtil.tempScanResult.unregisterObserver(TransferPackageFragment.this);
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvCellRfid.getText().toString().equals("")) {

                    Toast.makeText(getActivity(), getString(R.string.not_scan_cell_rfid), Toast.LENGTH_SHORT).show();
                } else if (tvPackageRfid.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.not_scan_package_rfid), Toast.LENGTH_SHORT).show();
                } else {
                    mRfimApi.transferPackage(tvPackageRfid.getText().toString(), mCellId);
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
                stopAmination(lavScanningCellRfid);
                stopAmination(lavScanningPackageRfid);
                isScanningCellRfid = false;
                isScanningPackageRfid = false;
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void initView() {
        tvCellRfid = getView().findViewById(R.id.tv_cell_rfid);
        tvPackageRfid = getView().findViewById(R.id.tv_package_rfid);
        tvFloorId = getView().findViewById(R.id.tv_floor_id);
        tvShelfId = getView().findViewById(R.id.tv_shelf_id);
        tvCellId = getView().findViewById(R.id.tv_cell_id);
        btnScanCellRfid = getView().findViewById(R.id.btn_scan_cell_rfid);
        btnScanPackageRfid = getView().findViewById(R.id.btn_scan_package_rfid);
        btnSave = getView().findViewById(R.id.btn_save);
        btnClear = getView().findViewById(R.id.btn_clear);
        btnCancel = getView().findViewById(R.id.btn_cancel);
        gson = new Gson();
        mRfimApi = new RFIMApi(this);
        mBeepSound = MediaPlayer.create(getActivity(), R.raw.beep);
        lavScanningCellRfid = getView().findViewById(R.id.lav_scanning_cell_rfid);
        lavScanningCellRfid.setAnimation(R.raw.scan);
        lavScanningCellRfid.loop(true);
        lavScanningPackageRfid = getView().findViewById(R.id.lav_scanning_package_rfid);
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
    public void onDestroyView() {
        super.onDestroyView();
        BluetoothUtil.tempScanResult.unregisterObserver(TransferPackageFragment.this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BluetoothUtil.tempScanResult.unregisterObserver(TransferPackageFragment.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void getNotification(int type) {
        mBeepSound.start();
        switch (type) {
            case Constant.SCAN_CELL_RFID:
                Log.e(TAG, "SCAN_SHELF_RFID: " + BluetoothUtil.tempScanResult.getRfidID());
                mRfimApi.getCellByCellRfid(BluetoothUtil.tempScanResult.getRfidID());
                BluetoothUtil.tempScanResult.unregisterObserver(TransferPackageFragment.this);
                break;
            case Constant.SCAN_PACKAGE_RFID:
                Log.e(TAG, "SCAN_PACKAGE_RFID: " + BluetoothUtil.tempScanResult.getRfidID());
                mRfimApi.getPackageByPackageRfid(BluetoothUtil.tempScanResult.getRfidID());
                BluetoothUtil.tempScanResult.unregisterObserver(TransferPackageFragment.this);
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
                        tvCellId.setText(cell.getCellId());
                        mCellId = cell.getCellId();
                        mRfimApi.getFloorByCellId(cell.getCellId());
                    }
                } else {
                    showResponseMessage(data);
                }
                stopAmination(lavScanningCellRfid);
                isScanningCellRfid = false;
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
            case Constant.GET_PACKAGE_BY_PACKAGE_RFID:
                if (code == HttpURLConnection.HTTP_OK) {
                    tvPackageRfid.setText(BluetoothUtil.tempScanResult.getRfidID());
                } else {
                    showResponseMessage(data);
                }
                stopAmination(lavScanningPackageRfid);
                isScanningPackageRfid = false;
                break;
            case Constant.TRANSFER_PACKAGE:
                if (code == HttpURLConnection.HTTP_OK) {
                    showResponseMessage(data);
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
                    showResponseMessage(data);
                }
                break;
        }
    }

    public void showResponseMessage(String data) {
        ResponseMessage message = gson.fromJson(data, ResponseMessage.class);
        if (message != null) {
            Toast.makeText(getActivity(), message.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
