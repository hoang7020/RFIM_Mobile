package vn.com.rfim_mobile.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.adapter.IssueInvoiceAdapter;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.InvoiceInfoItem;

import java.util.ArrayList;
import java.util.List;

public class IssueInvoiceFragment extends DialogFragment implements OnTaskCompleted {

    private static final String TAG = IssueInvoiceFragment.class.getSimpleName();

    private RecyclerView rcIssuseInvoice;
    private List<InvoiceInfoItem> mListShowInvoice;
    private IssueInvoiceAdapter mIssueInvoiceAdapter;
    private Button btnSortAlgorithm;
    private RFIMApi mRfimApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.issue_invoice_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        mListShowInvoice = (List<InvoiceInfoItem>) getArguments().getSerializable("LIST_INVOICE_ITEM");
        mIssueInvoiceAdapter = new IssueInvoiceAdapter(mListShowInvoice);
        rcIssuseInvoice.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rcIssuseInvoice.setItemAnimator(new DefaultItemAnimator());
        rcIssuseInvoice.setAdapter(mIssueInvoiceAdapter);

        btnSortAlgorithm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                List<String> a = new ArrayList<>();
//                a.add("BRJP001");
//                a.add("AQJP001");
//                a.add("CACE150");
                mRfimApi.sortIssueInvoice(mListShowInvoice);
            }
        });
    }

    private void initView() {
        rcIssuseInvoice = getView().findViewById(R.id.rc_issue_invoice);
        mListShowInvoice = new ArrayList<>();
        btnSortAlgorithm = getView().findViewById(R.id.btn_sort_algorithm);
        mRfimApi = new RFIMApi(this);
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

    @Override
    public void onTaskCompleted(String data, int type, int code) {
        switch (type) {
            case Constant.DIJKSTRA:
                Log.e(TAG, "onTaskCompleted: " + data);
                break;
        }
    }
}
