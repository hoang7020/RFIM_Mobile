package vn.com.rfim_mobile.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.models.ScannedProductItem;

import java.util.List;

public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.RecyclerViewHolder> {

    public static final String TAG = ListProductAdapter.class.getSimpleName();

    private List<ScannedProductItem> products;

    public ListProductAdapter(List<ScannedProductItem> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.list_product_layout, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int position) {
        ScannedProductItem product = products.get(position);
        recyclerViewHolder.tvProductNumber.setText(position + 1 + "");
        recyclerViewHolder.tvProductId.setText(product.getProductId());
        recyclerViewHolder.tvProductName.setText(product.getProductName());
        recyclerViewHolder.tvProductQuantity.setText(product.getQuantity() + "");
        Log.e(TAG, "onBindViewHolder: " + product.getProductName() + " " + product.getQuantity());
    }

    public void swap(List<ScannedProductItem> newData)
    {
        Log.e(TAG, "swap: ");
        products.clear();
        products.addAll(newData);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return products.size();
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
