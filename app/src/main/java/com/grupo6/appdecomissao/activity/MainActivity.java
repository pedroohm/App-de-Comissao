package com.grupo6.appdecomissao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.CommissionRule;
import com.grupo6.appdecomissao.domain.DataCache;
import com.grupo6.appdecomissao.domain.Goal;
import com.grupo6.appdecomissao.domain.User;
import com.grupo6.appdecomissao.remote.ApiCallback;
import com.grupo6.appdecomissao.remote.ApiRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends Activity {

    // Classe com os métodos para requisição de dados
    private static final ApiRepository apiRepository = new ApiRepository();
    private static final DataCache dataCache = DataCache.getInstance();
    private static final String origin = "8";
    private static final String token = "b116d29f1252d2ce144d5cb15fb14c7f";

    private static final String TAG = "MainActivity";

    private Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        // Desabilita o botão enquanto não carregar tudo
        btnLogin.setEnabled(false);

        // Chamando o metodo para listar todos os usuários da plataforma e inicializar o DataCache
        listAllUsersAndInitialize();
    }

    public void login(View v){
        Intent it = new Intent(this, ConsultantDashboardActivity.class);
        startActivity(it);
    }

    private void listAllUsersAndInitialize() {
        Log.d("MainActivity", "Fazendo requisição para coletar todos os usuários da plataforma");
        apiRepository.getAllUsers(origin, token, new ApiCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                for (User user : users) {
                    dataCache.putUser(user);
                }

                Log.d(TAG, "Usuários adicionados ao DataCache. Total: " + users.size());
                Log.d("MainActivity", "-----------------------------------");

                // Metodo para adicionar regras de comissão para todos os usuários consultores da plataforma
                createComissionRules();

                // Metodo para adicionar metas para todos os usuários consultores da plataforma
                createGoals();

                runOnUiThread(() -> btnLogin.setEnabled(true));
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Erro: " + errorMessage);
                });
            }
        });
    }

    private void createComissionRules() {
        Log.d("MainActivity", "Criando regras de comissão");

        // Consultores associados (exemplo)
        Set<String> consultants = new HashSet<>(Arrays.asList("84", "85", "86", "87"));

        // Criando a primeira regra de comissão
        CommissionRule rule1 = new CommissionRule(
                "1",
                "1",
                "Matriculado",
                "Comissão por Matrícula Direta",
                "Gera comissão quando um aluno é matriculado.",
                consultants,
                "Percentual Fixo",
                "Trimestral",
                15.0
        );

        // Criando a segunda regra de comissão
        CommissionRule rule2 = new CommissionRule(
                "2",
                "3",
                "Venda Concluída",
                "Comissão por Venda",
                "Gera comissão quando um curso livre é vendido.",
                consultants,
                "Percentual Fixo",
                "Trimestral",
                20
        );

        // Adicionando as regras ao DataCache
        dataCache.putCommissionRule(rule1);
        dataCache.putCommissionRule(rule2);

        Log.d("MainActivity", "Regra 1 adicionada: " + rule1.getName());
        Log.d("MainActivity", "Regra 2 adicionada: " + rule2.getName());
        Log.d("MainActivity", "-----------------------------------");
    }

    private void createGoals() {
        Log.d("MainActivity", "Criando metas para os usuários");

        // Consultores associados (exemplo)
        Set<String> consultants = new HashSet<>(Arrays.asList("84", "85", "86", "87"));

        Goal goal1 = new Goal(
                "1",
                consultants,
                "Vender 20 produtos",
                20.0,
                10.0,
                false
        );

        Goal goal2 = new Goal(
                "2",
                consultants,
                "Vender 10 produtos",
                10.0,
                15.0,
                false
        );

        // Adicionando Metas ao DataCache
        dataCache.putGoal(goal1);
        dataCache.putGoal(goal2);

        Log.d("MainActivity", "Meta 1 adicionada: " + goal1.getDescription());
        Log.d("MainActivity", "Meta 2 adicionada: " + goal2.getDescription());
        Log.d("MainActivity", "-----------------------------------");
    }
}