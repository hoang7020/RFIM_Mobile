package vn.com.rfim_mobile.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import com.google.gson.Gson;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.adapter.IssueAdapter;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.InvoiceInfoItem;

import java.util.ArrayList;
import java.util.List;

public class IssueFragment extends Fragment implements OnTaskCompleted {

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
        return inflater.inflate(R.layout.fragment_issue, container, false);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        mListShowInvoice = (List<InvoiceInfoItem>) getArguments().getSerializable("LIST_INVOICE_ITEM");
        mIssueInvoiceAdapter = new IssueAdapter(mListShowInvoice);
        rcIssuseInvoice.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rcIssuseInvoice.setItemAnimator(new DefaultItemAnimator());
        rcIssuseInvoice.setAdapter(mIssueInvoiceAdapter);
    }

    private void initView() {
//        mToolbar = getView().findViewById(R.id.tb_issue);
//        rcIssuseInvoice = getView().findViewById(R.id.rc_issue_invoice);
        mListShowInvoice = new ArrayList<>();
//        btnSortAlgorithm = getView().findViewById(R.id.btn_sort_algorithm);
        mRfimApi = new RFIMApi(this);
        gson = new Gson();
    }

    @Override
    public void onTaskCompleted(String data, int type, int code) {

    }
}
