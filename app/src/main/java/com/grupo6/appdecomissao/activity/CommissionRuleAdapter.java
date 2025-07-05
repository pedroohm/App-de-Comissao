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
import com.grupo6.appdecomissao.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import android.widget.ImageView;

public class CommissionRuleAdapter extends RecyclerView.Adapter<CommissionRuleAdapter.RuleViewHolder> {

    private List<CommissionRule> ruleList;
    private boolean showAssignedConsultants;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    public CommissionRuleAdapter(List<CommissionRule> ruleList, boolean showAssignedConsultants) {
        this.ruleList = ruleList;
        this.showAssignedConsultants = showAssignedConsultants;
    }

    @NonNull
    @Override
    public RuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commission_rule, parent, false);
        return new RuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RuleViewHolder holder, int position) {
        CommissionRule rule = ruleList.get(position);
        holder.bind(rule, this.showAssignedConsultants, onDeleteClickListener);
    }

    @Override
    public int getItemCount() {
        return ruleList.size();
    }

    static class RuleViewHolder extends RecyclerView.ViewHolder {
        TextView ruleName, ruleDescription, rulePercentage, ruleConsultants;
        ImageView deleteIcon;

        public RuleViewHolder(@NonNull View itemView) {
            super(itemView);
            ruleName = itemView.findViewById(R.id.tv_rule_name);
            ruleDescription = itemView.findViewById(R.id.tv_rule_description);
            rulePercentage = itemView.findViewById(R.id.tv_rule_percentage);
            ruleConsultants = itemView.findViewById(R.id.tv_rule_consultants);
            deleteIcon = itemView.findViewById(R.id.iv_delete_rule);
        }

        public void bind(CommissionRule rule, boolean showConsultants, final OnDeleteClickListener listener) {
            ruleName.setText(rule.getName());
            ruleDescription.setText(rule.getDescription());
            rulePercentage.setText("Percentual: " + String.format("%.2f", rule.getCommissionPercentage()) + "%");

            if (showConsultants) {
                deleteIcon.setVisibility(View.VISIBLE);
                ruleConsultants.setVisibility(View.VISIBLE);

                if (rule.getAssignedConsultantIds() != null && !rule.getAssignedConsultantIds().isEmpty()) {
                    StringBuilder namesBuilder = new StringBuilder();
                    boolean isFirst = true;
                    for (String id : rule.getAssignedConsultantIds()) {
                        User user = DataCache.getInstance().getUserById(id);
                        String name = (user != null) ? user.getName().split(" ")[0] : "";
                        if (!name.isEmpty()) {
                            if (!isFirst) {
                                namesBuilder.append(", ");
                            }
                            namesBuilder.append(name);
                            isFirst = false;
                        }
                    }
                    ruleConsultants.setText("Atribuído a: " + namesBuilder.toString());
                } else {
                    ruleConsultants.setText("Atribuído a: Nenhum consultor");
                }

                deleteIcon.setOnClickListener(v -> {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                });

            } else {
                ruleConsultants.setVisibility(View.GONE);
                deleteIcon.setVisibility(View.GONE);
            }
        }
    }
}