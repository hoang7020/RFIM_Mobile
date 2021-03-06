package vn.com.rfim_mobile.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import vn.com.rfim_mobile.adapter.ListProductAdapter;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.ScannedProductItem;
import vn.com.rfim_mobile.models.json.Package;
import vn.com.rfim_mobile.models.json.Product;
import vn.com.rfim_mobile.models.json.ResponseMessage;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;
import vn.com.rfim_mobile.utils.NetworkUtil;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class TransferBoxFragment extends Fragment implements Observer, OnTaskCompleted {

    public static final String TAG = TransferBoxFragment.class.getSimpleName();

    private TextView tvPackageRfid;
    private Button
//            btnScanPackageRfid,
//            btnScanBoxRfid,
            btnSave,
            btnClear,
            btnCancel;
    private LinearLayout
            btnScanPackageRfid,
            btnScanBoxRfid;
    private RFIMApi mRfimApi;
    private List<String> mListScanBoxRfid;
    private List<ScannedProductItem> mListShowProduct;
    private Gson gson;
    private ListProductAdapter mProductAdapter;
    private RecyclerView rcListTransferBox;
    private String mCurrentTransferProductid;
    private MediaPlayer mBeepSound;
    private LottieAnimationView lavScanningPackageRfid, lavScanningBoxRfid;
    private boolean isScanningPackageRfid = false;
    private boolean isScanningBoxRfid = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer_box, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {

            initView();

            mProductAdapter = new ListProductAdapter(mListShowProduct);
            rcListTransferBox.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            rcListTransferBox.setItemAnimator(new DefaultItemAnimator());
            rcListTransferBox.setAdapter(mProductAdapter);

            btnScanPackageRfid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!BluetoothUtil.isConnected) {
                            Toast.makeText(getActivity(), getString(R.string.no_bluetooth_connection), Toast.LENGTH_SHORT).show();
                        } else {
                            if (mListScanBoxRfid.isEmpty()) {
                                isScanningPackageRfid = !isScanningPackageRfid;
                                if (isScanningPackageRfid) {
                                    startAmination(lavScanningPackageRfid);
                                    if (isScanningBoxRfid) {
                                        stopAmination(lavScanningBoxRfid);
                                        isScanningBoxRfid = false;
                                    }
                                    BluetoothUtil.tempScanResult.registerObserver(TransferBoxFragment.this, Constant.SCAN_PACKAGE_RFID);
                                } else {
                                    stopAmination(lavScanningPackageRfid);
                                    BluetoothUtil.tempScanResult.unregisterObserver(TransferBoxFragment.this);
                                }
                            } else {
                                stopAmination(lavScanningBoxRfid);
                                isScanningBoxRfid = false;
                                Toast.makeText(getActivity(), getActivity().getString(R.string.clear_all_box), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                        Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnScanBoxRfid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                BluetoothUtil.tempScanResult.getObservers().clear();
                    try {
                        if (!BluetoothUtil.isConnected) {
                            Toast.makeText(getActivity(), getString(R.string.no_bluetooth_connection), Toast.LENGTH_SHORT).show();
                        } else {
                            if (!tvPackageRfid.getText().toString().equals("")) {
                                isScanningBoxRfid = !isScanningBoxRfid;
                                if (isScanningBoxRfid) {
                                    startAmination(lavScanningBoxRfid);
                                    if (isScanningPackageRfid) {
                                        stopAmination(lavScanningPackageRfid);
                                        isScanningPackageRfid = false;
                                    }
                                    BluetoothUtil.tempScanResult.registerObserver(TransferBoxFragment.this, Constant.SCAN_BOX_RFID);
                                } else {
                                    stopAmination(lavScanningBoxRfid);
                                    BluetoothUtil.tempScanResult.unregisterObserver(TransferBoxFragment.this);
                                }
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.not_scan_package_rfid), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                        Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!NetworkUtil.isOnline(getActivity())) {
                            Toast.makeText(getActivity(), getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                        } else if (tvPackageRfid.getText().toString().equals("")) {
                            Toast.makeText(getActivity(), getString(R.string.not_scan_package_rfid), Toast.LENGTH_SHORT).show();
                        } else if (mListScanBoxRfid.isEmpty()) {
                            Toast.makeText(getActivity(), getText(R.string.not_scan_product_rfid), Toast.LENGTH_SHORT).show();
                        } else {
                            mRfimApi.transferBoxes(tvPackageRfid.getText().toString(), mListScanBoxRfid);
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                        Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        BluetoothUtil.tempScanResult.unregisterObserver(TransferBoxFragment.this);
                        tvPackageRfid.setText("");
                        mListShowProduct.clear();
                        mListScanBoxRfid.clear();
                        mProductAdapter = new ListProductAdapter(mListShowProduct);
                        rcListTransferBox.setAdapter(mProductAdapter);
                        stopAmination(lavScanningBoxRfid);
                        stopAmination(lavScanningPackageRfid);
                        isScanningBoxRfid = false;
                        isScanningPackageRfid = false;
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                        Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

        } catch (Exception ex) {
            Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
            Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        tvPackageRfid = getView().findViewById(R.id.tv_package_rfid);
        btnScanPackageRfid = getView().findViewById(R.id.btn_scan_package_rfid);
        btnScanBoxRfid = getView().findViewById(R.id.btn_scan_box_rfid);
        btnSave = getView().findViewById(R.id.btn_save);
        btnClear = getView().findViewById(R.id.btn_clear);
        btnCancel = getView().findViewById(R.id.btn_cancel);
        mRfimApi = new RFIMApi(this);
        mListScanBoxRfid = new ArrayList<>();
        mListShowProduct = new ArrayList<>();
        gson = new Gson();
        rcListTransferBox = getView().findViewById(R.id.rc_list_transfer_box);
        mBeepSound = MediaPlayer.create(getActivity(), R.raw.beep);
        lavScanningBoxRfid = getView().findViewById(R.id.lav_scanning_box_rfid);
        lavScanningBoxRfid.setAnimation(R.raw.scan);
        lavScanningBoxRfid.loop(true);
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

    public void onDestroyView() {
        super.onDestroyView();
        BluetoothUtil.tempScanResult.unregisterObserver(TransferBoxFragment.this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BluetoothUtil.tempScanResult.unregisterObserver(TransferBoxFragment.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //Notification function when scan rfid
    @Override
    public void getNotification(int type) {
        mBeepSound.start();
        Log.e(TAG, "getNotification: " + BluetoothUtil.tempScanResult.getRfidID());
        switch (type) {
            case Constant.SCAN_PACKAGE_RFID:
                mRfimApi.getPackageByPackageRfid(BluetoothUtil.tempScanResult.getRfidID());
                BluetoothUtil.tempScanResult.unregisterObserver(this);
                stopAmination(lavScanningPackageRfid);
                isScanningPackageRfid = false;
                break;
            case Constant.SCAN_BOX_RFID:
//                if (mListScanBoxRfid.indexOf(BluetoothUtil.tempScanResult.getRfidID()) < 0) {
//                    mRfimApi.getProductByBoxRfid(BluetoothUtil.tempScanResult.getRfidID());
//                }
                mRfimApi.getProductByBoxRfid(BluetoothUtil.tempScanResult.getRfidID());
                break;
        }
    }


    //Callback function when call api
    @Override
    public void onTaskCompleted(String data, int type, int code) {
        switch (type) {
            case Constant.GET_PACKAGE_BY_PACKAGE_RFID:
                if (code == HttpURLConnection.HTTP_OK) {
                    Package pac = gson.fromJson(data, Package.class);
                    mCurrentTransferProductid = pac.getProductId();
                    tvPackageRfid.setText(BluetoothUtil.tempScanResult.getRfidID());
                } else {
                    showResponseMessage(data);
                }
                break;
            case Constant.CHECK_BOX_INFO:
                if (code == HttpURLConnection.HTTP_OK) {
                    Product product = gson.fromJson(data, Product.class);
                    if (product.getProductId() != null && product.getProductId().equals(mCurrentTransferProductid)) {
                        if (mListScanBoxRfid.indexOf(BluetoothUtil.tempScanResult.getRfidID()) < 0) {
                            mListScanBoxRfid.add(BluetoothUtil.tempScanResult.getRfidID());
                            if (product.getProductId().equals(mCurrentTransferProductid)) {
                                if (mListShowProduct.isEmpty() && product.getProductId().equals(mCurrentTransferProductid)) {
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
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.transfer_wrong_product_id), Toast.LENGTH_SHORT).show();
                    }
                    mProductAdapter = new ListProductAdapter(mListShowProduct);
                    rcListTransferBox.setAdapter(mProductAdapter);
                } else {
                    showResponseMessage(data);
                }
                break;
            case Constant.TRANSFER_BOXES:
                if (code == HttpURLConnection.HTTP_OK) {
                    showResponseMessage(data);
                    tvPackageRfid.setText("");
                    mListShowProduct.clear();
                    mListScanBoxRfid.clear();
                    mProductAdapter = new ListProductAdapter(mListShowProduct);
                    rcListTransferBox.setAdapter(mProductAdapter);
                    stopAmination(lavScanningBoxRfid);
                    stopAmination(lavScanningPackageRfid);
                    isScanningBoxRfid = false;
                    isScanningPackageRfid = false;
                } else {
                    showResponseMessage(data);
                }
                break;
        }
    }

    //Check product is exit in list that show on mobile
    private int isProductExitInShowList(String productId) {
        int position = -1;
        for (ScannedProductItem item : mListShowProduct) {
            if (item.getProductId().equalsIgnoreCase(productId)) {
                position = mListShowProduct.indexOf(item);
            }
        }
        return position;
    }

    public void showResponseMessage(String data) {
        ResponseMessage message = gson.fromJson(data, ResponseMessage.class);
        if (message != null) {
            Toast.makeText(getActivity(), message.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
