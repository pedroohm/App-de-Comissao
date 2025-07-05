package com.grupo6.appdecomissao.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.grupo6.appdecomissao.R;
import com.grupo6.appdecomissao.domain.CommissionRule;
import com.grupo6.appdecomissao.domain.DataCache;
import com.grupo6.appdecomissao.domain.Goal;
import com.grupo6.appdecomissao.domain.User;
import com.grupo6.appdecomissao.remote.MockApiClient;
import com.grupo6.appdecomissao.remote.MockApiEndpoints;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ManageConsultantsActivity extends AppCompatActivity {

    private static final String TAG = "ManageConsultantsActivity";
    private DataCache dataCache = DataCache.getInstance();
    private final String SUPERVISOR_ID = dataCache.getCurrentId();
    private LinearLayout consultantsContainer;
    private List<User> consultants;
    private List<User> filteredConsultants;
    private View addConsultantFormView;
    private TextInputEditText searchEditText;

    // Variáveis para armazenar as seleções do formulário de adição
    private ArrayList<String> selectedGoalIds = new ArrayList<>();
    private ArrayList<String> selectedCommissionRuleIds = new ArrayList<>();

    // Flag para controlar se os dados já foram carregados do servidor na inicialização
    private boolean initialDataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_consultants);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView addConsultantIcon = findViewById(R.id.add_consultant_icon);
        if (addConsultantIcon != null) {
            addConsultantIcon.setOnClickListener(v -> toggleAddConsultantForm());
        }

        consultantsContainer = findViewById(R.id.consultants_container);
        searchEditText = findViewById(R.id.search_edit_text);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterConsultants(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Chamada inicial para popular o DataCache APENAS se ainda não foi feito
        if (!initialDataLoaded) {
            loadAllDataFromMockServerToCache();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Sempre atualiza a UI a partir do DataCache quando a Activity volta ao foco
        updateConsultantListFromCache();
    }

    private void loadAllDataFromMockServerToCache() {
        Retrofit retrofit = MockApiClient.getInstance();
        MockApiEndpoints apiService = retrofit.create(MockApiEndpoints.class);

        apiService.getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (User user : response.body()) {
                        dataCache.putUser(user);
                    }
                    Log.d(TAG, "Todos os usuários do mock populados no DataCache: " + response.body().size());
                } else {
                    Log.e(TAG, "Erro ao carregar usuários do mock API: " + response.code());
                }
                loadGoalsAndRulesFromMockServer();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e(TAG, "Falha ao conectar para buscar usuários do mock: " + t.getMessage(), t);
                Toast.makeText(ManageConsultantsActivity.this, "Falha na conexão inicial com o mock.", Toast.LENGTH_LONG).show();
                loadGoalsAndRulesFromMockServer();
            }
        });
    }


    // Busca metas do servidor mockado e as adiciona ao DataCache.
    private void loadGoalsAndRulesFromMockServer() {
        Retrofit retrofit = MockApiClient.getInstance();
        MockApiEndpoints apiService = retrofit.create(MockApiEndpoints.class);

        apiService.getGoals().enqueue(new Callback<List<Goal>>() {
            @Override
            public void onResponse(Call<List<Goal>> call, Response<List<Goal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Goal goal : response.body()) {
                        dataCache.putGoal(goal);
                    }
                    Log.d(TAG, "Metas do mock populadas no DataCache: " + response.body().size());
                } else {
                    Log.e(TAG, "Erro ao carregar metas do mock API: " + response.code());
                }
                loadCommissionRulesFromMockServer();
            }

            @Override
            public void onFailure(Call<List<Goal>> call, Throwable t) {
                Log.e(TAG, "Falha ao conectar para buscar metas do mock: " + t.getMessage(), t);
                Toast.makeText(ManageConsultantsActivity.this, "Falha na conexão ao carregar metas do mock.", Toast.LENGTH_LONG).show();
                loadCommissionRulesFromMockServer();
            }
        });
    }


    // Busca regras de comissão do servidor mockado e as adiciona ao DataCache.
    private void loadCommissionRulesFromMockServer() {
        Retrofit retrofit = MockApiClient.getInstance();
        MockApiEndpoints apiService = retrofit.create(MockApiEndpoints.class);

        apiService.getCommissionRules().enqueue(new Callback<List<CommissionRule>>() {
            @Override
            public void onResponse(Call<List<CommissionRule>> call, Response<List<CommissionRule>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (CommissionRule rule : response.body()) {
                        dataCache.putCommissionRule(rule);
                    }
                    Log.d(TAG, "Regras de comissão do mock populadas no DataCache: " + response.body().size());
                } else {
                    Log.e(TAG, "Erro ao carregar regras do mock API: " + response.code());
                }
                initialDataLoaded = true;
                updateConsultantListFromCache();
            }

            @Override
            public void onFailure(Call<List<CommissionRule>> call, Throwable t) {
                Log.e(TAG, "Falha ao conectar para buscar regras do mock: " + t.getMessage(), t);
                Toast.makeText(ManageConsultantsActivity.this, "Falha na conexão ao carregar regras do mock.", Toast.LENGTH_LONG).show();
                initialDataLoaded = true;
                updateConsultantListFromCache();
            }
        });
    }

    // Atualiza a lista interna de consultores a partir do DataCache.
    private void updateConsultantListFromCache() {
        List<User> allUsersFromCache = dataCache.getAllConsultants();
        consultants = new ArrayList<>();

        for (User user : allUsersFromCache) {
            if ("Consultor".equalsIgnoreCase(user.getProfile()) && SUPERVISOR_ID.equals(user.getSupervisorId())) {
                consultants.add(user);
            }
        }
        Log.d(TAG, "Lista 'consultants' filtrada do DataCache (apenas consultores do supervisor " + SUPERVISOR_ID + "): " + consultants.size());
        filterConsultants(searchEditText.getText().toString());
    }


    // Filtra a lista de consultores com base no texto de pesquisa e atualiza a UI. query e o texto a ser usado para filtrar.
    private void filterConsultants(String query) {
        filteredConsultants = new ArrayList<>();

        if (consultants != null) {
            if (query.isEmpty()) {
                filteredConsultants.addAll(consultants);
            } else {
                String lowerCaseQuery = query.toLowerCase();
                for (User consultant : consultants) {
                    if (consultant.getName() != null && consultant.getName().toLowerCase().contains(lowerCaseQuery)) {
                        filteredConsultants.add(consultant);
                    }
                }
            }
        }
        showConsultants();
    }

    public void showConsultants(){
        consultantsContainer.removeAllViews();

        if (addConsultantFormView != null && addConsultantFormView.getParent() == null) {
            consultantsContainer.addView(addConsultantFormView, 0);
        }

        if (filteredConsultants != null && !filteredConsultants.isEmpty()) {
            for (User consultant : filteredConsultants) {
                addConsultantView(consultant);
            }
        } else {
            Log.w(TAG, "Nenhum consultor encontrado para exibir (após filtro ou lista vazia).");
        }
    }

    // Adiciona a view de um consultor individual ao container.
    public void addConsultantView(User consultant){
        LayoutInflater inflater = LayoutInflater.from(this);
        View consultorView = inflater.inflate(R.layout.item_consultant_detail, consultantsContainer, false);

        TextView consultantName = consultorView.findViewById(R.id.consultor_nome);
        ImageView deleteIcon = consultorView.findViewById(R.id.delete_icon);
        ImageView expandArrow = consultorView.findViewById(R.id.expand_arrow);

        LinearLayout detailsLayout = consultorView.findViewById(R.id.details_layout);
        TextInputEditText consultantEmailEditText = consultorView.findViewById(R.id.edit_text_email_details);
        TextInputEditText consultantPasswordEditText = consultorView.findViewById(R.id.edit_text_password_details);
        AutoCompleteTextView autoCompleteRulesDetails = consultorView.findViewById(R.id.auto_complete_rules_details);
        AutoCompleteTextView autoCompleteGoalsDetails = consultorView.findViewById(R.id.auto_complete_goals_details);
        Button buttonCancelUpdate = consultorView.findViewById(R.id.button_cancel_update);
        Button buttonSaveUpdate = consultorView.findViewById(R.id.button_save_update);


        final Set<String> currentSelectedGoalIds = new HashSet<>();
        final Set<String> currentSelectedCommissionRuleIds = new HashSet<>();

        // Inicializa as listas de seleção com as associações ATUAIS do consultor no DataCache
        List<Goal> userGoals = dataCache.getGoalsByUserId(consultant.getId());
        Set<String> userRulesIds = dataCache.getUserCommissionRules(consultant.getId());

        for (Goal goal : userGoals) {
            currentSelectedGoalIds.add(goal.getId());
        }
        currentSelectedCommissionRuleIds.addAll(userRulesIds); // Adiciona todos os IDs do Set


        consultantName.setText(consultant.getName());
        consultantEmailEditText.setText(consultant.getEmail());
        consultantPasswordEditText.setText(consultant.getPassword() != null && !consultant.getPassword().isEmpty() ? consultant.getPassword() : "");

        // Formata as regras para exibição inicial na caixinha
        StringBuilder rulesText = new StringBuilder();
        if (!currentSelectedCommissionRuleIds.isEmpty()) {
            List<String> ruleNames = new ArrayList<>();
            for (String ruleId : currentSelectedCommissionRuleIds) {
                CommissionRule rule = dataCache.getCommissionRuleById(ruleId);
                if (rule != null) {
                    ruleNames.add(rule.getName());
                }
            }
            rulesText.append(String.join(", ", ruleNames));
        } else {
            rulesText.append("Nenhuma regra selecionada");
        }
        autoCompleteRulesDetails.setText(rulesText.toString());

        // Formata as metas para exibição inicial na caixinha
        StringBuilder goalsText = new StringBuilder();
        if (!currentSelectedGoalIds.isEmpty()) {
            List<String> goalDescriptions = new ArrayList<>();
            for (String goalId : currentSelectedGoalIds) {
                Goal goal = dataCache.getGoalById(goalId);
                if (goal != null) {
                    goalDescriptions.add(goal.getDescription());
                }
            }
            goalsText.append(String.join(", ", goalDescriptions));
        } else {
            goalsText.append("Nenhuma meta selecionada");
        }
        autoCompleteGoalsDetails.setText(goalsText.toString());

        detailsLayout.setVisibility(View.GONE);

        autoCompleteRulesDetails.setOnClickListener(v -> {
            List<CommissionRule> allRules = dataCache.getAllCommissionRules();
            CharSequence[] ruleNames = new CharSequence[allRules.size()];
            boolean[] checkedRules = new boolean[allRules.size()];

            for (int i = 0; i < allRules.size(); i++) {
                ruleNames[i] = allRules.get(i).getName();
                checkedRules[i] = currentSelectedCommissionRuleIds.contains(allRules.get(i).getId());
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(ManageConsultantsActivity.this);
            builder.setTitle("Selecione as Regras");
            builder.setMultiChoiceItems(ruleNames, checkedRules, (dialog, which, isChecked) -> {
                String ruleId = allRules.get(which).getId();
                if (isChecked) {
                    currentSelectedCommissionRuleIds.add(ruleId);
                } else {
                    currentSelectedCommissionRuleIds.remove(ruleId);
                }
            });

            builder.setPositiveButton("OK", (dialog, which) -> {
                StringBuilder updatedRuleNames = new StringBuilder();
                if (currentSelectedCommissionRuleIds.isEmpty()) {
                    autoCompleteRulesDetails.setText("Nenhuma regra selecionada");
                } else {
                    List<String> names = new ArrayList<>();
                    for (String ruleId : currentSelectedCommissionRuleIds) {
                        CommissionRule rule = dataCache.getCommissionRuleById(ruleId);
                        if (rule != null) {
                            names.add(rule.getName());
                        }
                    }
                    updatedRuleNames.append(String.join(", ", names));
                    autoCompleteRulesDetails.setText(updatedRuleNames.toString());
                }
            });
            builder.setNegativeButton("Cancelar", null);
            builder.show();
        });

        autoCompleteGoalsDetails.setOnClickListener(v -> {
            List<Goal> allGoals = dataCache.getAllGoals();
            CharSequence[] goalDescriptions = new CharSequence[allGoals.size()];
            boolean[] checkedGoals = new boolean[allGoals.size()];

            for (int i = 0; i < allGoals.size(); i++) {
                goalDescriptions[i] = allGoals.get(i).getDescription();
                checkedGoals[i] = currentSelectedGoalIds.contains(allGoals.get(i).getId());
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(ManageConsultantsActivity.this);
            builder.setTitle("Selecione as Metas");
            builder.setMultiChoiceItems(goalDescriptions, checkedGoals, (dialog, which, isChecked) -> {
                String goalId = allGoals.get(which).getId();
                if (isChecked) {
                    currentSelectedGoalIds.add(goalId);
                } else {
                    currentSelectedGoalIds.remove(goalId);
                }
            });

            builder.setPositiveButton("OK", (dialog, which) -> {
                StringBuilder updatedGoalDescriptions = new StringBuilder();
                if (currentSelectedGoalIds.isEmpty()) {
                    autoCompleteGoalsDetails.setText("Nenhuma meta selecionada");
                } else {
                    List<String> descriptions = new ArrayList<>();
                    for (String goalId : currentSelectedGoalIds) {
                        Goal goal = dataCache.getGoalById(goalId);
                        if (goal != null) {
                            descriptions.add(goal.getDescription());
                        }
                    }
                    updatedGoalDescriptions.append(String.join(", ", descriptions));
                    autoCompleteGoalsDetails.setText(updatedGoalDescriptions.toString());
                }
            });
            builder.setNegativeButton("Cancelar", null);
            builder.show();
        });


        expandArrow.setOnClickListener(v -> {
            if (detailsLayout.getVisibility() == View.GONE) {
                detailsLayout.setVisibility(View.VISIBLE);
                expandArrow.setImageResource(R.drawable.arrow_drop_up_icon);
                expandArrow.setContentDescription("Recolher detalhes do consultor");
            } else {
                detailsLayout.setVisibility(View.GONE);
                expandArrow.setImageResource(R.drawable.arrow_drop_down_icon);
                expandArrow.setContentDescription("Expandir detalhes do consultor");
            }
        });

        deleteIcon.setOnClickListener(v -> {
            new AlertDialog.Builder(ManageConsultantsActivity.this)
                    .setTitle("Confirmar Exclusão")
                    .setMessage("Tem certeza que deseja remover o consultor " + consultant.getName() + "?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        deleteConsultant(consultant.getId(), consultant.getName());
                    })
                    .setNegativeButton("Não", null)
                    .show();
        });

        buttonCancelUpdate.setOnClickListener(v -> {
            detailsLayout.setVisibility(View.GONE);
            expandArrow.setImageResource(R.drawable.arrow_drop_down_icon);
            expandArrow.setContentDescription("Expandir detalhes do consultor");
        });

        buttonSaveUpdate.setOnClickListener(v -> {
            String newName = consultantName.getText().toString().trim();
            String newEmail = consultantEmailEditText.getText().toString().trim();
            String newPassword = consultantPasswordEditText.getText().toString().trim();

            if (newName.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(ManageConsultantsActivity.this, "Nome, Email e Senha não podem ser vazios.", Toast.LENGTH_SHORT).show();
                return;
            }

            User updatedConsultant = new User(
                    consultant.getId(),
                    newName,
                    consultant.getSupervisorId(),
                    consultant.getConsultantIds(),
                    newEmail,
                    consultant.getPicture(),
                    newPassword,
                    consultant.getProfile()
            );

            // Passa os Sets para o metodo de atualização
            updateConsultant(updatedConsultant, currentSelectedGoalIds, currentSelectedCommissionRuleIds);
        });

        consultantsContainer.addView(consultorView);
    }


    // Tenta remover um consultor do servidor.
    private void deleteConsultant(String userId, String userName) {
        Toast.makeText(ManageConsultantsActivity.this, "Removendo consultor " + userName + " localmente...", Toast.LENGTH_SHORT).show();

        dataCache.removeUser(userId);
        Log.d(TAG, "Consultor " + userName + " (ID: " + userId + ") removido do DataCache.");

        List<Goal> allGoals = dataCache.getAllGoals();
        for (Goal goal : allGoals) {
            Set<String> assignedConsultantIds = goal.getAssignedConsultantIds();
            if (assignedConsultantIds != null && assignedConsultantIds.contains(userId)) {
                assignedConsultantIds.remove(userId);
                goal.setAssignedConsultantIds(assignedConsultantIds);
                dataCache.putGoal(goal);
                Log.d(TAG, "Consultor " + userId + " removido da Meta: " + goal.getDescription());
            }
        }

        List<CommissionRule> allRules = dataCache.getAllCommissionRules();
        for (CommissionRule rule : allRules) {
            Set<String> assignedConsultantIds = rule.getAssignedConsultantIds();
            if (assignedConsultantIds != null && assignedConsultantIds.contains(userId)) {
                assignedConsultantIds.remove(userId);
                rule.setAssignedConsultantIds(assignedConsultantIds);
                dataCache.putCommissionRule(rule);
                Log.d(TAG, "Consultor " + userId + " removido da Regra: " + rule.getName());
            }
        }

        updateSupervisorConsultantListOnDelete(userId);

        updateConsultantListFromCache();

        Retrofit retrofit = MockApiClient.getInstance();
        MockApiEndpoints apiService = retrofit.create(MockApiEndpoints.class);

        Call<Void> call = apiService.deleteUser(userId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Consultor " + userName + " (ID: " + userId + ") removido com sucesso no servidor.");
                } else {
                    Log.e(TAG, "Erro ao remover consultor do servidor (ID: " + userId + "). Código: " + response.code() + " - " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Corpo do erro do servidor: " + errorBody);
                            Toast.makeText(ManageConsultantsActivity.this, "Erro no servidor ao remover: " + response.message(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao ler errorBody na exclusão do servidor: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Falha na conexão ao tentar remover consultor do servidor (ID: " + userId + "): " + t.getMessage(), t);
                Toast.makeText(ManageConsultantsActivity.this, "Falha de conexão com o servidor ao remover.", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Atualiza a lista de consultantIds do supervisor atual no DataCache ao remover um consultor.
    private void updateSupervisorConsultantListOnDelete(String consultantIdToRemove) {
        User supervisor = dataCache.getUserById(SUPERVISOR_ID);
        if (supervisor != null && supervisor.getConsultantIds() != null) {
            List<String> currentConsultantIds = new ArrayList<>(supervisor.getConsultantIds());
            if (currentConsultantIds.remove(consultantIdToRemove)) {
                supervisor.setConsultantIds(currentConsultantIds);
                dataCache.putUser(supervisor);
                Log.d(TAG, "Supervisor " + SUPERVISOR_ID + " updated: removed consultant " + consultantIdToRemove);
            }
        }
    }


    // Alterna a visibilidade do formulário de adição de novo consultor.
    private void toggleAddConsultantForm() {
        if (addConsultantFormView == null || addConsultantFormView.getParent() == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            addConsultantFormView = inflater.inflate(R.layout.item_add_consultant_form, consultantsContainer, false);

            TextInputEditText editTextName = addConsultantFormView.findViewById(R.id.edit_text_name);
            TextInputEditText editTextEmail = addConsultantFormView.findViewById(R.id.edit_text_email);
            TextInputEditText editTextPassword = addConsultantFormView.findViewById(R.id.edit_text_password);
            ImageView closeAddFormIcon = addConsultantFormView.findViewById(R.id.close_add_form_icon);
            Button buttonCancel = addConsultantFormView.findViewById(R.id.button_cancel_add);
            Button buttonSave = addConsultantFormView.findViewById(R.id.button_save_new_consultant);

            AutoCompleteTextView autoCompleteRules = addConsultantFormView.findViewById(R.id.auto_complete_rules);
            AutoCompleteTextView autoCompleteGoals = addConsultantFormView.findViewById(R.id.auto_complete_goals);

            selectedGoalIds.clear();
            selectedCommissionRuleIds.clear();
            autoCompleteRules.setText("Selecione as regras");
            autoCompleteGoals.setText("Selecione as metas");


            autoCompleteRules.setOnClickListener(v -> {
                List<CommissionRule> allRules = dataCache.getAllCommissionRules();
                CharSequence[] ruleNames = new CharSequence[allRules.size()];
                boolean[] checkedRules = new boolean[allRules.size()];

                for (int i = 0; i < allRules.size(); i++) {
                    ruleNames[i] = allRules.get(i).getName();
                    checkedRules[i] = selectedCommissionRuleIds.contains(allRules.get(i).getId());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ManageConsultantsActivity.this);
                builder.setTitle("Selecione as Regras");
                builder.setMultiChoiceItems(ruleNames, checkedRules, (dialog, which, isChecked) -> {
                    String ruleId = allRules.get(which).getId();
                    if (isChecked) {
                        selectedCommissionRuleIds.add(ruleId);
                    } else {
                        selectedCommissionRuleIds.remove(ruleId);
                    }
                });

                builder.setPositiveButton("OK", (dialog, which) -> {
                    StringBuilder selectedRuleNames = new StringBuilder();
                    if (selectedCommissionRuleIds.isEmpty()) {
                        autoCompleteRules.setText("Nenhuma regra selecionada");
                    } else {
                        List<String> names = new ArrayList<>();
                        for (String ruleId : selectedCommissionRuleIds) {
                            CommissionRule rule = dataCache.getCommissionRuleById(ruleId);
                            if (rule != null) {
                                names.add(rule.getName());
                            }
                        }
                        selectedRuleNames.append(String.join(", ", names));
                        autoCompleteRules.setText(selectedRuleNames.toString());
                    }
                });
                builder.setNegativeButton("Cancelar", null);
                builder.show();
            });

            autoCompleteGoals.setOnClickListener(v -> {
                List<Goal> allGoals = dataCache.getAllGoals();
                CharSequence[] goalDescriptions = new CharSequence[allGoals.size()];
                boolean[] checkedGoals = new boolean[allGoals.size()];

                for (int i = 0; i < allGoals.size(); i++) {
                    goalDescriptions[i] = allGoals.get(i).getDescription();
                    checkedGoals[i] = selectedGoalIds.contains(allGoals.get(i).getId());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ManageConsultantsActivity.this);
                builder.setTitle("Selecione as Metas");
                builder.setMultiChoiceItems(goalDescriptions, checkedGoals, (dialog, which, isChecked) -> {
                    String goalId = allGoals.get(which).getId();
                    if (isChecked) {
                        selectedGoalIds.add(goalId);
                    } else {
                        selectedGoalIds.remove(goalId);
                    }
                });

                builder.setPositiveButton("OK", (dialog, which) -> {
                    StringBuilder selectedGoalDescriptions = new StringBuilder();
                    if (selectedGoalIds.isEmpty()) {
                        autoCompleteGoals.setText("Nenhuma meta selecionada");
                    } else {
                        List<String> descriptions = new ArrayList<>();
                        for (String goalId : selectedGoalIds) {
                            Goal goal = dataCache.getGoalById(goalId);
                            if (goal != null) {
                                descriptions.add(goal.getDescription());
                            }
                        }
                        selectedGoalDescriptions.append(String.join(", ", descriptions));
                        autoCompleteGoals.setText(selectedGoalDescriptions.toString());
                    }
                });
                builder.setNegativeButton("Cancelar", null);
                builder.show();
            });


            closeAddFormIcon.setOnClickListener(v -> hideAddConsultantForm());
            buttonCancel.setOnClickListener(v -> hideAddConsultantForm());

            buttonSave.setOnClickListener(v -> {
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(ManageConsultantsActivity.this, "Nome, Email e Senha do consultor não podem ser vazios.", Toast.LENGTH_SHORT).show();
                    return;
                }

                User newUser = new User(
                        null,
                        name,
                        SUPERVISOR_ID,
                        null,
                        email,
                        null,
                        password,
                        "Consultor"
                );

                addNewConsultant(newUser);
            });

            consultantsContainer.addView(addConsultantFormView, 0);
        } else {
            hideAddConsultantForm();
        }
    }

    // Envia uma requisição POST para o servidor para adicionar um novo consultor.
    private void addNewConsultant(User newUser) {
        Retrofit retrofit = MockApiClient.getInstance();
        MockApiEndpoints apiService = retrofit.create(MockApiEndpoints.class);

        Call<User> call = apiService.createUser(newUser);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User createdUser = response.body();
                    Log.d(TAG, "Consultor " + createdUser.getName() + " (ID: " + createdUser.getId() + ") adicionado com sucesso.");
                    Toast.makeText(ManageConsultantsActivity.this, "Consultor " + createdUser.getName() + " adicionado.", Toast.LENGTH_SHORT).show();

                    dataCache.putUser(createdUser);

                    for (String goalId : selectedGoalIds) {
                        Goal goal = dataCache.getGoalById(goalId);
                        if (goal != null) {
                            Set<String> assignedConsultantIds = goal.getAssignedConsultantIds();
                            if (assignedConsultantIds == null) {
                                assignedConsultantIds = new HashSet<>();
                            }
                            assignedConsultantIds.add(createdUser.getId());
                            goal.setAssignedConsultantIds(assignedConsultantIds);
                            dataCache.putGoal(goal);
                            Log.d(TAG, "Meta '" + goal.getDescription() + "' atualizada com o ID do novo consultor: " + createdUser.getId());
                        }
                    }

                    for (String ruleId : selectedCommissionRuleIds) {
                        CommissionRule rule = dataCache.getCommissionRuleById(ruleId);
                        if (rule != null) {
                            Set<String> assignedConsultantIds = rule.getAssignedConsultantIds();
                            if (assignedConsultantIds == null) {
                                assignedConsultantIds = new HashSet<>();
                            }
                            assignedConsultantIds.add(createdUser.getId());
                            rule.setAssignedConsultantIds(assignedConsultantIds);
                            dataCache.putCommissionRule(rule);
                            Log.d(TAG, "Regra de Comissão '" + rule.getName() + "' atualizada com o ID do novo consultor: " + createdUser.getId());
                        }
                    }

                    updateSupervisorConsultantListOnAdd(createdUser.getId());

                    hideAddConsultantForm();
                    updateConsultantListFromCache();
                } else {
                    Log.e(TAG, "Erro ao adicionar consultor. Código: " + response.code() + " - " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Corpo do erro: " + errorBody);
                            Toast.makeText(ManageConsultantsActivity.this, "Erro ao adicionar consultor. " + errorBody, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao ler errorBody na adição: " + e.getMessage());
                        Toast.makeText(ManageConsultantsActivity.this, "Erro ao adicionar consultor. Verifique o log.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Falha na conexão ao tentar adicionar consultor: " + t.getMessage(), t);
                Toast.makeText(ManageConsultantsActivity.this, "Falha na conexão ao adicionar consultor.", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Atualiza a lista de consultantIds do supervisor atual no DataCache ao adicionar um novo consultor.
    private void updateSupervisorConsultantListOnAdd(String newConsultantId) {
        User supervisor = dataCache.getUserById(SUPERVISOR_ID);
        if (supervisor != null) {
            List<String> currentConsultantIds = supervisor.getConsultantIds();
            if (currentConsultantIds == null) {
                currentConsultantIds = new ArrayList<>();
            }
            if (!currentConsultantIds.contains(newConsultantId)) {
                currentConsultantIds.add(newConsultantId);
                supervisor.setConsultantIds(currentConsultantIds);
                dataCache.putUser(supervisor);
                Log.d(TAG, "Supervisor " + SUPERVISOR_ID + " updated: added new consultant " + newConsultantId);
            }
        }
    }

    // Atualiza um consultor existente no DataCache e, se houver, no servidor.
    private void updateConsultant(User updatedConsultant, Set<String> newSelectedGoalIds, Set<String> newSelectedCommissionRuleIds) {
        String consultantId = updatedConsultant.getId();

        // Atualizar o User no DataCache (dados básicos como nome, email, senha)
        dataCache.putUser(updatedConsultant);
        Log.d(TAG, "Consultor " + updatedConsultant.getName() + " (ID: " + consultantId + ") atualizado no DataCache.");

        // Lógica para atualizar associações de Goals
        List<Goal> allGoals = dataCache.getAllGoals();
        for (Goal goal : allGoals) {
            Set<String> assignedToGoal = (goal.getAssignedConsultantIds() != null) ?
                    new HashSet<>(goal.getAssignedConsultantIds()) : new HashSet<>();

            boolean wasAssigned = assignedToGoal.contains(consultantId);
            boolean nowAssigned = newSelectedGoalIds.contains(goal.getId());

            if (nowAssigned && !wasAssigned) {
                // Adicionar o consultor a esta Goal
                assignedToGoal.add(consultantId);
                goal.setAssignedConsultantIds(assignedToGoal);
                dataCache.putGoal(goal);
                Log.d(TAG, "Consultor " + consultantId + " adicionado à Meta: " + goal.getDescription());
            } else if (!nowAssigned && wasAssigned) {
                // Remover o consultor desta Goal
                assignedToGoal.remove(consultantId);
                goal.setAssignedConsultantIds(assignedToGoal);
                dataCache.putGoal(goal);
                Log.d(TAG, "Consultor " + consultantId + " removido da Meta: " + goal.getDescription());
            }
        }

        // Lógica para atualizar associações de CommissionRules
        List<CommissionRule> allRules = dataCache.getAllCommissionRules();
        for (CommissionRule rule : allRules) {
            // Crie um novo HashSet mutável a partir do Set existente
            Set<String> assignedToRule = (rule.getAssignedConsultantIds() != null) ?
                    new HashSet<>(rule.getAssignedConsultantIds()) : new HashSet<>();

            boolean wasAssigned = assignedToRule.contains(consultantId);
            boolean nowAssigned = newSelectedCommissionRuleIds.contains(rule.getId());

            if (nowAssigned && !wasAssigned) {
                assignedToRule.add(consultantId);
                rule.setAssignedConsultantIds(assignedToRule);
                dataCache.putCommissionRule(rule);
                Log.d(TAG, "Consultor " + consultantId + " adicionado à Regra: " + rule.getName());
            } else if (!nowAssigned && wasAssigned) {
                assignedToRule.remove(consultantId);
                rule.setAssignedConsultantIds(assignedToRule);
                dataCache.putCommissionRule(rule);
                Log.d(TAG, "Consultor " + consultantId + " removido da Regra: " + rule.getName());
            }
        }

        // Recarregar a UI a partir do DataCache para refletir as mudanças
        updateConsultantListFromCache();
        Toast.makeText(ManageConsultantsActivity.this, "Consultor " + updatedConsultant.getName() + " atualizado localmente.", Toast.LENGTH_SHORT).show();

        Log.i(TAG, "Chamada de atualização para o servidor seria feita aqui, se disponível na API.");
    }

    private void hideAddConsultantForm() {
        if (addConsultantFormView != null && addConsultantFormView.getParent() != null) {
            consultantsContainer.removeView(addConsultantFormView);
            addConsultantFormView = null;
        }
    }

}