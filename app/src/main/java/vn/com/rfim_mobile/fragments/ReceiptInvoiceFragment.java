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
import android.view.*;
import android.widget.Button;
import com.google.gson.Gson;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.adapter.IssueAdapter;
import vn.com.rfim_mobile.adapter.ReceiptAdapter;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.models.InvoiceInfoItem;

import java.util.ArrayList;
import java.util.List;

public class ReceiptInvoiceFragment extends DialogFragment {

    private RecyclerView rcReceipt;
    private List<InvoiceInfoItem> mListShowReceipts;
    private ReceiptAdapter mReceiptAdapter;
    private Toolbar mToolbar;
    private Button btnSortAlgorithm;
    private RFIMApi mRfimApi;
    private Gson gson;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.receipt_invoice_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

        mToolbar.setTitle("Receipt Invoice");
        mToolbar.inflateMenu(R.menu.exit_button_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                dismiss();
                return true;
            }
        });

        mListShowReceipts = (List<InvoiceInfoItem>) getArguments().getSerializable("LIST_RECEIPT_ITEM");
        mReceiptAdapter = new ReceiptAdapter(mListShowReceipts);
        rcReceipt.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rcReceipt.setItemAnimator(new DefaultItemAnimator());
        rcReceipt.setAdapter(mReceiptAdapter);

    }

    private void initView() {
        mToolbar = getView().findViewById(R.id.tb_receipt);
        rcReceipt = getView().findViewById(R.id.rc_receipe_invoice);
        mListShowReceipts = new ArrayList<>();
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
}
