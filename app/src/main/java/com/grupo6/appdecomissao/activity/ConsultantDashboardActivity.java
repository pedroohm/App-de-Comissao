package com.grupo6.appdecomissao.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.Goal;
import com.grupo6.appdecomissao.domain.Sale;
import com.grupo6.appdecomissao.viewmodel.ConsultantDashboardViewModel;

import java.util.List;

public class ConsultantDashboardActivity extends AppCompatActivity {

    private ConsultantDashboardViewModel viewModel;

    // Componentes da UI
    private TextView saleView, comissionView, salesCountView, comissionPercView;
    private TableLayout tableLayoutLog, tableLayoutGoals;
    private CircularProgressIndicator piGoals, piSalesGoals, piGains;
    private TextView tvGraphGoals, tvSalesGoals, tvGains;

    // Constantes (idealmente viriam de um login ou SharedPreferences)
    private static final String CONSULTANT_ID = "84";
    private static final String ORIGIN = "8";
    private static final String TOKEN = "b116d29f1252d2ce144d5cb15fb14c7f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_consultant);

        // Configuração inicial da UI
        setupWindowInsets();
        initializeUIComponents();
        setupBottomNavigation();

        // Inicializa o ViewModel
        viewModel = new ViewModelProvider(this).get(ConsultantDashboardViewModel.class);

        // Configura os observadores para reagir a mudanças nos dados
        setupObservers();

        // Pede ao ViewModel para iniciar o carregamento dos dados
        viewModel.loadConsultantData(CONSULTANT_ID, ORIGIN, TOKEN);

