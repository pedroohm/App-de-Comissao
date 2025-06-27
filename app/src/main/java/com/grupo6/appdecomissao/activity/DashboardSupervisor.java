package com.grupo6.appdecomissao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.DataCache;
import com.grupo6.appdecomissao.viewmodel.SupervisorDashboardViewModel;

public class DashboardSupervisor extends AppCompatActivity {

    private SupervisorDashboardViewModel viewModel;
    private TextView tvConsultantsValue, tvGoalsValue, tvInvoicingValue;
    private AutoCompleteTextView periodSelector;

    // IDs do supervisor logado e da API (idealmente viriam do login)
    private static final String SUPERVISOR_ID = "85";
    private static final String ORIGIN = "8";
    private static final String TOKEN = "b116d29f1252d2ce144d5cb15fb14c7f";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_supervisor);

        // 1. Configurações iniciais da UI
        setupWindowInsets();
        initializeUIComponents();
        setupBottomNavigation();

        // 2. Inicializa o ViewModel
        viewModel = new ViewModelProvider(this).get(SupervisorDashboardViewModel.class);

        // 3. Configura os observadores para reagir a mudanças nos dados
        setupObservers();

        // 4. Configura o filtro de período
        setupPeriodFilter();

        // 5. Pede ao ViewModel para iniciar o carregamento dos dados
        viewModel.loadSupervisorData(SUPERVISOR_ID, ORIGIN, TOKEN);
    }

    private void initializeUIComponents() {
        tvConsultantsValue = findViewById(R.id.tv_consultants_value);
        tvGoalsValue = findViewById(R.id.tv_goals_value);
        tvInvoicingValue = findViewById(R.id.tv_invoicing_value);
        periodSelector = findViewById(R.id.ti_date_select).findViewById(android.R.id.text1); // Ajuste para pegar o AutoCompleteTextView
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            // Aqui você pode adicionar um ProgressBar e controlar sua visibilidade
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Erro: " + error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getTeamConsultants().observe(this, consultants -> {
            if (consultants != null) {
                tvConsultantsValue.setText(String.valueOf(consultants.size()));
                // Futuramente, vamos popular o seletor de consultores aqui
            }
        });

        viewModel.getTeamSales().observe(this, sales -> {
            if (sales != null) {
                double totalInvoicing = sales.stream().mapToDouble(s -> s.getPrice()).sum();
                tvInvoicingValue.setText("R$ " + String.format("%.2f", totalInvoicing));
                // Futuramente, vamos atualizar os outros campos aqui
            }
        });

        viewModel.getTeamGoals().observe(this, goals -> {
            if (goals != null) {
                tvGoalsValue.setText(String.valueOf(goals.size()));
                // Futuramente, vamos atualizar os outros campos aqui
            }
        });
    }

    private void setupPeriodFilter() {
        // Lógica idêntica à da tela do consultor
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.ti_date_select).findViewById(R.id.autoCompleteTextView_period);
        String[] periods = new String[]{"Todo o período", "Este mês", "Últimos 3 meses", "Últimos 6 meses"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, periods);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setText(adapter.getItem(0), false); // Define o valor inicial

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPeriod = (String) parent.getItemAtPosition(position);
            viewModel.applyFilter(selectedPeriod);
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard); // Define o item selecionado
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_profile) {
                // Navega para a tela de perfil
                Intent profileIntent = new Intent(this, ProfileSettingsActivity.class);
                profileIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                // Define o ID atual no DataCache antes de ir para a próxima tela
                DataCache.getInstance().setCurrentId(SUPERVISOR_ID);
                startActivity(profileIntent);
                return true;
            } else if (itemId == R.id.nav_dashboard) {
                return true; // Já estamos aqui
            }
            return false;
        });
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}