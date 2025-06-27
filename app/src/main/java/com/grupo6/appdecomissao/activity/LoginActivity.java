package com.grupo6.appdecomissao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.CommissionRule;
import com.grupo6.appdecomissao.domain.DataCache;
import com.grupo6.appdecomissao.domain.Goal;
import com.grupo6.appdecomissao.domain.User;
import com.grupo6.appdecomissao.remote.ApiCallback;
import com.grupo6.appdecomissao.remote.ApiRepository;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {
    // Classe com os métodos para requisição de dados
    private static final ApiRepository apiRepository = new ApiRepository();
    private static final DataCache dataCache = DataCache.getInstance();
    private static final String origin = "8";
    private static final String token = "b116d29f1252d2ce144d5cb15fb14c7f";

    private static final String TAG = "MainActivity";
    private TextInputEditText passwordEditText, emailEditText;

    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLogin = findViewById(R.id.bt_login);
        // Desabilita o botão enquanto não carregar tudo
        btnLogin.setEnabled(false);

        // Chamando o metodo para listar todos os usuários da plataforma e inicializar o DataCache
        loadApplicationData();
    }

    public void logar(View v){
        //Intent it = new Intent(this, ConsultantDashboardActivity.class);
        //Intent it = new Intent(this, ConsultantDashboardActivity.class);
        // startActivity(it);
        // Obtém as views e seta as variáveis de email e senha
        TextInputLayout tiEmail = (TextInputLayout) findViewById(R.id.ti_email);
        emailEditText = (TextInputEditText) tiEmail.getEditText();

        TextInputLayout tiPassword = (TextInputLayout) findViewById(R.id.ti_password);
        passwordEditText = (TextInputEditText) tiPassword.getEditText();

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.equals("juan.freire@ufv.br")){
            //Intent it = new Intent(this, ConsultantDashboardActivity.class);
            Intent it = new Intent(this, TesteRegrasActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            dataCache.setCurrentId("84");
            startActivity(it);
        }
        else if (email.equals("pedro.moura2@ufv.br")){
            //Intent it = new Intent(this, DashboardSupervisor.class);
            Intent it = new Intent(this, TesteRegrasActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            dataCache.setCurrentId("85");
            startActivity(it);
        }
        else {
            Toast.makeText(getApplicationContext(), "Não foi possível realizar o login", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadApplicationData() {
        Log.d(TAG, "Iniciando carregamento de dados do aplicativo...");

        // 1. Carrega os usuários da API Rubeus
        apiRepository.getAllUsers(origin, token, new ApiCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                for (User user : users) {
                    dataCache.putUser(user);
                }
                Log.d(TAG, "Usuários da Rubeus carregados. Total: " + users.size());

                // 2. Após carregar usuários, carrega as regras de comissão do nosso servidor PHP
                apiRepository.getCommissionRules(new ApiCallback<List<CommissionRule>>() {
                    @Override
                    public void onSuccess(List<CommissionRule> rules) {
                        for (CommissionRule rule : rules) {
                            dataCache.putCommissionRule(rule);
                        }
                        Log.d(TAG, "Regras de comissão do servidor mock carregadas. Total: " + rules.size());

                        // 3. Após carregar as regras, carrega as metas do nosso servidor PHP
                        apiRepository.getGoals(new ApiCallback<List<Goal>>() {
                            @Override
                            public void onSuccess(List<Goal> goals) {
                                for (Goal goal : goals) {
                                    dataCache.putGoal(goal);
                                }
                                Log.d(TAG, "Metas do servidor mock carregadas. Total: " + goals.size());

                                // 4. Somente após tudo carregar, habilita o botão de login
                                runOnUiThread(() -> {
                                    btnLogin.setEnabled(true);
                                });
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Log.e(TAG, "Erro ao carregar metas: " + errorMessage);
                                // Lidar com erro
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Erro ao carregar regras de comissão: " + errorMessage);
                        // Lidar com erro
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Erro ao carregar usuários da Rubeus: " + errorMessage);
                // Lidar com erro
            }
        });
    }
}