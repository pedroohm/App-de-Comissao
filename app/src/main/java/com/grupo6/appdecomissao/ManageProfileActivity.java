package com.grupo6.appdecomissao;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ManageProfileActivity extends Activity {

    private EditText nameEditText, passwordEditText, confirmPasswordEditText;
    private ImageButton showPasswordButton, showConfirmPasswordButton;
    private ImageView profilePictureView;
    private boolean visiblePassword = false;
    private boolean visibleConfirmPassword = false;
    private DataCache dataCache = DataCache.getInstance();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Trocar o placeholder quando tivermos os xmls das telas
        setContentView(R.layout.placeholderxml);

        nameEditText = (EditText) findViewById(R.id.placeholdernametxt);
        profilePictureView = (ImageView) findViewById(R.id.placeholderpfpimageview);

        User user = dataCache.getUserById("placeholderID");
        String name = user.getName();
        String picture = user.getPicture();

        // Seta o nome e a foto do usuário na tela (acho que a lógica da foto
        // não vai funcionar do jeito que está, mas depois pensamos nisso)
        nameEditText.setText(name);
        profilePictureView.setImageResource(picture); // Creio que para usar esse metodo a foto deverá estar na Drawable
    }

    public void saveInfo(View view){
        passwordEditText = (EditText) findViewById(R.id.placeholderpasswordtxt);
        confirmPasswordEditText = (EditText) findViewById(R.id.placeholderconfirmpasswordtxt);

        String name = nameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        User user = dataCache.getUserById("placeholderID");

        if (password.equals(confirmPassword)) {
            // Muda a senha (não sei se vamos fazer isso no BD ou mudar pela API
        }

        // Alterar nome para o que o Usuário definir (assumindo que podemos trocar o nome)
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
            toggleButton.setImageResource(R.drawable.ic_visibility);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleButton.setImageResource(R.drawable.ic_visibility_off);
        }
        editText.setSelection(editText.getText().length());
    }

    // Quando o usuário quiser remover sua foto de perfil, colocar
    // uma default da Rubeus
    public void removeProfilePicture(View view){
        User user = dataCache.getUserById("placeholderID");
        user.setPicture("placeholderDefaultPicture");
        profilePictureView.setImageResource("placeholderDefaultPicture");
    }

    // Não pensei na lógica de update da foto de perfil ainda,
    // então deixei o metodo igual ao de cima por enquanto
    public void updateProfilePicture(View view){
        User user = dataCache.getUserById("placeholderID");
        user.setPicture("placeholderNewPicture");
        profilePictureView.setImageResource("placeholderNewPicture");
    }
}
