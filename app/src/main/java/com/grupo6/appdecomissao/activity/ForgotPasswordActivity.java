package com.grupo6.appdecomissao.activity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.DataCache;
import com.grupo6.appdecomissao.domain.User;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private Button btSendEmail;
    private DataCache dataCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Configuração padrão para a UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa os componentes da UI e o DataCache
        initializeComponents();

        // Configura o listener de clique para o botão
        setupButtonClick();
    }

    private void initializeComponents() {
        TextInputLayout tiEmail = findViewById(R.id.ti_email);
        emailEditText = (TextInputEditText) tiEmail.getEditText();
        btSendEmail = findViewById(R.id.bt_send_email);
        dataCache = DataCache.getInstance();
    }

    private void setupButtonClick() {
        btSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPasswordResetLink();
            }
        });
    }

    private void sendPasswordResetLink() {
        String email = emailEditText.getText().toString().trim();

        // Valida a entrada do usuário
        if (email.isEmpty()) {
            emailEditText.setError("O e-mail não pode estar vazio.");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Por favor, insira um e-mail válido.");
            emailEditText.requestFocus();
            return;
        }

        // Verifica se o e-mail existe no nosso DataCache
        User user = dataCache.getUserByEmail(email);

        // Exibe uma mensagem genérica para o usuário
        Toast.makeText(this, "Se o e-mail estiver cadastrado, um link para redefinição de senha será enviado.", Toast.LENGTH_LONG).show();

        // Fecha a tela e volta para a tela de login após um pequeno atraso
        new android.os.Handler().postDelayed(
                this::finish,
                3000
        );
    }
}