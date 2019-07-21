package vn.com.rfim_mobile.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.interfaces.OnRunAlgorithmListener;
import vn.com.rfim_mobile.interfaces.OnTurnBackListener;
import vn.com.rfim_mobile.models.InvoiceInfoItem;

import java.io.Serializable;
import java.util.List;

public class IssueInvoiceFragmentV2 extends DialogFragment implements OnRunAlgorithmListener, OnTurnBackListener {

    private static final String TAG = IssueInvoiceFragment.class.getSimpleName();

    private List<InvoiceInfoItem> mListShowInvoice;
    public static OnRunAlgorithmListener mOnRunAlgorithmListener;
    public static OnTurnBackListener mOntuOnTurnBackListener;

    private IssueFragment mIssueFragment;
    private Bundle bundle;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.issue_invoice_fragment_v2, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

        mListShowInvoice = (List<InvoiceInfoItem>) getArguments().getSerializable("LIST_INVOICE_ITEM");
        bundle = new Bundle();
        bundle.putSerializable("LIST_INVOICE_ITEM", (Serializable) mListShowInvoice);
        mIssueFragment = new IssueFragment();
        mIssueFragment.setArguments(bundle);
        loadFragment(mIssueFragment);
    }

    private void initView() {
        mOnRunAlgorithmListener = this;
        mOntuOnTurnBackListener = this;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onRunAlgorithm(String data) {
        Bundle bundle = new Bundle();
        bundle.putString("LIST_ALGORITHM_RESULT", data);
        AlgorithmResultFragment fragment = new AlgorithmResultFragment();
        fragment.setArguments(bundle);
        loadFragment(fragment);
    }

    @Override
    public void onTurnBack() {
        loadFragment(mIssueFragment);
    }
}
