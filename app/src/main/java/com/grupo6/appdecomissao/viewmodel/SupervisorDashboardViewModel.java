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

public class SupervisorDashboardViewModel extends ViewModel {

    private static final String TAG = "SupervisorVM";
    private final ApiRepository apiRepository = new ApiRepository();
    private final DataCache dataCache = DataCache.getInstance();

    // LiveData para a UI observar
    private final MutableLiveData<List<User>> teamConsultants = new MutableLiveData<>();
    private final MutableLiveData<List<Sale>> teamSales = new MutableLiveData<>();
    private final MutableLiveData<List<Goal>> teamGoals = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private List<Sale> originalSalesList = new ArrayList<>();
    private List<Goal> originalGoalsList = new ArrayList<>();
    private boolean dataLoaded = false;

    // Getters para a Activity
    public LiveData<List<User>> getTeamConsultants() { return teamConsultants; }
    public LiveData<List<Sale>> getTeamSales() { return teamSales; }
    public LiveData<List<Goal>> getTeamGoals() { return teamGoals; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadSupervisorData(String supervisorId, String origin, String token) {
        if (dataLoaded) return;
        isLoading.setValue(true);
        dataLoaded = true;

        dataCache.clearSales(); // Limpa vendas antigas para evitar duplicação

        User supervisor = dataCache.getUserById(supervisorId);
        if (supervisor == null || supervisor.getConsultantIds() == null || supervisor.getConsultantIds().isEmpty()) {
            errorMessage.setValue("Supervisor não encontrado ou não possui consultores.");
            isLoading.setValue(false);
            return;
        }

        List<User> consultantList = new ArrayList<>();
        List<String> consultantIds = supervisor.getConsultantIds();
        for(String id : consultantIds){
            User consultant = dataCache.getUserById(id);
            if(consultant != null){
                consultantList.add(consultant);
            }
        }
        teamConsultants.postValue(consultantList);

        int totalApiCalls = 0;
        for (User consultant : consultantList) {
            totalApiCalls += dataCache.getUserCommissionRules(consultant.getId()).size();
        }

        // Se não houver regras, não haverá chamadas. Processamos com a lista vazia.
        if (totalApiCalls == 0) {
            processAndPostResults(new ArrayList<>());
            return;
        }

        AtomicInteger pendingLoads = new AtomicInteger(totalApiCalls);
        List<Record> allTeamRecords = new ArrayList<>();

        if (consultantList.isEmpty()){
            processAndPostResults(new ArrayList<>());
            return;
        }

        for (User consultant : consultantList) {
            Set<String> rules = dataCache.getUserCommissionRules(consultant.getId());

            for(String ruleId : rules){
                CommissionRule rule = dataCache.getCommissionRuleById(ruleId);
                if (rule == null) {
                    // Se uma regra for nula, decrementamos o contador mesmo assim para não bloquear a lógica.
                    if (pendingLoads.decrementAndGet() == 0) {
                        processAndPostResults(allTeamRecords);
                    }
                    continue;
                }

                apiRepository.getRecordsByProcessAndStage(origin, token, rule.getProcessId(), rule.getStage(), consultant.getId(), new ApiCallback<List<Record>>() {
                    @Override
                    public void onSuccess(List<Record> records) {
                        synchronized (allTeamRecords) {
                            allTeamRecords.addAll(records);
                        }
                        // 3. A condição agora só será verdadeira após a ÚLTIMA chamada retornar.
                        if (pendingLoads.decrementAndGet() == 0) {
                            processAndPostResults(allTeamRecords);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Erro ao carregar registros para o consultor " + consultant.getId() + ": " + error);
                        if (pendingLoads.decrementAndGet() == 0) {
                            processAndPostResults(allTeamRecords);
                        }
                    }
                });
            }
        }
    }

    private void processAndPostResults(List<Record> records) {
        int saleId = 1;
        dataCache.clearSales();
        List<String> allConsultantIds = teamConsultants.getValue().stream().map(User::getId).collect(Collectors.toList());

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
                continue;
            }
            String formattedDate = "N/A";
            if (record.getLastDate() != null && !record.getLastDate().isEmpty()) {
                try {
                    String datePart = record.getLastDate().substring(0, 10);
                    DateTimeFormatter inputFormatter = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    }
                    DateTimeFormatter outputFormatter = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    }
                    LocalDate dateObj = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        dateObj = LocalDate.parse(datePart, inputFormatter);
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        formattedDate = dateObj.format(outputFormatter);
                    }
                } catch (Exception e) {
                    formattedDate = "N/A";
                }
            }

            double commissionValue = offerValue * comissionPercentage / 100;

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

        // Agrega metas de todos os consultores da equipe
        List<Goal> allGoals = new ArrayList<>();
        for (String consultantId : allConsultantIds){
            allGoals.addAll(dataCache.getGoalsByUserId(consultantId));
        }

        this.originalSalesList = new ArrayList<>(dataCache.getSales());
        this.originalGoalsList = new ArrayList<>(allGoals);

        // Dispara a atualização para a UI com os dados processados
        teamSales.postValue(this.originalSalesList);
        teamGoals.postValue(this.originalGoalsList);
        isLoading.postValue(false);
    }

    public void applyFilter(String period) {
        if (originalSalesList == null) return;
        if ("Todo o período".equals(period)) {
            teamSales.setValue(originalSalesList);
            teamGoals.setValue(originalGoalsList);
            return;
        }

        LocalDate today = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            today = LocalDate.now();
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        }

        LocalDate finalToday = today;
        DateTimeFormatter finalFormatter = formatter;
        List<Sale> filteredList = originalSalesList.stream()
                .filter(sale -> {
                    if ("N/A".equals(sale.getSaleDate())) return false;
                    try {
                        LocalDate saleDate = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            saleDate = LocalDate.parse(sale.getSaleDate(), finalFormatter);
                        }
                        switch (period) {
                            case "Este mês":
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    return saleDate.getMonth() == finalToday.getMonth() && saleDate.getYear() == finalToday.getYear();
                                }
                            case "Últimos 3 meses":
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    return saleDate.isAfter(finalToday.minusMonths(3));
                                }
                            case "Últimos 6 meses":
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    return saleDate.isAfter(finalToday.minusMonths(6));
                                }
                            default: return false;
                        }
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        teamSales.setValue(filteredList);
        teamGoals.setValue(this.originalGoalsList);
    }
}
