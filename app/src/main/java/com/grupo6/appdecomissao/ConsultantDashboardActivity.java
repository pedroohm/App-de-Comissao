package com.grupo6.appdecomissao;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ConsultantDashboardActivity extends Activity {
    private static final String TAG = "DashboardActivity";
    private ApiRepository apiRepository;
    private TextView textResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultant_dashboard);

        Log.d(TAG, "Activity criada");

        textResults = findViewById(R.id.textResults);
        apiRepository = new ApiRepository();

        loadRecords();
    }

    private void loadRecords() {
        Log.d(TAG, "Iniciando carregamento de registros");

        String origin = "8";
        String token = "b116d29f1252d2ce144d5cb15fb14c7f";

        String status = "Em andamento"; // Status a ser filtrado
        String nomeResponsavel = "Juan Santos Freire"; // Nome a ser filtrado

        apiRepository.getRecordsByStatus(origin, token, status, new ApiCallback<List<Record>>() {
            @Override
            public void onSuccess(List<Record> records) {
                Log.d(TAG, "Sucesso ao carregar registros. Total: " + records.size());

                runOnUiThread(() -> {
                    try {
                        List<Record> registrosFiltrados = new java.util.ArrayList<>();
                        for (Record record : records) {
                            Log.d(TAG, "Record responsavelNome: '" + record.getResponsavelNome() + "'");
                            if (nomeResponsavel.equalsIgnoreCase(record.getResponsavelNome())) {
                                registrosFiltrados.add(record);
                            }
                        }


                        if (registrosFiltrados.isEmpty()) {
                            textResults.setText("Nenhum registro encontrado para " + nomeResponsavel);
                            Toast.makeText(ConsultantDashboardActivity.this,
                                    "Nenhum registro encontrado para " + nomeResponsavel,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        StringBuilder sb = new StringBuilder();
                        sb.append("=== REGISTROS DE ").append(nomeResponsavel.toUpperCase()).append(" ===\n\n");

                        for (Record record : registrosFiltrados) {
                            // Log no Logcat de cada registro filtrado
                            Log.d(TAG, "Registro encontrado - ID: " + record.getId()
                                    + ", Cliente: " + record.getClientName()
                                    + ", Responsável: " + record.getResponsavelNome()
                                    + ", Status: " + record.getStatus()
                                    + ", Etapa: " + record.getStage());

                            sb.append("ID: ").append(record.getId()).append("\n")
                                    .append("Cliente: ").append(record.getClientName()).append("\n")
                                    .append("Responsável: ").append(record.getResponsavelNome()).append("\n")
                                    .append("Status: ").append(record.getStatus()).append("\n")
                                    .append("Etapa: ").append(record.getStage()).append("\n\n")
                                    .append("----------------\n\n");
                        }

                        textResults.setText(sb.toString());
                        Log.d(TAG, "Registros de " + nomeResponsavel + " exibidos com sucesso");

                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao formatar dados", e);
                        handleError("Erro ao formatar os dados");
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Erro ao carregar registros: " + errorMessage);
                handleError(errorMessage);
            }
        });
    }
    private void handleError(String errorMessage) {
        runOnUiThread(() -> {
            Log.e(TAG, "Erro: " + errorMessage);
            Toast.makeText(this, "Erro: " + errorMessage, Toast.LENGTH_SHORT).show();
        });
        Log.e(TAG, "Erro: " + errorMessage);
    }

}