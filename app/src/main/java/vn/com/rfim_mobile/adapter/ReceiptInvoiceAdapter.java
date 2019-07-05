package vn.com.rfim_mobile.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.models.InvoiceInfoItem;

import java.util.List;

public class ReceiptInvoiceAdapter extends RecyclerView.Adapter<ReceiptInvoiceAdapter.RecyclerViewHolder>  {

    public static final String TAG = IssueInvoiceAdapter.class.getSimpleName();

    private List<InvoiceInfoItem> invoices;

    public ReceiptInvoiceAdapter(List<InvoiceInfoItem> invoices) {
        this.invoices = invoices;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_receipt_layout, viewGroup, false);
        return new ReceiptInvoiceAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int position) {
        InvoiceInfoItem product = invoices.get(position);
        recyclerViewHolder.tvProductNumber.setText(position + 1 + "");
        recyclerViewHolder.tvProductId.setText(product.getProductId());
        recyclerViewHolder.tvProductName.setText(product.getProductName());
        recyclerViewHolder.tvProductQuantity.setText(product.getQuantity() + "");
        Log.e(TAG, "onBindViewHolder: " + product.getProductName() + " " + product.getQuantity());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProductNumber, tvProductId, tvProductName, tvProductQuantity;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            tvProductNumber = itemView.findViewById(R.id.tv_product_number);
            tvProductId = itemView.findViewById(R.id.tv_product_id);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductQuantity = itemView.findViewById(R.id.tv_product_quantity);
        }
    }
}
