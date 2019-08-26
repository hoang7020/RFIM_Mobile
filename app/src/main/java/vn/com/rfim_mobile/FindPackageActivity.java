package vn.com.rfim_mobile;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.Observer;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.json.Package;
import vn.com.rfim_mobile.models.json.ResponseMessage;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FindPackageActivity extends AppCompatActivity implements Observer, OnTaskCompleted {

    public static final String TAG = FindPackageActivity.class.getSimpleName();

    private TextView tvPackageRfid,
            tvCellId,
            tvProductId,
            tvDate;
    private LinearLayout btnScanPackageRfid;
    private Button btnCancel;
    private RFIMApi mRfimApi;
    private MediaPlayer mBeepSound;
    private boolean isScanningPackageRfid = false;
    private LottieAnimationView lavScanningPackageRfid;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_package);

        try {

            initView();

            btnScanPackageRfid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!BluetoothUtil.isConnected) {
                            Toast.makeText(FindPackageActivity.this, getString(R.string.no_bluetooth_connection), Toast.LENGTH_SHORT).show();
                        } else {
                            isScanningPackageRfid = !isScanningPackageRfid;
                            if (isScanningPackageRfid) {
                                startAmination(lavScanningPackageRfid);
                                BluetoothUtil.tempScanResult.registerObserver(FindPackageActivity.this, Constant.SCAN_PACKAGE_RFID);
                            } else {
                                stopAmination(lavScanningPackageRfid);
                                BluetoothUtil.tempScanResult.unregisterObserver(FindPackageActivity.this);
                            }
                        }
                    } catch (Exception ex) {
                        Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BluetoothUtil.tempScanResult.unregisterObserver(FindPackageActivity.this);
                    finish();
                }
            });

        } catch (Exception ex) {
            Log.e(TAG, "TRY CATCH ALL: " + ex.getMessage());
            Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        tvPackageRfid = findViewById(R.id.tv_package_rfid);
        tvCellId = findViewById(R.id.tv_cell_id);
        tvProductId = findViewById(R.id.tv_product_id);
        tvDate = findViewById(R.id.tv_date);
        btnScanPackageRfid = findViewById(R.id.btn_scan_package_rfid);
        btnCancel = findViewById(R.id.btn_cancel);
        mRfimApi = new RFIMApi(this);
        gson = new Gson();
        mBeepSound = MediaPlayer.create(this, R.raw.beep);
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
        BluetoothUtil.tempScanResult.unregisterObserver(FindPackageActivity.this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BluetoothUtil.tempScanResult.unregisterObserver(FindPackageActivity.this);
        finish();
    }

    @Override
    public void getNotification(int type) {
        mBeepSound.start();
        switch (type) {
            case Constant.SCAN_PACKAGE_RFID:
                mRfimApi.getPackageByPackageRfid(BluetoothUtil.tempScanResult.getRfidID());
                BluetoothUtil.tempScanResult.unregisterObserver(FindPackageActivity.this);
                stopAmination(lavScanningPackageRfid);
                isScanningPackageRfid = false;
                break;
        }
    }

    @Override
    public void onTaskCompleted(String data, int type, int code) {
        Log.e(TAG, "onTaskCompleted: " + data);
        switch (type) {
            case Constant.GET_PACKAGE_BY_PACKAGE_RFID:
                if (code == HttpURLConnection.HTTP_OK) {
                    Package pac = gson.fromJson(data, Package.class);
                    if (pac != null) {
                        if (pac.getCellId() != null) {
                            tvPackageRfid.setText(pac.getPackageRfid());
                            tvCellId.setText(pac.getCellId());
                            tvProductId.setText(pac.getProductId());
                            tvDate.setText(convertDate(pac.getDate()));
                        } else {
                            tvPackageRfid.setText(pac.getPackageRfid());
                            tvCellId.setText("");
                            tvProductId.setText(pac.getProductId());
                            tvDate.setText("");
                        }
                    }
                } else {
                    showResponseMessage(data);
                }
                break;
        }
    }

    //Show response message
    public void showResponseMessage(String data) {
        ResponseMessage message = gson.fromJson(data, ResponseMessage.class);
        if (message != null) {
            Toast.makeText(this, message.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String convertDate(String data) {
        Log.e(TAG, "convertDate: " + data);
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = inputFormat.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = outputFormat.format(date);
//        System.out.println(formattedDate);
        return formattedDate;
    }
}
