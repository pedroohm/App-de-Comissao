package com.grupo6.appdecomissao.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import java.io.IOException;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.DataCache;
import com.grupo6.appdecomissao.domain.Goal;
import com.grupo6.appdecomissao.domain.Sale;
import com.grupo6.appdecomissao.domain.User;
import com.grupo6.appdecomissao.viewmodel.SupervisorDashboardViewModel;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardSupervisor extends AppCompatActivity {

    private SupervisorDashboardViewModel viewModel;
    private TextView tvConsultantsValue, tvGoalsValue, tvInvoicingValue;
    private AutoCompleteTextView periodSelector;
    private AutoCompleteTextView consultantSelector;

    private TextView tvConsultantSalesValue, tvConsultantComissionValue;
    private CircularProgressIndicator piGains, piInvoicingGoals, piGoals;
    private TextView tvGains, tvInvoicingGoals, tvGraphGoals;

    private ProgressBar pbConsultantGains, pbConsultantGoals, pbConsultantSalesGoals;
    private TextView tvConsultantGainsPerc, tvConsultantGoalsPerc, tvConsultantSalesGoalsPerc;

    private static final double META_FATURAMENTO_EQUIPE = 50000.0;

    private static final double META_VENDAS_CONSULTOR = 15000.0;

    private Button btGenerateReport;
    private String generatedCsvContent;

    private final ActivityResultLauncher<Intent> createFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null && this.generatedCsvContent != null) {
                        writeCsvToFile(uri, this.generatedCsvContent);
                        this.generatedCsvContent = null; // Limpa após o uso
                    } else {
                        Toast.makeText(this, "Erro ao gerar o conteúdo do relatório.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private LinearLayout consultantDetailsContainer;

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
        setupButtonClicks();

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
        periodSelector = findViewById(R.id.ti_date_select).findViewById(android.R.id.text1);
        consultantSelector = findViewById(R.id.ti_consultant_select).findViewById(R.id.autoCompleteTextView_consultant);
        tvConsultantSalesValue = findViewById(R.id.tv_consultant_sales_value);
        tvConsultantComissionValue = findViewById(R.id.tv_consultant_comission_value);
        piGains = findViewById(R.id.pi_gains);
        tvGains = findViewById(R.id.tv_gains);
        piInvoicingGoals = findViewById(R.id.pi_invoicing_goals);
        tvInvoicingGoals = findViewById(R.id.tv_invoicing_goals);
        piGoals = findViewById(R.id.pi_goals);
        tvGraphGoals = findViewById(R.id.tv_graph_goals);
        pbConsultantGains = findViewById(R.id.pb_consultant_gains);
        tvConsultantGainsPerc = findViewById(R.id.tv_consultant_gains_perc);
        pbConsultantGoals = findViewById(R.id.pb_consultant_goals);
        tvConsultantGoalsPerc = findViewById(R.id.tv_consultant_goals_perc);
        pbConsultantSalesGoals = findViewById(R.id.pb_consultant_sales_goals);
        tvConsultantSalesGoalsPerc = findViewById(R.id.tv_consultant_sales_goals_perc);
        consultantDetailsContainer = findViewById(R.id.ll_consultant_details_container);
        btGenerateReport = findViewById(R.id.bt_generate_report);
        periodSelector = findViewById(R.id.ti_date_select).findViewById(R.id.autoCompleteTextView_period);
    }

    private void setupButtonClicks() {
        btGenerateReport.setOnClickListener(v -> {
            // Gera o conteúdo do CSV com base no estado atual do ViewModel
            this.generatedCsvContent = generateCsvContent();

            if (this.generatedCsvContent == null || this.generatedCsvContent.isEmpty()) {
                Toast.makeText(this, "Não há dados para exportar.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Inicia o processo de salvar o arquivo
            launchSaveFileIntent();
        });
    }

    private void launchSaveFileIntent() {
        String fileName = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fileName = "relatorio_supervisor_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd")) + ".csv";
        }
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        createFileLauncher.launch(intent);
    }

    private void writeCsvToFile(Uri uri, String content) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF }); // BOM para UTF-8
                outputStream.write(content.getBytes(StandardCharsets.UTF_8));
                outputStream.close();
                Toast.makeText(this, "Relatório salvo com sucesso!", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.e("ExportSupervisorCSV", "Erro ao salvar arquivo CSV", e);
            Toast.makeText(this, "Erro ao salvar o relatório.", Toast.LENGTH_LONG).show();
        }
    }

    private String generateCsvContent() {
        StringBuilder csvBuilder = new StringBuilder();
        String period = periodSelector.getText().toString();

        // Cabeçalho do Relatório
        csvBuilder.append("Relatório de Desempenho da Equipe\n");
        csvBuilder.append("Período do Filtro;").append(period).append("\n\n");

        // --- Lógica para gerar o relatório da EQUIPE TODA ---
        List<User> consultants = viewModel.getTeamConsultants().getValue();
        List<Sale> allSales = viewModel.getTeamSales().getValue();
        List<Goal> allGoals = viewModel.getTeamGoals().getValue();

        // Verifica se os dados foram carregados antes de continuar
        if (consultants == null || allSales == null || allGoals == null) {
            Toast.makeText(this, "Aguarde o carregamento dos dados antes de gerar o relatório.", Toast.LENGTH_SHORT).show();
            return "";
        }

        // Resumo da Equipe
        double totalInvoicing = allSales.stream().mapToDouble(Sale::getPrice).sum();
        double totalCommission = allSales.stream().mapToDouble(Sale::getCommission).sum();
        csvBuilder.append("Resumo Geral da Equipe\n");
        csvBuilder.append("Número de Consultores;").append(consultants.size()).append("\n");
        csvBuilder.append("Faturamento Total (R$);").append(String.format("%.2f", totalInvoicing)).append("\n");
        csvBuilder.append("Comissão Total (R$);").append(String.format("%.2f", totalCommission)).append("\n\n");

        // Detalhes de cada consultor
        for (User consultant : consultants) {
            csvBuilder.append("--- Detalhes do Consultor: ").append(consultant.getName()).append(" ---\n");
            List<Sale> consultantSales = allSales.stream().filter(s -> s.getConsultantId().equals(consultant.getId())).collect(Collectors.toList());
            List<Goal> consultantGoals = allGoals.stream().filter(g -> g.getAssignedConsultantIds().contains(consultant.getId())).collect(Collectors.toList());

            // Histórico de Comissões
            csvBuilder.append("Histórico de Comissões\n");
            csvBuilder.append("ID Venda;Produto;Preço (R$);Comissão (R$);Data\n");
            if (consultantSales.isEmpty()){
                csvBuilder.append("Nenhuma venda encontrada neste período para este consultor.\n");
            } else {
                for (Sale sale : consultantSales) {
                    csvBuilder.append(sale.getId()).append(";");
                    csvBuilder.append("\"").append(sale.getProduct()).append("\";");
                    csvBuilder.append(String.format("%.2f", sale.getPrice())).append(";");
                    csvBuilder.append(String.format("%.2f", sale.getCommission())).append(";");
                    csvBuilder.append(sale.getSaleDate()).append("\n");
                }
            }
            csvBuilder.append("\n");

            // Metas
            csvBuilder.append("Metas\n");
            csvBuilder.append("Descrição;Bônus (%);Status\n");
            if (consultantGoals.isEmpty()){
                csvBuilder.append("Nenhuma meta encontrada para este consultor.\n");
            } else {
                for (Goal goal : consultantGoals) {
                    csvBuilder.append("\"").append(goal.getDescription()).append("\";");
                    csvBuilder.append(String.format("%.2f", goal.getBonus())).append("%;");
                    csvBuilder.append(goal.getAchieved() ? "Atingida" : "Pendente").append("\n");
                }
            }
            csvBuilder.append("\n\n");
        }

        return csvBuilder.toString();
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
                // AQUI: Populamos o seletor de consultores
                setupConsultantSelector(consultants);
            }
        });

        viewModel.getTeamSales().observe(this, sales -> {
            if (sales != null) {
                double totalInvoicing = sales.stream().mapToDouble(s -> s.getPrice()).sum();
                tvInvoicingValue.setText("R$ " + String.format("%.2f", totalInvoicing));

                double progressInvoicing = (totalInvoicing / META_FATURAMENTO_EQUIPE) * 100;
                animateProgress(piInvoicingGoals, tvInvoicingGoals, progressInvoicing);

                double totalCommission = sales.stream().mapToDouble(Sale::getCommission).sum();
                double avgCommission = (totalInvoicing > 0) ? (totalCommission / totalInvoicing) * 100 : 0;
                animateProgress(piGains, tvGains, avgCommission);
            }
        });

        viewModel.getTeamGoals().observe(this, goals -> {
            if (goals != null) {
                tvGoalsValue.setText(String.valueOf(goals.size()));

                long achievedCount = goals.stream().filter(Goal::getAchieved).count();
                int totalGoals = goals.isEmpty() ? 1 : goals.size();
                double progressGoals = ((double) achievedCount / totalGoals) * 100;
                animateProgress(piGoals, tvGraphGoals, progressGoals);
            }
        });

        viewModel.getSelectedConsultantSales().observe(this, sales -> {
            if (sales != null) {
                double totalSales = sales.stream().mapToDouble(Sale::getPrice).sum();
                double totalCommission = sales.stream().mapToDouble(Sale::getCommission).sum();

                tvConsultantSalesValue.setText("R$ " + String.format("%.2f", totalSales));
                tvConsultantComissionValue.setText("R$ " + String.format("%.2f", totalCommission));

                double avgCommission = (totalSales > 0) ? (totalCommission / totalSales) * 100 : 0;
                animateHorizontalProgress(pbConsultantGains, tvConsultantGainsPerc, avgCommission);

                double salesGoalProgress = (totalSales / META_VENDAS_CONSULTOR) * 100;
                animateHorizontalProgress(pbConsultantSalesGoals, tvConsultantSalesGoalsPerc, salesGoalProgress);
            }
        });

        viewModel.getSelectedConsultantGoals().observe(this, goals -> {
            if (goals != null) {
                long achievedCount = goals.stream().filter(Goal::getAchieved).count();
                int totalGoals = goals.isEmpty() ? 1 : goals.size(); // Evita divisão por zero

                double progressGoals = ((double) achievedCount / totalGoals) * 100;
                animateHorizontalProgress(pbConsultantGoals, tvConsultantGoalsPerc, progressGoals);
            }
        });
    }

    private void setupConsultantSelector(List<User> consultants) {
        // Extrai apenas os nomes dos consultores para uma lista de Strings
        List<String> consultantNames = consultants.stream().map(User::getName).collect(Collectors.toList());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, consultantNames);
        consultantSelector.setAdapter(adapter);

        consultantSelector.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);

            // 2. Procura na lista original de consultores qual deles tem esse nome.
            User selectedConsultant = consultants.stream()
                    .filter(consultant -> consultant.getName().equals(selectedName))
                    .findFirst()
                    .orElse(null); // Retorna null se, por algum motivo, não encontrar

            // 3. Continua a lógica apenas se um consultor válido foi encontrado.
            if (selectedConsultant != null) {
                consultantDetailsContainer.setVisibility(View.VISIBLE);

                // Avisa o ViewModel qual consultor foi selecionado
                viewModel.selectConsultant(selectedConsultant);
            }
        });
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

    private void animateHorizontalProgress(ProgressBar progressBar, TextView textView, double finalProgress) {
        int targetProgress = (int) Math.min(100, finalProgress);
        ObjectAnimator animator = ObjectAnimator.ofInt(progressBar, "progress", 0, targetProgress);
        animator.setDuration(800);
        animator.start();

        // Animação para o texto
        ValueAnimator textAnimator = ValueAnimator.ofInt(0, targetProgress);
        textAnimator.setDuration(800);
        textAnimator.addUpdateListener(animation -> {
            textView.setText(animation.getAnimatedValue().toString() + "%");
        });
        textAnimator.start();
    }

    private void setupPeriodFilter() {
        String[] periods = new String[]{"Todo o período", "Este mês", "Últimos 3 meses", "Últimos 6 meses"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, periods);

        // Usa diretamente a variável de classe 'periodSelector'
        periodSelector.setAdapter(adapter);
        periodSelector.setText(adapter.getItem(0), false);

        periodSelector.setOnItemClickListener((parent, view, position, id) -> {
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
            } else if (itemId == R.id.nav_regras) {
                Intent regrasIntent = new Intent(this, RegrasSupervisorActivity.class);
                regrasIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(regrasIntent);
                return true;
            }else {
                return false;
            }
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