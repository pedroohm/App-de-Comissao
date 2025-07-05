package com.grupo6.appdecomissao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.DataCache;
import com.grupo6.appdecomissao.domain.User;

public class ProfileSettingsActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, emailEditText;
    private DataCache dataCache = DataCache.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (dataCache.getCurrentId() == "85")
            setContentView(R.layout.activity_config_supervisor);
        else
            setContentView(R.layout.activity_configs_consultant);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile); // Define o item selecionado para esta tela

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent profileIntent;
            if (itemId == R.id.nav_dashboard) {
                if (dataCache.getCurrentId().equals("84"))
                    profileIntent = new Intent(this, ConsultantDashboardActivity.class);
                else
                    profileIntent = new Intent(this, DashboardSupervisor.class);

                profileIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(profileIntent);
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true; // Já estamos nesta tela, apenas consuma o evento
            } else {
                if (dataCache.getCurrentId().equals("84"))
                    profileIntent = new Intent(this, RegrasConsultorActivity.class);
                else
                    profileIntent = new Intent(this, RulesSupervisor.class);

                profileIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(profileIntent);
                return true;
            }
        });

        TextInputLayout tiName = (TextInputLayout) findViewById(R.id.ti_name);
        nameEditText = (TextInputEditText) tiName.getEditText();

        TextInputLayout tiEmail = (TextInputLayout) findViewById(R.id.ti_email);
        emailEditText = (TextInputEditText) tiEmail.getEditText();

        User user = dataCache.getUserById(dataCache.getCurrentId());
        String name = user.getName();
        String email = user.getEmail();

        nameEditText.setText(name);
        emailEditText.setText(email);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile); // Garante a seleção ao retomar
    }

    public void manageProfile(View view){
        Intent it = new Intent(this, ManageProfileActivity.class);
        startActivity(it);
    }

    public void manageConsultants(View view){
        Intent it = new Intent(this, ManageConsultantsActivity.class);
        startActivity(it);
    }
}