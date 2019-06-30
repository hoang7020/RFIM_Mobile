package vn.com.rfim_mobile.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.json.Product;
import vn.com.rfim_mobile.models.json.ResponseMessage;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class RegisterBoxFragment extends Fragment implements Observer, OnTaskCompleted {

    public static final String TAG = RegisterBoxFragment.class.getSimpleName();

    private List<String> mListScanRfid;
    private Button btnScanProductRfid, btnScanShelfRfid, btnSave, btnClear, btnCancel;
    private TextView txtBoxRfid, txtPackageRfid, txtProductName, txtProductDescription;
    private SmartMaterialSpinner snProductId;
    private StringBuffer sb;
    private List<Product> mProducts;
    private List<String> mListProductId;
    private RFIMApi mRfimApi;
    private String mProductId;
    private Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_box, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

        mRfimApi.getAllProduct();
        btnScanProductRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtil.tempScanResult.registerObserver(RegisterBoxFragment.this, Constant.SCAN_BOX_RFID);
            }
        });

        btnScanShelfRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothUtil.tempScanResult.registerObserver(RegisterBoxFragment.this, Constant.SCAN_PACKAGE_RFID);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProductId.equals("")) {
                    Toast.makeText(getActivity(), getText(R.string.not_choose_product), Toast.LENGTH_SHORT).show();
                } else if (txtPackageRfid.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getText(R.string.not_scan_product_rfid), Toast.LENGTH_SHORT).show();
                } else {
                    mRfimApi.registerPackageAndBox(txtPackageRfid.getText().toString(), mProductId, mListScanRfid);
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListScanRfid.clear();
                snProductId.setSelection(0);
                txtPackageRfid.setText("");
                txtBoxRfid.setText("");
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        snProductId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txtProductName.setText(mProducts.get(position).getProductName());
                mProductId = mListProductId.get(position);
                txtProductDescription.setText(mProducts.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initView() {
        btnScanProductRfid = getView().findViewById(R.id.btn_scan_product_rfid);
        btnScanShelfRfid = getView().findViewById(R.id.btn_scan_shelf_rfid);
        btnSave = getView().findViewById(R.id.btn_save);
        btnClear = getView().findViewById(R.id.btn_clear);
        btnCancel = getView().findViewById(R.id.btn_cancel);
        txtBoxRfid = getView().findViewById(R.id.txt_box_rfid);
        txtPackageRfid = getView().findViewById(R.id.txt_package_rfid);
        txtProductName = getView().findViewById(R.id.txt_product_name);
        txtProductDescription = getView().findViewById(R.id.txt_product_description);
        snProductId = getView().findViewById(R.id.sn_product_id);
        mListScanRfid = new ArrayList<>();
        sb = new StringBuffer();
        gson = new Gson();
        mRfimApi = new RFIMApi(this);
    }

    @Override
    public void getNotification(int type) {
        switch (type) {
            case Constant.SCAN_BOX_RFID:
                if (mListScanRfid.isEmpty()) {
                    mListScanRfid.add(BluetoothUtil.tempScanResult.getRfidID());
                } else {
                    if (mListScanRfid.indexOf(BluetoothUtil.tempScanResult.getRfidID()) < 0) {
                        mListScanRfid.add(BluetoothUtil.tempScanResult.getRfidID());
                    }
                }
                for (String s : mListScanRfid) {
                    sb.append(s + "\n");
                }
                Log.e(TAG, "getNotification: " + sb.toString());
                txtBoxRfid.setText(sb.toString());
                sb.delete(0, sb.length());
                break;
            case Constant.SCAN_PACKAGE_RFID:
                txtPackageRfid.setText(BluetoothUtil.tempScanResult.getRfidID());
                BluetoothUtil.tempScanResult.unregisterObserver(RegisterBoxFragment.this);
                break;
        }
    }

    @Override
    public void onTaskCompleted(String data, int type, int code) {
                Log.e(TAG, "onTaskCompleted: " + data);
        switch (type) {
            case Constant.GET_ALL_PRODUCTS:
                mListProductId = new ArrayList<>();
                if (code == HttpURLConnection.HTTP_OK) {
                    mProducts = gson.fromJson(data, new TypeToken<List<Product>>() {
                    }.getType());
                    for (Product p : mProducts) {
                        mListProductId.add(p.getProductId());
                    }
                }
                snProductId.setItem(mListProductId);
                break;
            case Constant.REGISTER_PACKAGE_AND_BOX:
                ResponseMessage message = gson.fromJson(data, ResponseMessage.class);
                if (code == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(getActivity(), message.getMessage(), Toast.LENGTH_SHORT).show();
                    txtPackageRfid.setText("");
                    txtBoxRfid.setText("");
                    mListScanRfid.clear();
                }
                break;
        }
    }
}
