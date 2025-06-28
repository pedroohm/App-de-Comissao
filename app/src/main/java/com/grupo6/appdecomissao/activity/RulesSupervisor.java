package com.grupo6.appdecomissao.activity;

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

        // Teste para verificar se os views estão sendo encontrados
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            Log.d("RulesSupervisor", "Main view encontrado");
        } else {
            Log.e("RulesSupervisor", "Main view não encontrado!");
        }

        setupBottomNavigation();
        setupCardClickListeners();
    }

    private void setupCardClickListeners() {
        // Card para cadastrar nova regra
        CardView cardCadastro = findViewById(R.id.card_cadastro);
        if (cardCadastro != null) {
            Log.d("RulesSupervisor", "Card cadastro encontrado, configurando click listener");
            
            // Teste com OnTouchListener como alternativa
            cardCadastro.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, android.view.MotionEvent event) {
                    if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                        Intent it = new Intent(RulesSupervisor.this, CadastroRegraActivity.class);
                        startActivity(it);

                        //Log.d("RulesSupervisor", "Card cadastro tocado");
                        //Toast.makeText(RulesSupervisor.this, "Card cadastro tocado!", Toast.LENGTH_SHORT).show();
                        //return true;
                    }
                    return false;
                }
            });
            
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

        // Card para mostrar regras vigentes
        CardView cardRegrasVigentes = findViewById(R.id.card_regras_vigentes);
        if (cardRegrasVigentes != null) {
            Log.d("RulesSupervisor", "Card regras vigentes encontrado, configurando click listener");
            
            // Teste com OnTouchListener como alternativa
            cardRegrasVigentes.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, android.view.MotionEvent event) {
                    if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                        Intent it = new Intent(RulesSupervisor.this, RegrasSupervisorActivity.class);
                        startActivity(it);
                        Log.d("RulesSupervisor", "Card regras vigentes tocado");
                        //Toast.makeText(RulesSupervisor.this, "Card regras vigentes tocado!", Toast.LENGTH_SHORT).show();
                        //return true;
                    }
                    return false;
                }
            });
            
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

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                Intent profileIntent = new Intent(this, ProfileSettingsActivity.class);
                profileIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(profileIntent);
                return true;
            } else if (itemId == R.id.nav_regras) {
                // Já estamos na tela de regras, não fazer nada
                return true;
            } else {
                return false;
            }
        });

        // Marcar o item de regras como selecionado
        bottomNavigationView.setSelectedItemId(R.id.nav_regras);
    }
}