package vn.com.rfim_mobile;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import vn.com.rfim_mobile.fragments.TransferBoxFragment;
import vn.com.rfim_mobile.fragments.TransferPackageFragment;
import vn.com.rfim_mobile.utils.Bluetooth.BluetoothUtil;

public class TransferProductActivity extends AppCompatActivity {

    public static final String TAG = TransferProductActivity.class.getSimpleName();

    private BottomNavigationView bnvTransferProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_product);

        initView();

        loadFragment(new TransferBoxFragment());

        bnvTransferProduct.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_transfer_box:
                        fragment = new TransferBoxFragment();
                        loadFragment(fragment);
                        break;
                    case R.id.action_transfer_package:
                        fragment = new TransferPackageFragment();
                        loadFragment(fragment);
                        break;
                }
                return false;
            }
        });
    }

    private void initView() {
        bnvTransferProduct = findViewById(R.id.bnv_transfer_product);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
