package com.grupo6.appdecomissao.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.grupo6.appdecomissao.R;

public class RulesSupervisor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rules_supervisor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            Log.d("RulesSupervisor", "Main view encontrado");
        } else {
            Log.e("RulesSupervisor", "Main view não encontrado!");
        }

        setupBottomNavigation();
        setupCardClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_regras); // Garante a seleção ao retomar
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupCardClickListeners() {
        CardView cardCadastro = findViewById(R.id.card_cadastro);
        if (cardCadastro != null) {
            Log.d("RulesSupervisor", "Card cadastro encontrado, configurando click listener");

            cardCadastro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("RulesSupervisor", "Card cadastro clicado, abrindo CadastroRegraActivity");
                    Toast.makeText(RulesSupervisor.this, "Abrindo cadastro de regra...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RulesSupervisor.this, CadastroRegraActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.e("RulesSupervisor", "Card cadastro não encontrado!");
        }

        CardView cardRegrasVigentes = findViewById(R.id.card_regras_vigentes);
        if (cardRegrasVigentes != null) {
            Log.d("RulesSupervisor", "Card regras vigentes encontrado, configurando click listener");

            cardRegrasVigentes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("RulesSupervisor", "Card regras vigentes clicado, abrindo RegrasSupervisorActivity");
                    Toast.makeText(RulesSupervisor.this, "Abrindo regras vigentes...", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RulesSupervisor.this, RegrasSupervisorActivity.class);

                    startActivity(intent);
                }
            });
        } else {
            Log.e("RulesSupervisor", "Card regras vigentes não encontrado!");
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_regras); // Define o item selecionado para esta tela

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                Intent profileIntent = new Intent(this, ProfileSettingsActivity.class);
                profileIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(profileIntent);
                return true;
            } else if (itemId == R.id.nav_regras) {
                return true; // Já estamos nesta tela, apenas consuma o evento
            } else {
                Intent dashboardIntent;
                // Como você não tinha uma lógica para nav_dashboard aqui, adicionei a que leva para o dashboard do supervisor por padrão,
                // já que esta é a RulesSupervisor. Você pode precisar ajustar isso se houver um dashboard de consultor também.
                dashboardIntent = new Intent(this, DashboardSupervisor.class);
                dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(dashboardIntent);
                return true;
            }
        });
    }
}