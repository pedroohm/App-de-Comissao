package com.grupo6.appdecomissao.viewmodel;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
public class ConsultantDashboardViewModel extends ViewModel {

    private static final String TAG = "DashboardViewModel";
    private final ApiRepository apiRepository = new ApiRepository();
    private final DataCache dataCache = DataCache.getInstance();

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

            double commissionValue = offerValue * comissionPercentage / 100;
            String date = record.getLastDate() != null && record.getLastDate().length() >= 11 ? record.getLastDate().substring(0, 11) : "N/A";

            Sale sale = new Sale(
                    String.valueOf(saleId),
                    record.getResponsibleId(),
                    record.getOfferName(),
                    offerValue,
                    date,
                    commissionValue,
                    record.getId()
            );
            dataCache.putSale(sale);
            saleId++;
        }

        // Dispara a atualização para a UI com os dados processados
        salesList.postValue(new ArrayList<>(dataCache.getSalesByUserId(consultantId)));
        goalsList.postValue(dataCache.getGoalsByUserId(consultantId));
        isLoading.postValue(false);
    }
}
