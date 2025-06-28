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
        // 1. Obter os dados de entrada do usuário
        TextInputLayout tiEmail = findViewById(R.id.ti_email);
        emailEditText = (TextInputEditText) tiEmail.getEditText();

        TextInputLayout tiPassword = findViewById(R.id.ti_password);
        passwordEditText = (TextInputEditText) tiPassword.getEditText();

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        // 2. Validar se os campos não estão vazios
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Por favor, preencha e-mail e senha.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Buscar o usuário no DataCache pelo e-mail fornecido
        User user = dataCache.getUserByEmail(email);

        // 4. Verificar se o usuário existe
        if (user == null) {
            Toast.makeText(getApplicationContext(), "Usuário não encontrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 5. Verificar se a senha está correta
        if (user.getPassword().equals(password)) {
            // Senha correta, proceder com o login
            Toast.makeText(getApplicationContext(), "Login bem-sucedido!", Toast.LENGTH_SHORT).show();

            // Armazena o ID do usuário logado para uso em outras telas
            dataCache.setCurrentId(user.getId());

            // 6. Redirecionar para a tela correta com base no perfil do usuário
            Intent intent;
            if ("Supervisor".equalsIgnoreCase(user.getProfile())) {
                intent = new Intent(this, DashboardSupervisor.class);
            } else {
                intent = new Intent(this, ConsultantDashboardActivity.class);
            }

            // Limpa as atividades anteriores para que o usuário não possa "voltar" para a tela de login
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Finaliza a LoginActivity

        } else {
            // Senha incorreta
            Toast.makeText(getApplicationContext(), "Senha incorreta.", Toast.LENGTH_SHORT).show();
        }
    }

    public void openForgotPasswordActivity(View view) {
        // Cria uma Intent (uma "intenção" de ir para outra tela)
        Intent intent = new Intent(this, ForgotPasswordActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        // Inicia a nova activity

        startActivity(intent);
    }

    private void loadApplicationData() {
        Log.d(TAG, "Iniciando carregamento de dados do aplicativo...");

        // 1. Carrega os usuários da API Rubeus
        apiRepository.getUsers(new ApiCallback<List<User>>() {
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