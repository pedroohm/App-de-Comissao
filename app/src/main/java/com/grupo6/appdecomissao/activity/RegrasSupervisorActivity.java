package com.grupo6.appdecomissao.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.activity.CommissionRuleAdapter;
import com.grupo6.appdecomissao.domain.CommissionRule;
import com.grupo6.appdecomissao.domain.DataCache;
import java.util.List;

public class RegrasSupervisorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommissionRuleAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define o layout que esta activity vai usar
        setContentView(R.layout.activity_regras_supervisor);

        // Configura a Toolbar para o botão "voltar" funcionar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_regras_supervisor);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Configura o RecyclerView
        recyclerView = findViewById(R.id.recyclerRegrasSupervisor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Busca todas as regras de comissão disponíveis no DataCache
        List<CommissionRule> todasAsRegras = DataCache.getInstance().getAllCommissionRules();

        // Inicializa o adapter com a lista completa de regras
        // Passamos 'true' para indicar que, na visão do supervisor, queremos ver a lista de consultores atribuídos
        adapter = new CommissionRuleAdapter(todasAsRegras, true);

        // Define o adapter no RecyclerView para que os itens sejam exibidos na tela
        recyclerView.setAdapter(adapter);
    }
}