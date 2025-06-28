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
        loadInitialData();
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
        if (email.equals("juan.freire@ufv.br")){
            Intent it = new Intent(this, ConsultantDashboardActivity.class);
            //Intent it = new Intent(this, TesteRegrasActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            dataCache.setCurrentId("84");
            startActivity(it);
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
        else if (email.equals("pedro.moura2@ufv.br")){
            Intent it = new Intent(this, DashboardSupervisor.class);
            //Intent it = new Intent(this, TesteRegrasActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            dataCache.setCurrentId("85");
            startActivity(it);
        }
        else {
            Toast.makeText(getApplicationContext(), "Não foi possível realizar o login", Toast.LENGTH_SHORT).show();
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
    private void loadInitialData() {
        Log.d("LOGIN", "Iniciando carregamento de dados iniciais");

        // Carregar usuários da API Rubeus
        Log.d("LOGIN", "Carregando usuários da API Rubeus...");
        dataCache.loadUsersFromApi("app", "token123", new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d("LOGIN", "Usuários carregados com sucesso da API Rubeus");

                // Carregar regras do servidor PHP
                Log.d("LOGIN", "Carregando regras do servidor PHP...");
                dataCache.loadRulesFromApi(new ApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Log.d("LOGIN", "Regras carregadas com sucesso do servidor PHP");
                        Log.d("LOGIN", "Total de usuários no cache: " + dataCache.getAllUsers().size());
                        Log.d("LOGIN", "Total de regras no cache: " + dataCache.getAllRules().size());

                        // Carregar metas e vendas mockadas
                        Log.d("LOGIN", "Carregando metas e vendas mockadas...");
                        dataCache.loadMockGoalsAndSales();
                        Log.d("LOGIN", "Metas e vendas mockadas carregadas");
                        Log.d("LOGIN", "Total de metas no cache: " + dataCache.getAllGoals().size());
                        Log.d("LOGIN", "Total de vendas no cache: " + dataCache.getAllSales().size());

                        Log.d("LOGIN", "Carregamento inicial concluído com sucesso");

                        // Habilitar botão de login após carregamento
                        runOnUiThread(() -> {
                            btnLogin.setEnabled(true);
                            Log.d("LOGIN", "Botão de login habilitado");
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("LOGIN", "Erro ao carregar regras: " + error);
                        // Continuar mesmo com erro nas regras
                        dataCache.loadMockGoalsAndSales();

                        // Habilitar botão mesmo com erro
                        runOnUiThread(() -> {
                            btnLogin.setEnabled(true);
                            Log.d("LOGIN", "Botão de login habilitado (com erro nas regras)");
                        });
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e("LOGIN", "Erro ao carregar usuários da API Rubeus: " + error);
                Log.d("LOGIN", "Carregando dados mockados como fallback...");

                // Carregar dados mockados quando a API falhar
                dataCache.loadMockUsers();
                dataCache.loadMockGoalsAndSales();

                Log.d("LOGIN", "Dados mockados carregados");
                Log.d("LOGIN", "Total de usuários mockados no cache: " + dataCache.getAllUsers().size());

                // Carregar regras do servidor PHP
                Log.d("LOGIN", "Carregando regras do servidor PHP...");
                dataCache.loadRulesFromApi(new ApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Log.d("LOGIN", "Regras carregadas com sucesso do servidor PHP");
                        Log.d("LOGIN", "Total de regras no cache: " + dataCache.getAllRules().size());

                        // Habilitar botão após carregamento
                        runOnUiThread(() -> {
                            btnLogin.setEnabled(true);
                            Log.d("LOGIN", "Botão de login habilitado (com dados mockados)");
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("LOGIN", "Erro ao carregar regras: " + error);

                        // Habilitar botão mesmo com erro
                        runOnUiThread(() -> {
                            btnLogin.setEnabled(true);
                            Log.d("LOGIN", "Botão de login habilitado (com dados mockados e erro nas regras)");
                        });
                    }
                });
            }
        });
    }
}