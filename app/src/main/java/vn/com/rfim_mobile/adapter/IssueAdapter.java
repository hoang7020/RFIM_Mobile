package vn.com.rfim_mobile.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.api.RFIMApi;
import vn.com.rfim_mobile.models.InvoiceInfoItem;

import java.util.List;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.RecyclerViewHolder> {

    public static final String TAG = IssueAdapter.class.getSimpleName();
    protected RFIMApi mRfimApi;

    private List<InvoiceInfoItem> invoices;

    public IssueAdapter(List<InvoiceInfoItem> invoices, RFIMApi mRfimApi) {
        this.invoices = invoices;
        this.mRfimApi = mRfimApi;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_issue_layout, viewGroup, false);
        return new IssueAdapter.RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int position) {
        InvoiceInfoItem product = invoices.get(position);
        recyclerViewHolder.tvProductNumber.setText(position + 1 + "");
        recyclerViewHolder.tvProductId.setText(product.getProductId());
        recyclerViewHolder.tvProductName.setText(product.getProductName());
        recyclerViewHolder.tvProductQuantity.setText(product.getQuantity() + "");
//        recyclerViewHolder.tvProductPosition.setText(product.getShelfId());
        Log.e(TAG, "onBindViewHolder: " + product.getProductName() + " " + product.getQuantity());
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvProductNumber,
                tvProductId,
                tvProductName,
                tvProductQuantity;
//                tvProductPosition;
        private Context context;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            tvProductNumber = itemView.findViewById(R.id.tv_product_number);
            tvProductId = itemView.findViewById(R.id.tv_product_id);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductQuantity = itemView.findViewById(R.id.tv_product_quantity);
//            tvProductPosition = itemView.findViewById(R.id.tv_product_position);
            context = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, tvProductId.getText().toString(), Toast.LENGTH_SHORT).show();
            mRfimApi.suggestShelf(tvProductId.getText().toString());
        }
    }
}
