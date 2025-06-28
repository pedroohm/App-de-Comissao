package com.grupo6.appdecomissao.viewmodel;

import android.os.Build;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grupo6.appdecomissao.domain.CommissionRule;
import com.grupo6.appdecomissao.domain.DataCache;
import com.grupo6.appdecomissao.domain.Goal;
import com.grupo6.appdecomissao.domain.Record;
import com.grupo6.appdecomissao.domain.Sale;
import com.grupo6.appdecomissao.domain.User;
import com.grupo6.appdecomissao.remote.ApiCallback;
import com.grupo6.appdecomissao.remote.ApiRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ConsultantDashboardViewModel extends ViewModel {

    private static final String TAG = "DashboardViewModel";
    private final ApiRepository apiRepository = new ApiRepository();
    private final DataCache dataCache = DataCache.getInstance();

    private List<Sale> originalSalesList = new ArrayList<>();
    private List<Goal> originalGoalsList = new ArrayList<>();
    private final MutableLiveData<List<Sale>> salesList = new MutableLiveData<>();
    private final MutableLiveData<List<Goal>> goalsList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private boolean dataLoaded = false;

    // Métodos públicos para a Activity observar
    public LiveData<List<Sale>> getSalesList() { return salesList; }
    public LiveData<List<Goal>> getGoalsList() { return goalsList; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // Ponto de entrada para carregar todos os dados
    public void loadConsultantData(String consultantId, String origin, String token) {
        if (dataLoaded) return;

        isLoading.setValue(true);
        dataLoaded = true;

        // Limpa os dados de vendas para evitar duplicação em novas cargas
        dataCache.clearSales();

        Set<String> rules = dataCache.getUserCommissionRules(consultantId);
        if (rules == null || rules.isEmpty()) {
            isLoading.setValue(false);
            errorMessage.setValue("Consultor não possui regras de comissão.");
            // Atualiza as listas com dados vazios para limpar a UI
            salesList.setValue(new ArrayList<>());
            goalsList.setValue(dataCache.getGoalsByUserId(consultantId));
            return;
        }

        AtomicInteger pendingLoads = new AtomicInteger(rules.size());
        List<Record> allFilteredRecords = new ArrayList<>();

        for (String ruleId : rules) {
            CommissionRule rule = dataCache.getCommissionRuleById(ruleId);
            if (rule == null) {
                if (pendingLoads.decrementAndGet() == 0) {
                    processAndPostResults(allFilteredRecords, consultantId);
                }
                continue;
            }

            apiRepository.getRecordsByProcessAndStage(origin, token, rule.getProcessId(), rule.getStage(), consultantId, new ApiCallback<List<Record>>() {
                @Override
                public void onSuccess(List<Record> records) {
                    synchronized (allFilteredRecords) {
                        allFilteredRecords.addAll(records);
                    }
                    if (pendingLoads.decrementAndGet() == 0) {
                        processAndPostResults(allFilteredRecords, consultantId);
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Erro ao carregar registros para a regra " + ruleId + ": " + error);
                    if (pendingLoads.decrementAndGet() == 0) {
                        processAndPostResults(allFilteredRecords, consultantId);
                    }
                }
            });
        }
    }

    private void processAndPostResults(List<Record> records, String consultantId) {
        int saleId = 1;
        dataCache.clearSales();

        for (Record record : records) {
            Set<String> comissionRulesIds = dataCache.getUserCommissionRules(record.getResponsibleId());
            double comissionPercentage = 0.0;

            for (String comissionRuleId : comissionRulesIds) {
                CommissionRule rule = dataCache.getCommissionRuleById(comissionRuleId);
                if (rule != null && rule.getProcessId().equals(record.getProcessId())) {
                    comissionPercentage = rule.getCommissionPercentage();
                    break;
                }
            }

            double offerValue = 0;
            try {
                if (record.getOfferValue() != null) {
                    offerValue = Double.parseDouble(record.getOfferValue());
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Valor da oferta inválido para o registro: " + record.getId());
                continue;
            }

            String formattedDate = "N/A";
            if (record.getLastDate() != null && !record.getLastDate().isEmpty()) {
                try {
                    // Pega apenas a parte da data da string (os 10 primeiros caracteres)
                    String datePart = record.getLastDate().substring(0, 10);

                    // Define o formato de entrada e o de saída
                    DateTimeFormatter inputFormatter = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    }
                    DateTimeFormatter outputFormatter = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    }

                    // Converte a string de entrada para um objeto de data
                    LocalDate dateObj = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dateObj = LocalDate.parse(datePart, inputFormatter);
                    }

                    // Formata para o nosso padrão de app "dd/MM/yyyy"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        formattedDate = dateObj.format(outputFormatter);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Não foi possível parsear a data da API: " + record.getLastDate(), e);
                    formattedDate = "N/A";
                }
            }

            double commissionValue = offerValue * comissionPercentage / 100;
            String date = record.getLastDate() != null && record.getLastDate().length() >= 11 ? record.getLastDate().substring(0, 11) : "N/A";

            Sale sale = new Sale(
                    String.valueOf(saleId),
                    record.getResponsibleId(),
                    record.getOfferName(),
                    offerValue,
                    formattedDate,
                    commissionValue,
                    record.getId()
            );
            dataCache.putSale(sale);
            saleId++;
        }

        this.originalSalesList = new ArrayList<>(dataCache.getSalesByUserId(consultantId));
        this.originalGoalsList = new ArrayList<>(dataCache.getGoalsByUserId(consultantId));

        // Dispara a atualização para a UI com os dados processados
        salesList.postValue(this.originalSalesList);
        goalsList.postValue(this.originalGoalsList);
        isLoading.postValue(false);
    }

    public void applyFilter(String period) {
        if (originalSalesList == null) return; // Proteção para caso os dados ainda não tenham carregado

        // Se o usuário escolher 'Todo o periodo', simplesmente restaura a lista original
        if ("Todo o período".equals(period)) {
            salesList.setValue(originalSalesList);
            goalsList.setValue(originalGoalsList);
            return;
        }

        // Define a data de hoje e o formatador para as datas que vêm do cache
        LocalDate today;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            today = LocalDate.now();
        } else {
            today = null;
        }
        DateTimeFormatter formatter;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        } else {
            formatter = null;
        }

        // Usa a API de Streams do Java para filtrar a lista. É mais moderno e legível.
        List<Sale> filteredList = originalSalesList.stream()
                .filter(sale -> {
                    if ("N/A".equals(sale.getSaleDate())) {
                        return false;
                    }

                    try {
                        LocalDate saleDate = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            saleDate = LocalDate.parse(sale.getSaleDate(), formatter);
                        }
                        switch (period) {
                            case "Este mês":
                                // A venda ocorreu no mesmo mês e mesmo ano que hoje?
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    return saleDate.getMonth() == today.getMonth() && saleDate.getYear() == today.getYear();
                                }
                            case "Últimos 3 meses":
                                // A venda ocorreu depois da data de "hoje menos 3 meses"?
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    return saleDate.isAfter(today.minusMonths(3));
                                }
                            case "Últimos 6 meses":
                                // A venda ocorreu depois da data de "hoje menos 6 meses"?
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    return saleDate.isAfter(today.minusMonths(6));
                                }
                            default:
                                return false;
                        }
                    } catch (Exception e) {
                        // Se a data estiver em um formato inválido, ignora este item no filtro
                        Log.e(TAG, "Erro ao parsear data: " + sale.getSaleDate(), e);
                        return false;
                    }
                })
                .collect(Collectors.toList());

        // Atualiza o LiveData com a nova lista filtrada. A UI reagirá a isso.
        salesList.setValue(filteredList);
        goalsList.setValue(this.originalGoalsList);
    }
}
