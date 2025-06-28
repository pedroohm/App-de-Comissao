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

public class RegrasSupervisorActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RegrasSupervisorAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regras_supervisor);
        recyclerView = findViewById(R.id.recyclerRegrasSupervisor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Buscar regras apenas do DataCache local
        List<CommissionRule> regras = DataCache.getInstance().getAllCommissionRules();
        adapter = new RegrasSupervisorAdapter(regras);
        recyclerView.setAdapter(adapter);
    }
    // Classe auxiliar para exemplo
    public static class RegraComConsultores {
        public String nome, produto, percentual;
        public List<String> consultores;
        public RegraComConsultores(String nome, String produto, String percentual, List<String> consultores) {
            this.nome = nome;
            this.produto = produto;
            this.percentual = percentual;
            this.consultores = consultores;
        }
    }
} 