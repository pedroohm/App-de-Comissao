package com.grupo6.appdecomissao.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.DataCache;

public class ConfigsConsultant extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configs_consultant);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                Intent dashboardIntent = new Intent(this, ConsultantDashboardActivity.class);
                dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(dashboardIntent);
                return true;
            } else if (itemId == R.id.nav_regras) {
                Intent regrasIntent = new Intent(this, RegrasConsultorActivity.class);
                regrasIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(regrasIntent);
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent profileIntent = new Intent(this, ProfileSettingsActivity.class);
                profileIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(profileIntent);
                return true;
            }
            return false;
        });

        // Marcar o item de perfil como selecionado
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }
}