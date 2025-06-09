package com.grupo6.appdecomissao.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Set;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.CommissionRule;
import com.grupo6.appdecomissao.domain.DataCache;
import com.grupo6.appdecomissao.domain.Goal;
import com.grupo6.appdecomissao.domain.Record;
import com.grupo6.appdecomissao.domain.Sale;
import com.grupo6.appdecomissao.domain.User;
import com.grupo6.appdecomissao.remote.ApiCallback;
import com.grupo6.appdecomissao.remote.ApiRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsultantDashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";

    private static final String currentId = "84";

    private static final String origin = "8";
    private static final String token = "b116d29f1252d2ce144d5cb15fb14c7f";

    private String finalStage;
    private String processId;

    private final List<Record> recordsFilters = new ArrayList<>();
    private AtomicInteger pendingLoads;

    private DataCache dataCache = DataCache.getInstance();
    private ApiRepository apiRepository;
    private TextView textResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_consultant);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                Intent profileIntent = new Intent(this, ProfileSettingsActivity.class);
                profileIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(profileIntent);
                return true;
            } else {
                return false;
            }
        });

        Log.d(TAG, "Activity criada");

        //textResults = findViewById(R.id.textResults);
        apiRepository = new ApiRepository();

        recordSales(currentId, new ApiCallback<List<Record>>() {
            @Override
            public void onSuccess(List<Record> records) {
                // Vamos criar uma venda para cada registro filtrado
                int id = 1;
                for (Record record : records) {
                    String date = record.getLastDate();
                    date = date.substring(0, 11);

                    User user = dataCache.getUserById(record.getResponsibleId());
                    Set<String> comissionRules = dataCache.getUserCommissionRules(record.getResponsibleId());
                    double comissionPercentage = 0.0;

                    for (String comissionRule : comissionRules) {
                        Log.d(TAG, "comissionRules: " + comissionRules);
                        Log.d(TAG, "comissionRule: " + comissionRule);
                        if (dataCache.getCommissionRuleById(comissionRule).getProcessId().equals(record.getProcessId())) {
                            comissionPercentage = dataCache.getCommissionRuleById(comissionRule).getCommissionPercentage();
                        }
                    }

                    Log.d(TAG, "Comissão do consultor: " + comissionPercentage + "%");

                    double comission = Double.parseDouble(record.getOfferValue()) * comissionPercentage / 100;

                    Sale sale = new Sale(
                           Integer.toString(id),
                           record.getResponsibleId(),
                           record.getOfferName(),
                           Double.parseDouble(record.getOfferValue()),
                           date, comission, record.getId()
                    );

                    dataCache.putSale(sale);
                    id++;
                }

                plotSalesInfo();

            }
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Erro ao carregar registros: " + errorMessage);
            }
        });
    }

    private void recordSales(String currentId, ApiCallback<List<Record>> callback) {
        // Pegando o id das regras de comissão do consultor
        Set<String> rules = dataCache.getUserCommissionRules(currentId);
        Log.d(TAG, "Regras de comissão do consultor: " + rules);

        // Será usado para chamar o showResults
        pendingLoads = new AtomicInteger(rules.size());

        // Para cada regra de comissão do consultor, vamos mapear as suas vendas no respectivo processo da regra
        for (String ruleId : rules) {
            Log.d(TAG, "Regra de comissão: " + ruleId);
            CommissionRule rule = dataCache.getCommissionRuleById(ruleId);

            processId = rule.getProcessId();
            finalStage = rule.getStage();

            Log.d(TAG, "ProcessId da regra de comissao: " + processId);
            Log.d(TAG, "finalStage final da regra de comissao: " + finalStage);

            loadRecords(processId, finalStage, callback);
        }
    }

    private void loadRecords(final String processId, final String finalStage, ApiCallback<List<Record>> callback) {
        Log.d(TAG, "Iniciando carregamento de registros");

        apiRepository.getRecordsByProcessAndStage(origin, token, processId, finalStage, currentId, new ApiCallback<List<Record>>() {
            @Override
            public void onSuccess(List<Record> records) {
                Log.d(TAG, "Sucesso ao carregar registros. Total: " + records.size());

                try {
                    Log.d(TAG, "Records : " + records);
                    for (Record record : records) {
                        synchronized (recordsFilters) {
                            recordsFilters.addAll(records);
                        }

                        if (pendingLoads.decrementAndGet() == 0) {
                            callback.onSuccess(new ArrayList<>(recordsFilters));
                        }
                    }


                    if (recordsFilters.isEmpty()) {
                        callback.onError("Nenhum registro foi encontrado");
                    }

                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao formatar dados", e);
                    }

            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Erro ao carregar registros: " + errorMessage);
            }
        });
    }

    private void plotSalesInfo() {
        TextView saleView = findViewById(R.id.tv_sales_value);
        TextView comissionView = findViewById(R.id.tv_pcomission_value);
        TextView goalsView = findViewById(R.id.tv_pcomission_value);
        TextView salesCountView = findViewById(R.id.tv_consultant_sales_value);
        TextView comissionPercView = findViewById(R.id.tv_consultant_comission_value);

        List<Sale> sales = new ArrayList<>();
        int qtdSales = dataCache.getSales().size();

        for (int i = 1; i <= qtdSales; i++) {
            sales.add(dataCache.getSaleById(Integer.toString(i)));
        }

        double totalSales = 0;
        double totalCommission = 0;
        double avgCommission;

        TableLayout tableLayout = findViewById(R.id.tl_log); // Coloque isso fora do for

        qtdSales = 0;
        for (Sale sale : sales) {
            qtdSales++;
            String id = sale.getId();
            String product = sale.getProduct();
            double price = sale.getPrice();
            double comission = sale.getCommission();

            totalSales += price;
            totalCommission += comission;

            TableRow newRow = new TableRow(this);
            newRow.setBackgroundColor(Color.parseColor("#80E0E0E0"));

            TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

            TextView tvNumero = new TextView(this);
            tvNumero.setText(id);
            tvNumero.setTextColor(Color.BLACK);
            tvNumero.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvNumero.setTypeface(null, Typeface.BOLD);
            tvNumero.setLayoutParams(cellParams);

            TextView tvProduto = new TextView(this);
            tvProduto.setText(product);
            tvProduto.setTextColor(Color.BLACK);
            tvProduto.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvProduto.setTypeface(null, Typeface.BOLD);
            tvProduto.setLayoutParams(cellParams);

            TextView tvPreco = new TextView(this);
            tvPreco.setText("R$ " + String.format("%.2f", price));
            tvPreco.setTextColor(Color.BLACK);
            tvPreco.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvPreco.setTypeface(null, Typeface.BOLD);
            tvPreco.setLayoutParams(cellParams);

            TextView tvComissao = new TextView(this);
            tvComissao.setText("R$ " + String.format("%.2f", comission));
            tvComissao.setTextColor(Color.BLACK);
            tvComissao.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvComissao.setTypeface(null, Typeface.BOLD);
            tvComissao.setLayoutParams(cellParams);
            tvProduto.setMaxLines(1); // Apenas uma linha
            tvProduto.setEllipsize(TextUtils.TruncateAt.END); // Usa "..." no fim se for muito longo
            //tvProduto.setSingleLine(true); // Alternativa para .setMaxLines(1)

            newRow.addView(tvNumero);
            newRow.addView(tvProduto);
            newRow.addView(tvPreco);
            newRow.addView(tvComissao);

            tableLayout.addView(newRow);
        }

        avgCommission = (totalCommission / totalSales) * 100;

        saleView.setText("R$ " + String.format("%.2f", totalSales));
        comissionView.setText("R$ " + String.format("%.2f", totalCommission));
        salesCountView.setText(String.valueOf(qtdSales));
        //goalsView.setText(String.format("%.2f", avgCommission) + "%");
        comissionPercView.setText(String.format("%.2f", avgCommission));

        int achievied = plotGoalsInfo(currentId);

        animateProgress(R.id.pi_goals, R.id.tv_graph_goals, achievied/2);
        animateProgress(R.id.pi_sales_goals, R.id.tv_sales_goals, qtdSales*5);
        animateProgress(R.id.pi_gains, R.id.tv_gains, avgCommission);
    }

    private void animateProgress(int circularProgressId, int textViewId, double progressoFinal) {
        CircularProgressIndicator progressIndicator = findViewById(circularProgressId);
        TextView textView = findViewById(textViewId);

        int progressoInicial = progressIndicator.getProgress();

        ValueAnimator animator = ValueAnimator.ofInt(progressoInicial, (int) progressoFinal);
        animator.setDuration(800); // duração da animação (ms)
        animator.addUpdateListener(animation -> {
            int progressoAtual = (int) animation.getAnimatedValue();
            progressIndicator.setProgress(progressoAtual);
            textView.setText(progressoAtual + "%");
        });
        animator.start();
    }


    private int plotGoalsInfo(String userId) {
        TableLayout tableLayout = findViewById(R.id.tl_goals);
        DataCache dataCache = DataCache.getInstance();

        int achievied = 0;

        while (tableLayout.getChildCount() > 1) {
            tableLayout.removeViewAt(1);
        }

        List<Goal> userGoals = dataCache.getGoalsByUserId(userId);

        for (Goal goal : userGoals) {
            TableRow newRow = new TableRow(this);
            newRow.setBackgroundColor(Color.parseColor("#80E0E0E0")); // opcional

            TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

            // Descrição da meta
            TextView tvDescricao = new TextView(this);
            tvDescricao.setText(goal.getDescription());
            tvDescricao.setTextColor(Color.parseColor("#4A4A4A"));
            tvDescricao.setTypeface(null, Typeface.BOLD);
            tvDescricao.setLayoutParams(cellParams);
            tvDescricao.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            // Bônus
            TextView tvBonus = new TextView(this);
            tvBonus.setText("R$ " + String.format("%.2f", goal.getBonus()));
            tvBonus.setTextColor(Color.parseColor("#4A4A4A"));
            tvBonus.setTypeface(null, Typeface.BOLD);
            tvBonus.setLayoutParams(cellParams);
            tvBonus.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            // Atingida
            TextView tvAtingida = new TextView(this);
            if (goal.getAchieved()) achievied++;
            tvAtingida.setText(goal.getAchieved() ? "Sim" : "Não");
            tvAtingida.setTextColor(Color.parseColor("#4A4A4A"));
            tvAtingida.setTypeface(null, Typeface.BOLD);
            tvAtingida.setLayoutParams(cellParams);
            tvAtingida.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            // Adiciona os TextViews à linha
            newRow.addView(tvDescricao);
            newRow.addView(tvBonus);
            newRow.addView(tvAtingida);

            // Adiciona a nova linha na tabela
            tableLayout.addView(newRow);
        }

        return achievied;
    }

}