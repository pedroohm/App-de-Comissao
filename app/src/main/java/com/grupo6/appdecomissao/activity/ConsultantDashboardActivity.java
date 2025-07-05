package com.grupo6.appdecomissao.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import java.util.ArrayList;
import java.util.List;

public class ConsultantDashboardActivity extends AppCompatActivity {

    private ConsultantDashboardViewModel viewModel;

    // Componentes da UI
    private TextView saleView, comissionView, salesCountView, comissionPercView, goalsValueView;
    private TableLayout tableLayoutLog, tableLayoutGoals;
    private CircularProgressIndicator piGoals, piSalesGoals, piGains;
    private TextView tvGraphGoals, tvSalesGoals, tvGains;

    private AutoCompleteTextView periodSelector;
    private Button btReport;

    private String generatedCsvContent;

    private Button btnSeeAllSales;
    private static final int INITIAL_ITEMS_TO_SHOW = 3;

    private Button btnSeeAllGoals;

    // Declara o launcher que vai lidar com a ação de criar o arquivo
    private final ActivityResultLauncher<Intent> createFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Aqui recebemos o resultado da tela de seleção de arquivo
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null && this.generatedCsvContent != null) {
                        writeCsvToFile(uri, this.generatedCsvContent);
                        this.generatedCsvContent = null; // Limpa a variável após o uso
                    } else {
                        Toast.makeText(this, "Erro ao gerar o conteúdo do relatório.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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
        setupPeriodFilter();
        setupButtonClicks();

        // Inicializa o ViewModel
        viewModel = new ViewModelProvider(this).get(ConsultantDashboardViewModel.class);

        // Configura os observadores para reagir a mudanças nos dados
        setupObservers();

        // Pede ao ViewModel para iniciar o carregamento dos dados
        viewModel.loadConsultantData(CONSULTANT_ID, ORIGIN, TOKEN);
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
        goalsValueView = findViewById(R.id.tv_goals_value);
        periodSelector = findViewById(R.id.autoCompleteTextView_period);
        btReport = findViewById(R.id.bt_report);
        btnSeeAllSales = findViewById(R.id.btn_see_all_sales);
        btnSeeAllGoals = findViewById(R.id.btn_see_all_goals);
    }

    private void setupObservers() {
        viewModel.getSalesList().observe(this, sales -> {
            if (sales != null) {
                plotSalesInfo(sales);
            }
        });

        viewModel.getGoalsList().observe(this, goals -> {
            if (goals != null) {
                goalsValueView.setText(String.valueOf(goals.size()));

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

        // Mostra o botão apenas se a lista for maior que o limite
        if (btnSeeAllSales != null) {
            if (sales.size() > INITIAL_ITEMS_TO_SHOW) {
                btnSeeAllSales.setVisibility(View.VISIBLE);
            } else {
                btnSeeAllSales.setVisibility(View.GONE);
            }
        }

        double totalSales = 0;
        double totalCommission = 0;

        for (Sale sale : sales) {
            totalSales += sale.getPrice();
            totalCommission += sale.getCommission();
        }

        int itemsToShow = Math.min(sales.size(), INITIAL_ITEMS_TO_SHOW);
        for (int i = 0; i < itemsToShow; i++) {
            Sale sale = sales.get(i);

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
            tvProduto.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
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

        animateProgress(piSalesGoals, tvSalesGoals, sales.size() * 5);
        animateProgress(piGains, tvGains, (int) avgCommission);
    }

    private int plotGoalsInfo(List<Goal> goals) {
        while (tableLayoutGoals.getChildCount() > 1) {
            tableLayoutGoals.removeViewAt(1);
        }

        if (btnSeeAllGoals != null) {
            if (goals.size() > INITIAL_ITEMS_TO_SHOW - 1) {
                btnSeeAllGoals.setVisibility(View.VISIBLE);
            } else {
                btnSeeAllGoals.setVisibility(View.GONE);
            }
        }

        int achievedCount = 0;
        int itemsToShow = Math.min(goals.size(), INITIAL_ITEMS_TO_SHOW - 1);

        for (Goal goal : goals) {
            if (goal.getAchieved()) {
                achievedCount++;
            }
        }

        for (int i = 0; i < itemsToShow; i++) {
            Goal goal = goals.get(i);
            TableRow newRow = new TableRow(this);
            newRow.setBackgroundColor(Color.parseColor("#80E0E0E0"));
            TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

            // Célula 1: Descrição
            TextView tvDescricao = new TextView(this);
            tvDescricao.setText(goal.getDescription());
            tvDescricao.setTextColor(Color.BLACK);
            tvDescricao.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            tvDescricao.setTypeface(null, Typeface.BOLD);
            tvDescricao.setLayoutParams(cellParams);
            tvDescricao.setMaxLines(2);
            tvDescricao.setEllipsize(TextUtils.TruncateAt.END);

            // Célula 2: Bônus
            TextView tvBonus = new TextView(this);
            tvBonus.setText(String.format("%.2f%%", goal.getBonus()));
            tvBonus.setTextColor(Color.BLACK);
            tvBonus.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvBonus.setTypeface(null, Typeface.BOLD);
            tvBonus.setLayoutParams(cellParams);

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
        int targetProgress = (int) Math.min(100, finalProgress);
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
                profileIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(profileIntent);
                return true;
            } else if (item.getItemId() == R.id.nav_regras) {
                Intent regrasIntent = new Intent(this, RegrasConsultorActivity.class);
                regrasIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(regrasIntent);
                return false;
            } else if (item.getItemId() == R.id.nav_dashboard) {
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

    private void setupPeriodFilter() {
        // Opções que aparecerão no menu dropdown
        String[] periods = new String[]{"Todo o período", "Este mês", "Últimos 3 meses", "Últimos 6 meses"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                periods
        );

        periodSelector.setAdapter(adapter);

        periodSelector.setText(adapter.getItem(0), false);

        periodSelector.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPeriod = (String) parent.getItemAtPosition(position);
            Toast.makeText(this, "Filtrando por: " + selectedPeriod, Toast.LENGTH_SHORT).show();

            viewModel.applyFilter(selectedPeriod);
        });
    }

    private void setupButtonClicks() {
        if (btReport != null) {
            btReport.setOnClickListener(v -> {
                // Pega a lista de vendas ATUALMENTE exibida do ViewModel
                List<Sale> currentSales = viewModel.getSalesList().getValue();
                List<Goal> currentGoals = viewModel.getGoalsList().getValue();

                if (currentSales == null || currentSales.isEmpty()) {
                    Toast.makeText(this, "Não há dados para exportar.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gera o conteúdo do CSV
                this.generatedCsvContent = generateCsvContent(currentSales, currentGoals);

                // Inicia o processo de salvar o arquivo
                launchSaveFileIntent();
            });
        }

        if (btnSeeAllSales != null) {
            btnSeeAllSales.setOnClickListener(v -> {
                Intent intent = new Intent(this, SalesHistoryActivity.class);
                // Pega a lista completa do ViewModel
                ArrayList<Sale> fullList = new ArrayList<>(viewModel.getSalesList().getValue());
                intent.putParcelableArrayListExtra("SALES_LIST", fullList);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            });
        }

        if (btnSeeAllGoals != null) {
            btnSeeAllGoals.setOnClickListener(v -> {
                Intent intent = new Intent(this, GoalsListActivity.class);
                ArrayList<Goal> fullList = new ArrayList<>(viewModel.getGoalsList().getValue());
                intent.putParcelableArrayListExtra("GOALS_LIST", fullList);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            });
        }
    }

    private String generateCsvContent(List<Sale> sales, List<Goal> goals) {
        StringBuilder csvBuilder = new StringBuilder();

        // Resumo do Período
        double totalSalesValue = 0;
        double totalCommissionValue = 0;
        for (Sale sale : sales) {
            totalSalesValue += sale.getPrice();
            totalCommissionValue += sale.getCommission();
        }

        csvBuilder.append("Resumo do Período\n");
        csvBuilder.append("Total de Vendas (R$);").append(String.format("%.2f", totalSalesValue)).append("\n");
        csvBuilder.append("Total de Comissões (R$);").append(String.format("%.2f", totalCommissionValue)).append("\n");
        csvBuilder.append("Número de Vendas;").append(sales.size()).append("\n");
        csvBuilder.append("\n");

        // Adicionando as Metas
        csvBuilder.append("Metas do Consultor\n");
        csvBuilder.append("Descricao;Bonus (%);Status\n");
        if (goals.isEmpty()) {
            csvBuilder.append("Nenhuma meta encontrada.\n");
        } else {
            for (Goal goal : goals) {
                csvBuilder.append("\"").append(goal.getDescription()).append("\";");
                csvBuilder.append(String.format("%.2f", goal.getBonus())).append("%;");
                csvBuilder.append(goal.getAchieved() ? "Atingida" : "Pendente").append("\n");
            }
        }
        csvBuilder.append("\n");

        // Detalhamento das vendas
        csvBuilder.append("Detalhes das Vendas do Período\n");
        csvBuilder.append("ID Venda;Produto;Preco do Produto (R$);Comissao (R$);Data\n");
        if (sales.isEmpty()) {
            csvBuilder.append("Nenhuma venda encontrada no período.\n");
        } else {
            for (Sale sale : sales) {
                csvBuilder.append(sale.getId()).append(";");
                csvBuilder.append("\"").append(sale.getProduct()).append("\";");
                csvBuilder.append(String.format("%.2f", sale.getPrice())).append(";");
                csvBuilder.append(String.format("%.2f", sale.getCommission())).append(";");
                csvBuilder.append(sale.getSaleDate()).append("\n");
            }
        }

        return csvBuilder.toString();
    }

    private void launchSaveFileIntent() {
        // Cria um nome de arquivo dinâmico com a data atual
        String fileName = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fileName = "relatorio_comissao_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd")) + ".csv";
        }

        // Cria a intenção para o sistema operacional abrir a tela de "Salvar Como..."
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv"); // Define o tipo de arquivo como CSV
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        // Lança a tela de seleção de arquivo
        createFileLauncher.launch(intent);
    }

    private void writeCsvToFile(Uri uri, String content) {
        try {
            // Abre um "fluxo de escrita" para o local que o usuário escolheu
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                // Escreve o conteúdo e fecha o fluxo
                outputStream.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });
                outputStream.write(content.getBytes(StandardCharsets.UTF_8));

                outputStream.close();
                Toast.makeText(this, "Relatório salvo com sucesso!", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.e("ExportCSV", "Erro ao salvar arquivo CSV", e);
            Toast.makeText(this, "Erro ao salvar o relatório.", Toast.LENGTH_LONG).show();
        }
    }
}