package com.grupo6.appdecomissao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.DataCache;
import com.grupo6.appdecomissao.domain.User;

public class ProfileSettingsActivity extends Activity {

    private TextInputEditText nameEditText, emailEditText;
    // private ImageView profilePictureView;
    private DataCache dataCache = DataCache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Trocar o placeholder quando tivermos os xmls das telas
        setContentView(R.layout.activity_configs_consultant);

        // Assumi que não será possível fazer edição no nome e email, só será
        // possível fazer alteração quando o usuário for para o ManageProfile
        TextInputLayout tiName = (TextInputLayout) findViewById(R.id.ti_name);
        nameEditText = (TextInputEditText) tiName.getEditText();

        TextInputLayout tiEmail = (TextInputLayout) findViewById(R.id.ti_email);
        emailEditText = (TextInputEditText) tiEmail.getEditText();
        // profilePictureView = (ImageView) findViewById(R.id.placeholderpfpimageview);

        // Lógica que obtém o nome e o email do usuário
        User user = dataCache.getUserById("placeholderID");
        String name = user.getName();
        String email = user.getEmail();
        // String picture = user.getPicture();

        // Seta o nome e o email do usuário na tela
        nameEditText.setText(name);
        emailEditText.setText(email);
        // profilePictureView.setImageResource(picture);
    }

    // Chamado pelo botão de gerenciar perfil
    public void manageProfile(View view){
        Intent it = new Intent(this, ManageProfileActivity.class);
        startActivity(it);
    }

    // Chamado pelo botão de gerenciar consultores
    public void manageConsultants(View view){
        // A Activity ManageConsultants ainda não existe, o nome é somente um placeholder
        //Intent it = new Intent(this, ManageConsultantsActivity.class);
        // startActivity(it);
    }
}
