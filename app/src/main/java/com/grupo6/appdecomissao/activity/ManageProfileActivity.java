package com.grupo6.appdecomissao.activity;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.DataCache;
import com.grupo6.appdecomissao.domain.User;

public class ManageProfileActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, passwordEditText, confirmPasswordEditText;
    private ImageButton showPasswordButton, showConfirmPasswordButton;
    private ImageView profilePictureView;
    private boolean visiblePassword = false;
    private boolean visibleConfirmPassword = false;
    private DataCache dataCache = DataCache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mananger_profile_consultant);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputLayout tiName = (TextInputLayout) findViewById(R.id.ti_name);
        nameEditText= (TextInputEditText) tiName.getEditText();

        User user = dataCache.getUserById(dataCache.getCurrentId());
        String name = user.getName();

        nameEditText.setText(name);
    }

    public void saveInfo(View view){
        TextInputLayout tiName = (TextInputLayout) findViewById(R.id.ti_name);
        nameEditText = (TextInputEditText) tiName.getEditText();

        TextInputLayout tiPassword = (TextInputLayout) findViewById(R.id.ti_password);
        passwordEditText = (TextInputEditText) tiPassword.getEditText();

        TextInputLayout tiConfirmPassword = (TextInputLayout) findViewById(R.id.ti_confirm_password);
        confirmPasswordEditText = (TextInputEditText) tiConfirmPassword.getEditText();

        String name = nameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        User user = dataCache.getUserById("placeholderID");

        // Alterar nome para o que o Usuário definir
        user.setName(name);
    }

    // Chamado pelo botão de olho da senha
    public void onTogglePasswordClick(View view) {
        visiblePassword = !visiblePassword;
        togglePasswordVisibility(passwordEditText, showPasswordButton, visiblePassword);
    }

    // Chamado pelo botão de olho da confirmação da senha
    public void onToggleConfirmPasswordClick(View view) {
        visibleConfirmPassword = !visibleConfirmPassword;
        togglePasswordVisibility(confirmPasswordEditText, showConfirmPasswordButton, visibleConfirmPassword);
    }

    // Lógica de alternar visibilidade
    // ic_visibility e ic_visibility_off seriam imagens dos olhos abertos e fechados ao clicar
    // para a senha aparecer na tela
    private void togglePasswordVisibility(EditText editText, ImageButton toggleButton, boolean isVisible) {
        if (isVisible) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            // toggleButton.setImageResource(R.drawable.ic_visibility);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            // toggleButton.setImageResource(R.drawable.ic_visibility_off);
        }
        editText.setSelection(editText.getText().length());
    }
}
