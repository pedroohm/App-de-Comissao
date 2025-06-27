package com.grupo6.appdecomissao.activity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.Goal;
import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalViewHolder> {

    private List<Goal> goalsList;

    public GoalsAdapter(List<Goal> goalsList) {
        this.goalsList = goalsList;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goalsList.get(position);
        holder.bind(goal);
    }

    @Override
    public int getItemCount() {
        return goalsList.size();
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvBonus, tvStatus;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tv_item_goal_description);
            tvBonus = itemView.findViewById(R.id.tv_item_goal_bonus);
            tvStatus = itemView.findViewById(R.id.tv_item_goal_status);
        }

        public void bind(Goal goal) {
            tvDescription.setText(goal.getDescription());
            tvBonus.setText(String.format("%.2f", goal.getBonus()) + "%");

            if (goal.getAchieved()) {
                tvStatus.setText("Atingida");
                tvStatus.setTextColor(Color.parseColor("#009688")); // Verde
            } else {
                tvStatus.setText("Pendente");
                tvStatus.setTextColor(Color.RED);
            }
        }
    }
}