        Button btnCadastroRegra = findViewById(R.id.btnCadastroRegra);
        Button btnRegrasConsultor = findViewById(R.id.btnRegrasConsultor);
        Button btnRegrasSupervisor = findViewById(R.id.btnRegrasSupervisor);
        btnCadastroRegra.setOnClickListener(v -> startActivity(new Intent(this, CadastroRegraActivity.class)));
        btnRegrasConsultor.setOnClickListener(v -> startActivity(new Intent(this, RegrasConsultorActivity.class)));
        btnRegrasSupervisor.setOnClickListener(v -> startActivity(new Intent(this, RegrasSupervisorActivity.class)));
    }

    private void initializeUIComponents() {
        saleView = findViewById(R.id.tv_sales_value);
        comissionView = findViewById(R.id.tv_pcomission_value);
        salesCountView = findViewById(R.id.tv_consultant_sales_value);
        comissionPercView = findViewById(R.id.tv_consultant_comission_value);
        tableLayoutLog = findViewById(R.id.tl_log);
        tableLayoutGoals = findViewById(R.id.tl_goals);
        piGoals = findViewById(R.id.pi_goals);
        piSalesGoals = findViewById(R.id.pi_sales_goals);
        piGains = findViewById(R.id.pi_gains);
        tvGraphGoals = findViewById(R.id.tv_graph_goals);
        tvSalesGoals = findViewById(R.id.tv_sales_goals);
        tvGains = findViewById(R.id.tv_gains);
    }

    private void setupObservers() {
        viewModel.getSalesList().observe(this, sales -> {
            if (sales != null) {
                plotSalesInfo(sales);
            }
        });

        viewModel.getGoalsList().observe(this, goals -> {
            if (goals != null) {
                int achievedCount = plotGoalsInfo(goals);
                int totalGoals = goals.size() > 0 ? goals.size() : 1;
                int progress = (100 * achievedCount) / totalGoals;
                animateProgress(piGoals, tvGraphGoals, progress);
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Erro: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void plotSalesInfo(List<Sale> sales) {
        while (tableLayoutLog.getChildCount() > 1) {
            tableLayoutLog.removeViewAt(1);
        }

        double totalSales = 0;
        double totalCommission = 0;

        for (Sale sale : sales) {
            totalSales += sale.getPrice();
            totalCommission += sale.getCommission();

            TableRow newRow = new TableRow(this);
            newRow.setBackgroundColor(Color.parseColor("#80E0E0E0"));
            TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

            // Célula 1: Número (ID da Venda)
            TextView tvNumero = new TextView(this);
            tvNumero.setText(sale.getId());
            tvNumero.setTextColor(Color.BLACK);
            tvNumero.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvNumero.setTypeface(null, Typeface.BOLD);
            tvNumero.setLayoutParams(cellParams);

            // Célula 2: Produto
            TextView tvProduto = new TextView(this);
            tvProduto.setText(sale.getProduct());
            tvProduto.setTextColor(Color.BLACK);
            tvProduto.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvProduto.setTypeface(null, Typeface.BOLD);
            tvProduto.setLayoutParams(cellParams);
            tvProduto.setMaxLines(1);
            tvProduto.setEllipsize(TextUtils.TruncateAt.END);

            // Célula 3: Preço do Produto
            TextView tvPreco = new TextView(this);
            tvPreco.setText("R$ " + String.format("%.2f", sale.getPrice()));
            tvPreco.setTextColor(Color.BLACK);
            tvPreco.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvPreco.setTypeface(null, Typeface.BOLD);
            tvPreco.setLayoutParams(cellParams);

            // Célula 4: Comissão
            TextView tvComissao = new TextView(this);
            tvComissao.setText("R$ " + String.format("%.2f", sale.getCommission()));
            tvComissao.setTextColor(Color.BLACK);
            tvComissao.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvComissao.setTypeface(null, Typeface.BOLD);
            tvComissao.setLayoutParams(cellParams);

            newRow.addView(tvNumero);
            newRow.addView(tvProduto);
            newRow.addView(tvPreco);
            newRow.addView(tvComissao);
            tableLayoutLog.addView(newRow);
        }

        double avgCommission = (totalSales > 0) ? (totalCommission / totalSales) * 100 : 0;

        saleView.setText("R$ " + String.format("%.2f", totalSales));
        comissionView.setText("R$ " + String.format("%.2f", totalCommission));
        salesCountView.setText(String.valueOf(sales.size()));
        comissionPercView.setText(String.format("%.2f", avgCommission));

        animateProgress(piSalesGoals, tvSalesGoals, sales.size() * 5); // Ex: 5% por venda para a meta
        animateProgress(piGains, tvGains, (int) avgCommission);
    }

    private int plotGoalsInfo(List<Goal> goals) {
        while (tableLayoutGoals.getChildCount() > 1) {
            tableLayoutGoals.removeViewAt(1);
        }

        int achievedCount = 0;
        for (Goal goal : goals) {
            if (goal.getAchieved()) {
                achievedCount++;
            }
            TableRow newRow = new TableRow(this);
            newRow.setBackgroundColor(Color.parseColor("#80E0E0E0"));
            TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

            // Célula 1: Descrição da Meta
            TextView tvDescricao = new TextView(this);
            tvDescricao.setText(goal.getDescription());
            tvDescricao.setTextColor(Color.parseColor("#4A4A4A"));
            tvDescricao.setTypeface(null, Typeface.BOLD);
            tvDescricao.setLayoutParams(cellParams);
            tvDescricao.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            // Célula 2: Bônus
            TextView tvBonus = new TextView(this);
            tvBonus.setText(String.format("%.2f", goal.getBonus()) + "%");
            tvBonus.setTextColor(Color.parseColor("#4A4A4A"));
            tvBonus.setTypeface(null, Typeface.BOLD);
            tvBonus.setLayoutParams(cellParams);
            tvBonus.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            // Célula 3: Atingida?
            TextView tvAtingida = new TextView(this);
            tvAtingida.setText(goal.getAchieved() ? "Sim" : "Não");
            tvAtingida.setTextColor(goal.getAchieved() ? Color.parseColor("#009688") : Color.RED);
            tvAtingida.setTypeface(null, Typeface.BOLD);
            tvAtingida.setLayoutParams(cellParams);
            tvAtingida.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            newRow.addView(tvDescricao);
            newRow.addView(tvBonus);
            newRow.addView(tvAtingida);
            tableLayoutGoals.addView(newRow);
        }
        return achievedCount;
    }

    private void animateProgress(CircularProgressIndicator progressIndicator, TextView textView, double finalProgress) {
        int targetProgress = (int) Math.min(100, finalProgress); // Garante que não passe de 100
        ValueAnimator animator = ValueAnimator.ofInt(0, targetProgress);
        animator.setDuration(800);
        animator.addUpdateListener(animation -> {
            int currentProgress = (int) animation.getAnimatedValue();
            progressIndicator.setProgress(currentProgress);
            textView.setText(currentProgress + "%");
        });
        animator.start();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                Intent profileIntent = new Intent(this, ProfileSettingsActivity.class);
                startActivity(profileIntent);
                return true;
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