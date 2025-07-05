package com.grupo6.appdecomissao.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.activity.CommissionRuleAdapter;
import com.grupo6.appdecomissao.domain.CommissionRule;
import com.grupo6.appdecomissao.domain.DataCache;
import java.util.List;

public class RegrasSupervisorActivity extends AppCompatActivity implements CommissionRuleAdapter.OnDeleteClickListener {

    private RecyclerView recyclerView;
    private CommissionRuleAdapter adapter;
    private List<CommissionRule> todasAsRegras;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regras_supervisor);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_regras_supervisor);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerRegrasSupervisor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Busca todas as regras de comissão disponíveis no DataCache
        todasAsRegras = DataCache.getInstance().getAllCommissionRules();

        // Passamos 'true' para indicar que, na visão do supervisor, queremos ver a lista de consultores atribuídos
        adapter = new CommissionRuleAdapter(todasAsRegras, true);
        adapter.setOnDeleteClickListener(this); // Configura o listener

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDeleteClick(final int position) {
        // Pega a regra que será removida para mostrar o nome no diálogo
        CommissionRule ruleToDelete = todasAsRegras.get(position);

        // Cria o diálogo de confirmação
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja remover a regra \"" + ruleToDelete.getName() + "\"?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    // Ação a ser executada se o usuário clicar em "Sim"
                    // Remove a regra do DataCache
                    DataCache.getInstance().removeCommissionRule(ruleToDelete.getId());

                    // Remove a regra da lista local que o adapter está usando
                    todasAsRegras.remove(position);

                    // Notifica o adapter que um item foi removido para atualizar a UI
                    adapter.notifyItemRemoved(position);

                    Toast.makeText(RegrasSupervisorActivity.this, "Regra removida com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Não", null) // Não faz nada se o usuário clicar em "Não"
                .setIcon(R.drawable.ic_delete)
                .show();
    }
}