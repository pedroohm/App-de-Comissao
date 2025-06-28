package com.grupo6.appdecomissao.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.CommissionRule;
import com.grupo6.appdecomissao.domain.DataCache;

import java.util.ArrayList;
import java.util.List;

public class RegrasConsultorActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommissionRuleAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regras_consultor);
        recyclerView = findViewById(R.id.recyclerRegrasConsultor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String userId = DataCache.getInstance().getCurrentId();
        if (userId == null) {
            Toast.makeText(this, "Nenhum usuário logado.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buscar regras apenas do DataCache local
        List<CommissionRule> todasRegras = DataCache.getInstance().getAllCommissionRules();
        List<CommissionRule> minhasRegras = new ArrayList<>();
        
        for (CommissionRule rule : todasRegras) {
            if (rule.getAssignedConsultantIds().contains(userId)) {
                minhasRegras.add(rule);
            }
        }

        adapter = new CommissionRuleAdapter(minhasRegras, false);
        recyclerView.setAdapter(adapter);
    }
} 