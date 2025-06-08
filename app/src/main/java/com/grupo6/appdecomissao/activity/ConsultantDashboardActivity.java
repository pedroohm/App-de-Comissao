package com.grupo6.appdecomissao.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Set;

import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.CommissionRule;
import com.grupo6.appdecomissao.domain.DataCache;
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

                //showResults();

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

    private void showResults() {
        List<Sale> sales = new ArrayList<>();
        int n = dataCache.getSales().size();

        for (int i = 1; i <= n; i++) {
            sales.add(dataCache.getSaleById(Integer.toString(i)));
        }


        StringBuilder sb = new StringBuilder();
        sb.append("=== VENDAS DE ").append(currentId.toUpperCase()).append(" ===\n\n");

        for (Sale sale : sales) {
            // Log no Logcat de cada registro filtrado
            Log.d(TAG, "Venda encontrada - ID: " + sale.getId()
                    + ", Responsável: " + sale.getConsultantId()
                    + ", Produto: " + sale.getProduct()
                    + ", Data: " + sale.getSaleDate()
                    + ", Valor: " + sale.getPrice()
                    + ", Comissão: " + sale.getCommission()
                    + ", Registro: " + sale.getRecordId()
            );

            sb.append("ID: ").append(sale.getId()).append("\n")
                    .append("Responsável: ").append(sale.getConsultantId()).append("\n")
                    .append("Produto: ").append(sale.getProduct()).append("\n")
                    .append("Data: ").append(sale.getSaleDate()).append("\n")
                    .append("Valor: ").append(sale.getPrice()).append("\n")
                    .append("Comissão: ").append(sale.getCommission()).append("\n")
                    .append("Registro: ").append(sale.getRecordId()).append("\n\n\n");
            }

        //textResults.setText(sb.toString());
        Log.d(TAG, "Vendas de " + currentId + " exibidas com sucesso");
    }

}