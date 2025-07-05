package com.grupo6.appdecomissao.domain;

/*
    Essa classe servirá como um "cache em memória" para mapearmos os dados que receberemos da API
    Aqui armazenamos todas as entidades (como User, Goal, Sale, Record...) em um Map<id, Objeto>
    Temos também alguns maps adicionais para os relacionamentos entre as entidades (por exemplo, usuário -> suas regras)
*/

import android.util.Log;
import com.grupo6.appdecomissao.remote.ApiCallback;
import com.grupo6.appdecomissao.remote.ApiRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataCache {
    // Singleton
    private static DataCache instance = null;

    /* ATRIBUTO QUE GUARDA O CURRENTID */
    private static String currentId = null;

    /* MAPS PARA AS ENTIDADES */

    /* Map para todos os usuários cadastrados */
    private final Map<String, User> users = new HashMap<>();

    /* Map para todas as metas*/
    private final Map<String, Goal> goals = new HashMap<>();

    /* Map para todas as vendas */
    private final Map<String, Sale> sales = new HashMap<>();

    /* Map para todos os registros */
    private final Map<String, Record> records = new HashMap<>();

    /* Map com todas as regras de comissão */
    private final Map<String, CommissionRule> commissionRules = new HashMap<>();

    /* Map para todos os processos */
    private final Map<String, Process> processes = new HashMap<>();

    /* Map para todas as etapas */
    private final Map<String, Stage> stages = new HashMap<>();

    /* INSTÂNCIA DO API REPOSITORY */
    private final ApiRepository apiRepository = new ApiRepository();

    /* MAPS PARA RELACIONAMENTO ENTRE AS ENTIDADES */

    /* Mapeia um usuário a todas as suas regras de comissões */
    private final Map<String, Set<String>> userCommissionRules = new HashMap<>();

    /* Mapeia um usuário a todas as suas metas */
    private final Map<String, Set<String>> userGoals = new HashMap<>();

    /* Mapeia um usuário a todas as suas vendas */
    private final Map<String, Set<String>> userSales = new HashMap<>();

    /* Mapeia um usuário a todos os seus registros */
    private final Map<String, Set<String>> userRecords = new HashMap<>();

    private DataCache() {}

    public static DataCache getInstance(){
        if (instance == null) {
            instance = new DataCache();
        }

        return instance;
    }

    /* MÉTODOS PARA COLOCAR OU ATUALIZAR DADOS DOS OBJETOS */
    public void putUser(User user) {
        users.put(user.getId(), user);

        // Inicializa os outros maps
        userCommissionRules.putIfAbsent(user.getId(), new HashSet<>());
        userGoals.putIfAbsent(user.getId(), new HashSet<>());
        userSales.putIfAbsent(user.getId(), new HashSet<>());
        userRecords.putIfAbsent(user.getId(), new HashSet<>());
    }

    public void putGoal(Goal goal) {
        goals.put(goal.getId(), goal);

        // Para cada consultor associado à meta, cria relacionamento user→rule
        for (String consultantId : goal.getAssignedConsultantIds()) {
            userGoals
                    .computeIfAbsent(consultantId, k -> new HashSet<>())
                    .add(goal.getId());
        }
    }

    public void putSale(Sale sale) {
        sales.put(sale.getId(), sale);

        // Adiciona a venda ao consultor relacionado
        userSales.computeIfAbsent(sale.getConsultantId(), k -> new HashSet<>()).add(sale.getId());
    }

    public void putRecord(Record record) {
        records.put(record.getId(), record);

        // Adiciona o registro ao consultor relacionado
        userRecords.computeIfAbsent(record.getResponsibleId(), k -> new HashSet<>()).add(record.getId());
    }

    public void putCommissionRule(CommissionRule rule) {
        commissionRules.put(rule.getId(), rule);

        // Para cada consultor associado à regra, cria relacionamento user→rule
        for (String consultantId : rule.getAssignedConsultantIds()) {
            userCommissionRules
                    .computeIfAbsent(consultantId, k -> new HashSet<>())
                    .add(rule.getId());
        }
    }

    public void putProcess(Process process) {
        processes.put(process.getId(), process);
    }

    public void putStage(Stage stage) {
        stages.put(stage.getId(), stage);
    }

    /* MÉTODOS DE REMOÇÃO DE OBJETOS */

    public void clearSales() {
        sales.clear();
        // Limpa também o relacionamento de vendas por usuário
        for (Set<String> saleSet : userSales.values()) {
            saleSet.clear();
        }
    }

    public void removeUser(String userId) {
        users.remove(userId);
        userCommissionRules.remove(userId);
        userGoals.remove(userId);
        userSales.remove(userId);
        userRecords.remove(userId);
    }

    public void removeGoal(String goalId) {
        goals.remove(goalId);

        for (Set<String> goalSet : userGoals.values()) {
            goalSet.remove(goalId);
        }
    }

    public void removeSale(String saleId) {
        sales.remove(saleId);
        for (Set<String> saleSet : userSales.values()) {
            saleSet.remove(saleId);
        }
    }

    public void removeRecord(String recordId) {
        records.remove(recordId);
        for (Set<String> recordSet : userRecords.values()) {
            recordSet.remove(recordId);
        }
    }

    public void removeCommissionRule(String ruleId) {
        commissionRules.remove(ruleId);
        for (Set<String> ruleSet : userCommissionRules.values()) {
            ruleSet.remove(ruleId);
        }
    }

    /* MÉTODOS GET E SET PARA CURRENT ID */

    public String getCurrentId() {
        return currentId;
    }

    public void setCurrentId(String id) {
        currentId = id;
    }

    /* MÉTODOS PARA BUSCA SIMPLES COM ID */
    public User getUserById(String userId) {
        return users.get(userId);
    }

    public Goal getGoalById(String goalId) {
        return goals.get(goalId);
    }

    public List<Goal> getAllGoals() {
        // Converte a Collection de valores para um novo ArrayList
        return new ArrayList<>(goals.values());
    }

    public Sale getSaleById(String saleId) {
        return sales.get(saleId);
    }

    public Record getRecordById(String recordId) {
        return records.get(recordId);
    }

    public List<User> getAllConsultants(){
        return new ArrayList<>(users.values());
    }

    public CommissionRule getCommissionRuleById(String ruleId) {
        return commissionRules.get(ruleId);
    }

    public List<CommissionRule> getAllCommissionRules() {
        return new ArrayList<>(commissionRules.values());
    }

    public List<Goal> getGoalsByUserId(String userId) {
        List<Goal> result = new ArrayList<>();
        Set<String> goalIds = userGoals.getOrDefault(userId, new HashSet<>());

        for (String goalId : goalIds) {
            Goal goal = goals.get(goalId);
            if (goal != null) {
                result.add(goal);
            }
        }
        return result;
    }


    /* MÉTODOS PARA BUSCA MAIORES */
    public Set<String> getUserCommissionRules(String userId) {
        return userCommissionRules.getOrDefault(userId, new HashSet<>());
    }

    public List<Sale> getSalesByUserId(String userId) {
        // lista vazia para armazenar o resultado.
        List<Sale> result = new ArrayList<>();

        Set<String> saleIds = userSales.getOrDefault(userId, new HashSet<>());

        for (String saleId : saleIds) {
            Sale sale = sales.get(saleId);

            if (sale != null) {
                result.add(sale);
            }
        }

        return result;
    }

    public List<User> getAllUsersFromCache() {
        return new ArrayList<>(users.values());
    }

    // Metodo para buscar um usuário específico pelo email
    public User getUserByEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail() != null && user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null; // Retorna null se não encontrar o email
    }

    public Set<Sale> getSales() {
        return new HashSet<>(sales.values());
    }

    public List<Process> getAllProcesses() {
        return new ArrayList<>(processes.values());
    }

    public List<Stage> getAllStages() {
        return new ArrayList<>(stages.values());
    }

    public List<Stage> getStagesByProcessId(String processId) {
        // Na API Rubeus, as etapas são globais e não específicas de um processo
        // Retornamos todas as etapas disponíveis
        return new ArrayList<>(stages.values());
    }

    // MÉTODOS UTILITÁRIOS ADICIONAIS
    /**
     * Retorna uma lista de nomes dos consultores associados a uma regra de comissão.
     */
    public List<String> getConsultantNamesByRule(CommissionRule rule) {
        List<String> nomes = new ArrayList<>();
        for (String id : rule.getAssignedConsultantIds()) {
            User user = users.get(id);
            if (user != null) {
                nomes.add(user.getName());
            }
        }
        return nomes;
    }

    /**
     * Retorna o nome do produto associado a uma regra de comissão.
     */
    public String getProductNameByRule(CommissionRule rule) {
        if (rule.getProductName() != null && !rule.getProductName().isEmpty()) {
            return rule.getProductName();
        }
        // Lógica adicional pode ser implementada aqui se necessário
        return "Produto não especificado";
    }


    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<CommissionRule> getAllRules() {
        return new ArrayList<>(commissionRules.values());
    }


    public List<Sale> getAllSales() {
        return new ArrayList<>(sales.values());
    }

    public void loadUsersFromApi(String origin, String token, ApiCallback<Void> callback) {
        Log.d("DATACACHE", "Iniciando carregamento de usuários da API Rubeus");
        Log.d("DATACACHE", "Origin: " + origin + ", Token: " + token);
        
        apiRepository.getAllUsers(origin, token, new ApiCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> userList) {
                Log.d("DATACACHE", "Usuários recebidos da API: " + userList.size());
                
                // Log de cada usuário recebido
                for (User user : userList) {
                    Log.d("DATACACHE", "Usuário recebido - ID: " + user.getId() + 
                          ", Nome: " + user.getName() + 
                          ", Email: " + user.getEmail() + 
                          ", Profile: " + user.getProfile());
                }
                
                users.clear();
                for (User user : userList) {
                    users.put(user.getId(), user);
                }
                
                Log.d("DATACACHE", "Usuários carregados no cache: " + users.size());
                
                // Log dos usuários no cache após carregamento
                for (User user : users.values()) {
                    Log.d("DATACACHE", "Usuário no cache - ID: " + user.getId() + 
                          ", Nome: " + user.getName() + 
                          ", Email: " + user.getEmail() + 
                          ", Profile: " + user.getProfile());
                }
                
                callback.onSuccess(null);
            }

            @Override
            public void onError(String error) {
                Log.e("DATACACHE", "Erro ao carregar usuários: " + error);
                callback.onError(error);
            }
        });
    }

    public void loadRulesFromApi(ApiCallback<Void> callback) {
        Log.d("DATACACHE", "Iniciando carregamento de regras da API PHP");
        
        apiRepository.getCommissionRules(new ApiCallback<List<CommissionRule>>() {
            @Override
            public void onSuccess(List<CommissionRule> rules) {
                Log.d("DATACACHE", "Regras recebidas da API PHP: " + rules.size());
                
                for (CommissionRule rule : rules) {
                    Log.d("DATACACHE", "Regra recebida - ID: " + rule.getId() + 
                          ", Nome: " + rule.getName() + 
                          ", Percentual: " + rule.getCommissionPercentage());
                    putCommissionRule(rule);
                }
                
                Log.d("DATACACHE", "Regras carregadas no cache: " + getAllRules().size());
                callback.onSuccess(null);
            }

            @Override
            public void onError(String error) {
                Log.e("DATACACHE", "Erro ao carregar regras: " + error);
                callback.onError(error);
            }
        });
    }

    public void loadProcessesFromApi(String origin, String token, ApiCallback<Void> callback) {
        Log.d("DATACACHE", "Iniciando carregamento de processos da API Rubeus");
        
        apiRepository.getAllProcesses(origin, token, new ApiCallback<List<Process>>() {
            @Override
            public void onSuccess(List<Process> processList) {
                Log.d("DATACACHE", "Processos recebidos da API: " + processList.size());
                
                processes.clear();
                for (Process process : processList) {
                    processes.put(process.getId(), process);
                    Log.d("DATACACHE", "Processo carregado - ID: " + process.getId() + 
                          ", Nome: " + process.getName());
                }
                
                Log.d("DATACACHE", "Processos carregados no cache: " + processes.size());
                callback.onSuccess(null);
            }

            @Override
            public void onError(String error) {
                Log.e("DATACACHE", "Erro ao carregar processos: " + error);
                callback.onError(error);
            }
        });
    }

    public void loadStagesFromApi(String origin, String token, String processId, ApiCallback<Void> callback) {
        Log.d("DATACACHE", "Iniciando carregamento de etapas da API Rubeus para processo: " + processId);
        
        apiRepository.getStagesByProcess(origin, token, processId, new ApiCallback<List<Stage>>() {
            @Override
            public void onSuccess(List<Stage> stageList) {
                Log.d("DATACACHE", "Etapas recebidas da API: " + stageList.size());
                
                // Remove etapas antigas do processo
                stages.entrySet().removeIf(entry -> entry.getValue().getProcessId() == null);
                
                for (Stage stage : stageList) {
                    stages.put(stage.getId(), stage);
                    Log.d("DATACACHE", "Etapa carregada - ID: " + stage.getId() + 
                          ", Título: " + stage.getTitulo() + 
                          ", Índice: " + stage.getIndice());
                }
                
                Log.d("DATACACHE", "Etapas carregadas no cache: " + stages.size());
                callback.onSuccess(null);
            }

            @Override
            public void onError(String error) {
                Log.e("DATACACHE", "Erro ao carregar etapas da API: " + error);
                Log.d("DATACACHE", "Carregando etapas mockadas como fallback...");
                
                // Carregar etapas mockadas quando a API falhar
                loadMockStages();
                
                Log.d("DATACACHE", "Etapas mockadas carregadas no cache: " + stages.size());
                callback.onSuccess(null);
            }
        });
    }

    public void loadMockGoalsAndSales() {
        Log.d("DATACACHE", "Carregando metas e vendas mockadas");
        
        // Adicionar algumas metas mockadas
        Set<String> consultantIds1 = new HashSet<>();
        consultantIds1.add("84");
        consultantIds1.add("85");
        
        Set<String> consultantIds2 = new HashSet<>();
        consultantIds2.add("84");
        
        Goal goal1 = new Goal("1", consultantIds1, "Meta de vendas mensal", 10000.0, 500.0, false);
        Goal goal2 = new Goal("2", consultantIds2, "Meta de vendas trimestral", 30000.0, 1000.0, false);
        putGoal(goal1);
        putGoal(goal2);
        
        // Adicionar algumas vendas mockadas
        Sale sale1 = new Sale("1", "84", "Produto A", 1000.0, "2024-01-15", 100.0, "record1");
        Sale sale2 = new Sale("2", "85", "Produto B", 1500.0, "2024-01-20", 150.0, "record2");
        Sale sale3 = new Sale("3", "84", "Produto C", 2000.0, "2024-01-25", 200.0, "record3");
        putSale(sale1);
        putSale(sale2);
        putSale(sale3);
        
        Log.d("DATACACHE", "Metas e vendas mockadas carregadas - Metas: " + getAllGoals().size() + 
              ", Vendas: " + getAllSales().size());
    }

    public void loadMockStages() {
        Log.d("DATACACHE", "Carregando etapas mockadas");
        
        // Limpar etapas existentes
        stages.clear();
        
        // Adicionar etapas mockadas baseadas no formato da API Rubeus
        Stage stage1 = new Stage("0", "Sem etapa", "0");
        Stage stage2 = new Stage("1", "Potencial", "1");
        Stage stage3 = new Stage("2", "Interessado", "2");
        Stage stage4 = new Stage("3", "Inscrito parcial", "3");
        Stage stage5 = new Stage("4", "Inscrito", "4");
        Stage stage6 = new Stage("5", "Confirmado", "5");
        Stage stage7 = new Stage("6", "Convocado", "6");
        Stage stage8 = new Stage("7", "Matriculado", "7");
        
        putStage(stage1);
        putStage(stage2);
        putStage(stage3);
        putStage(stage4);
        putStage(stage5);
        putStage(stage6);
        putStage(stage7);
        putStage(stage8);
        
        Log.d("DATACACHE", "Etapas mockadas carregadas: " + stages.size());
        for (Stage stage : stages.values()) {
            Log.d("DATACACHE", "Etapa mockada - ID: " + stage.getId() + 
                  ", Título: " + stage.getTitulo() + 
                  ", Índice: " + stage.getIndice());
        }
    }
}