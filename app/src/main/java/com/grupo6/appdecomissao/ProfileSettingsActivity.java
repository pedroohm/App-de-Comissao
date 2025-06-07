package com.grupo6.appdecomissao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class ProfileSettingsActivity extends Activity {

    private EditText nameTextView, emailTextView;
    private ImageView profilePictureView;
    private DataCache dataCache = DataCache.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Trocar o placeholder quando tivermos os xmls das telas
        setContentView(R.layout.placeholderxml);

        // Assumi que o nome e o email que aparecerão na tela vão estar
        // numa EditText onde não será possível fazer edição, só será
        // possível fazer alteração quando o usuário for para o ManageProfile
        nameTextView = (EditText) findViewById(R.id.placeholdernametxt);
        emailTextView = (EditText) findViewById(R.id.placeholderemailtxt);
        profilePictureView = (ImageView) findViewById(R.id.placeholderpfpimageview);

        // Lógica que obtém o nome e o email do usuário
        User user = dataCache.getUserById("placeholderID");
        String name = user.getName();
        String email = user.getEmail();
        String picture = user.getPicture();

        // Seta o nome e o email do usuário na tela
        nameTextView.setText(name);
        emailTextView.setText(email);
        profilePictureView.setImageResource(picture);
    }

    // Chamado pelo botão de gerenciar perfil
    public void manageProfile(View view){
        Intent it = new Intent(this, ManageProfileActivity.class);
        startActivity(it);
    }

    // Chamado pelo botão de gerenciar consultores
    public void manageConsultants(View view){
        // A Activity ManageConsultants ainda não existe, o nome é somente um placeholder
        Intent it = new Intent(this, ManageConsultantsActivity.class);
        startActivity(it);
    }
}
