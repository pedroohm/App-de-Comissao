package com.grupo6.appdecomissao.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.Sale;
import java.util.List;

public class SalesHistoryAdapter extends RecyclerView.Adapter<SalesHistoryAdapter.SaleViewHolder> {

    private List<Sale> salesList;

    public SalesHistoryAdapter(List<Sale> salesList) {
        this.salesList = salesList;
    }

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale_history, parent, false);
        return new SaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleViewHolder holder, int position) {
        Sale sale = salesList.get(position);
        holder.bind(sale);
    }

    @Override
    public int getItemCount() {
        return salesList.size();
    }

    static class SaleViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvProduct, tvPrice, tvCommission;

        public SaleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_item_id);
            tvProduct = itemView.findViewById(R.id.tv_item_product);
            tvPrice = itemView.findViewById(R.id.tv_item_price);
            tvCommission = itemView.findViewById(R.id.tv_item_commission);
        }

        public void bind(Sale sale) {
            tvId.setText(sale.getId());
            tvProduct.setText(sale.getProduct());
            tvPrice.setText("R$ " + String.format("%.2f", sale.getPrice()));
            tvCommission.setText("R$ " + String.format("%.2f", sale.getCommission()));
        }
    }
}
