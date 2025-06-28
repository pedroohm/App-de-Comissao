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
            Intent it = new Intent(this, ConsultantDashboardActivity.class);
            //Intent it = new Intent(this, TesteRegrasActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            dataCache.setCurrentId("84");
            startActivity(it);
        }
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