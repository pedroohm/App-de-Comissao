package com.grupo6.appdecomissao.domain;

/*
    Essa classe servirá como um "cache em memória" para mapearmos os dados que receberemos da API
    Aqui armazenamos todas as entidades (como User, Goal, Sale, Record...) em um Map<id, Objeto>
    Temos também alguns maps adicionais para os relacionamentos entre as entidades (por exemplo, usuário -> suas regras)
*/

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

    /* MÉTODOS DE REMOÇÃO DE OBJETOS */

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

    public Set<Sale> getSales() {
        return new HashSet<>(sales.values());
    }
}