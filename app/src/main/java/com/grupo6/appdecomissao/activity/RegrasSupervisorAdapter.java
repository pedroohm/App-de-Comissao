package com.grupo6.appdecomissao.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.CommissionRule;
import com.grupo6.appdecomissao.domain.DataCache;
import java.util.List;

public class RegrasSupervisorAdapter extends RecyclerView.Adapter<RegrasSupervisorAdapter.RegraViewHolder> {
    private List<CommissionRule> regras;
    public RegrasSupervisorAdapter(List<CommissionRule> regras) {
        this.regras = regras;
    }
    @NonNull
    @Override
    public RegraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_regra_supervisor, parent, false);
        return new RegraViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RegraViewHolder holder, int position) {
        CommissionRule regra = regras.get(position);
        holder.tvNome.setText(regra.getName());
        holder.tvProduto.setText(DataCache.getInstance().getProductNameByRule(regra));
        holder.tvPercentual.setText(regra.getCommissionPercentage() + "%");
        // Lista de consultores associados
        List<String> nomesConsultores = DataCache.getInstance().getConsultantNamesByRule(regra);
        holder.tvConsultores.setText("Consultores: " + String.join(", ", nomesConsultores));
    }
    @Override
    public int getItemCount() {
        return regras.size();
    }
    public static class RegraViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvProduto, tvPercentual, tvConsultores;
        public RegraViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomeRegraSupervisor);
            tvProduto = itemView.findViewById(R.id.tvProdutoRegraSupervisor);
            tvPercentual = itemView.findViewById(R.id.tvPercentualRegraSupervisor);
            tvConsultores = itemView.findViewById(R.id.tvConsultoresRegraSupervisor);
        }
    }
} 