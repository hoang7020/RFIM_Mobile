package vn.com.rfim_mobile.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Toast;
import com.google.gson.Gson;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.adapter.IssueAdapter;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.interfaces.OnRunAlgorithmListener;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.InvoiceInfoItem;

import java.util.ArrayList;
import java.util.List;

public class IssueFragment extends Fragment implements OnTaskCompleted {

    public static final String TAG = IssueFragment.class.getSimpleName();

    private RecyclerView rcIssuseInvoice;
    private Toolbar mToolbar;
    private List<InvoiceInfoItem> mListShowInvoice;
    private IssueAdapter mIssueInvoiceAdapter;
    private Button btnSortAlgorithm;
    private RFIMApi mRfimApi;
    private Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_issue, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

        mToolbar.setTitle("Goods Issue");
        mToolbar.inflateMenu(R.menu.exit_button_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                DialogFragment currDialogFragment = (DialogFragment) getActivity().getSupportFragmentManager().findFragmentByTag("ISSUE");
                currDialogFragment.dismiss();
                return true;
            }
        });

        mListShowInvoice = (List<InvoiceInfoItem>) getArguments().getSerializable("LIST_INVOICE_ITEM");
        mIssueInvoiceAdapter = new IssueAdapter(mListShowInvoice, mRfimApi);
        rcIssuseInvoice.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rcIssuseInvoice.setItemAnimator(new DefaultItemAnimator());
        rcIssuseInvoice.setAdapter(mIssueInvoiceAdapter);
    }


    private void initView() {
//        mToolbar = getView().findViewById(R.id.tb_issue);
        rcIssuseInvoice = getView().findViewById(R.id.rc_issue_invoice_v2);
        mToolbar = getView().findViewById(R.id.tb_issue);
        mListShowInvoice = new ArrayList<>();
//        btnSortAlgorithm = getView().findViewById(R.id.btn_sort_algorithm);
        mRfimApi = new RFIMApi(this);
        gson = new Gson();
    }

    @Override
    public void onTaskCompleted(String data, int type, int code) {
        IssueInvoiceFragmentV2.mOnRunAlgorithmListener.onRunAlgorithm(data);
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
