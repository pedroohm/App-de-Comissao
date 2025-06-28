package com.grupo6.appdecomissao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.grupo6.appdecomissao.R;

public class TesteRegrasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste_regras);

        Button btnCadastroRegra = findViewById(R.id.btnCadastroRegra);
        Button btnRegrasConsultor = findViewById(R.id.btnRegrasConsultor);
        Button btnRegrasSupervisor = findViewById(R.id.btnRegrasSupervisor);

        btnCadastroRegra.setOnClickListener(v -> {
            startActivity(new Intent(this, CadastroRegraActivity.class));
        });

        btnRegrasConsultor.setOnClickListener(v -> {
            startActivity(new Intent(this, RegrasConsultorActivity.class));
        });

        btnRegrasSupervisor.setOnClickListener(v -> {
            startActivity(new Intent(this, CommissionRuleAdapter.class));
        });
    }
} 