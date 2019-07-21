package vn.com.rfim_mobile.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.adapter.IssueAdapter;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.constants.Constant;
import vn.com.rfim_mobile.interfaces.OnTaskCompleted;
import vn.com.rfim_mobile.models.InvoiceInfoItem;
import vn.com.rfim_mobile.models.json.Invoice;

import java.util.ArrayList;
import java.util.List;

public class IssueInvoiceFragment extends DialogFragment implements OnTaskCompleted {

    private static final String TAG = IssueInvoiceFragment.class.getSimpleName();

    private RecyclerView rcIssuseInvoice;
    private Toolbar mToolbar;
    private List<InvoiceInfoItem> mListShowInvoice;
    private IssueAdapter mIssueInvoiceAdapter;
    private Button btnSortAlgorithm;
    private RFIMApi mRfimApi;
    private Gson gson;

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

        mToolbar.setTitle("Issue Invoice");
        mToolbar.inflateMenu(R.menu.exit_button_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                dismiss();
                return true;
            }
        });

//        mListShowInvoice = (List<InvoiceInfoItem>) getArguments().getSerializable("LIST_INVOICE_ITEM");
//        mIssueInvoiceAdapter = new IssueAdapter(mListShowInvoice);
//        rcIssuseInvoice.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
//        rcIssuseInvoice.setItemAnimator(new DefaultItemAnimator());
//        rcIssuseInvoice.setAdapter(mIssueInvoiceAdapter);

        mRfimApi.sortIssueInvoice(mListShowInvoice);

//        btnSortAlgorithm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mRfimApi.sortIssueInvoice(mListShowInvoice);
//            }
//        });
    }

    private void initView() {
        mToolbar = getView().findViewById(R.id.tb_issue);
        rcIssuseInvoice = getView().findViewById(R.id.rc_issue_invoice);
        mListShowInvoice = new ArrayList<>();
//        btnSortAlgorithm = getView().findViewById(R.id.btn_sort_algorithm);
        mRfimApi = new RFIMApi(this);
        gson = new Gson();
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
//        switch (type) {
//            case Constant.DIJKSTRA:
//                Log.e(TAG, "onTaskCompleted: " + data);
//                mListShowInvoice = gson.fromJson(data, new TypeToken<List<InvoiceInfoItem>>(){}.getType());
//                mIssueInvoiceAdapter = new IssueAdapter(mListShowInvoice);
//                rcIssuseInvoice.setAdapter(mIssueInvoiceAdapter);
//                break;
//        }
    }
}
