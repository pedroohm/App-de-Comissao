package com.grupo6.appdecomissao.remote;

import com.grupo6.appdecomissao.domain.Goal;
import com.grupo6.appdecomissao.domain.CommissionRule;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.grupo6.appdecomissao.domain.Record;
import com.grupo6.appdecomissao.domain.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log; // <-- Add this line


public class ApiRepository {

    // Instância única do retrofit
    private final RubeusEndpointsAPI service;

    private final MockApiEndpoints mockService;
    // Corpo de cada requisição
    private final Map<String, String> body;

    public ApiRepository() {
        service = ApiClient
                .getInstance()
                .create(RubeusEndpointsAPI.class);

        mockService = MockApiClient
                .getInstance()
                .create(MockApiEndpoints.class);

        body = new HashMap<>();
    }

    public void getAllUsers(String origin, String token, ApiCallback<List<User>> callback) {
        body.put("origem", origin);
        body.put("token", token);

        Call<JsonObject> call = service.listUsers(body);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Erro HTTP: " + response.code());
                    return;
                }

                JsonObject json = response.body();
                if (!json.has("success") || !json.get("success").getAsBoolean()) {
                    callback.onError("API retornou success=false");
                    return;
                }

                if (!json.has("dados") || !json.get("dados").isJsonArray()) {
                    callback.onError("Dados ausentes ou inválidos");
                    return;
                }

                JsonArray dataArray = json.getAsJsonArray("dados");
                List<User> userList = new ArrayList<>();

                for (int i = 0; i < dataArray.size(); i++) {
                    JsonObject userJson = dataArray.get(i).getAsJsonObject();
                    User user = new Gson().fromJson(userJson, User.class);
                    userList.add(user);
                }

                callback.onSuccess(userList);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onError("Erro de rede: " + t.getMessage());
            }
        });
    }

    public void getRecordsByProcessAndStage(String origin, String token, String processId, String finalStage, String currentId, ApiCallback<List<Record>> callback) {
        try {
            /*
            // Validação do status informado
            if (targetStatus == null || (!targetStatus.equalsIgnoreCase("Em andamento")
                    && !targetStatus.equalsIgnoreCase("Ganho")
                    && !targetStatus.equalsIgnoreCase("Perdido"))) {
                callback.onError("Status inválido");
                return;
            }
            */

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("origem", Integer.parseInt(origin));
            requestBody.addProperty("token", token);
            requestBody.addProperty("processo", Integer.parseInt(processId));

            Log.d("API_REQUEST", "Origem na requisição: " + origin);
            Log.d("API_REQUEST", "Token na requisição: " + token);
            Log.d("API_REQUEST", "Processo na requisição: " + processId);

            Log.d("API_REQUEST", "Enviando requisição para listRecords no processo: " + processId);

            Call<JsonObject> call = service.listRecords(requestBody);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        if (!response.isSuccessful() || response.body() == null) {
                            String error = "Erro HTTP: " + response.code();
                            if (response.errorBody() != null) {
                                error += " - " + response.errorBody().string();
                            }
                            Log.e("API_ERROR", error);
                            callback.onError(error);
                            return;
                        }

                        JsonObject jsonResponse = response.body();

                        if (!jsonResponse.has("success") || !jsonResponse.get("success").getAsBoolean()) {
                            String errorMsg = jsonResponse.has("errors") ?
                                    jsonResponse.get("errors").getAsString() : "API retornou success=false";
                            callback.onError(errorMsg);
                            return;
                        }

                        if (!jsonResponse.has("dados") || !jsonResponse.get("dados").isJsonObject()) {
                            callback.onError("Campo 'dados' ausente ou inválido na resposta");
                            return;
                        }

                        // Pega o objeto "dados" principal
                        JsonObject dadosObj = jsonResponse.getAsJsonObject("dados");

                        // Verifica se existe o array "dados" dentro do objeto "dados"
                        if (!dadosObj.has("dados") || !dadosObj.get("dados").isJsonArray()) {
                            callback.onError("Array 'dados' ausente ou inválido dentro do objeto 'dados'");
                            return;
                        }

                        JsonArray recordsArray = dadosObj.getAsJsonArray("dados");
                        List<Record> records = new ArrayList<>();

                        // Itera pelo array e filtra pelo statusNome informado
                        for (JsonElement element : recordsArray) {
                            JsonObject recordJson = element.getAsJsonObject();

                            Log.d("API_RESPONSE", "Resposta da API: " + recordJson.toString());

                            if ((recordJson.has("etapaNome") &&
                                    recordJson.get("etapaNome").getAsString().equalsIgnoreCase(finalStage)) &&
                                    (recordJson.has("responsavel") &&
                                            recordJson.get("responsavel").getAsString().equalsIgnoreCase(currentId))
                            ) {

                                Record record = new Gson().fromJson(recordJson, Record.class);
                                records.add(record);
                            }
                        }

                        Log.d("API_SUCCESS", "Registros encontrados: " + records.size());
                        callback.onSuccess(records);

                    } catch (Exception e) {
                        Log.e("API_PROCESSING", "Erro ao processar resposta", e);
                        callback.onError("Erro no processamento: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("API_FAILURE", "Falha na requisição", t);
                    callback.onError("Falha na rede: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e("API_EXCEPTION", "Erro na chamada da API", e);
            callback.onError("Erro interno: " + e.getMessage());
        }
    }

    public void getCommissionRules(ApiCallback<List<CommissionRule>> callback) {
        Call<List<CommissionRule>> call = mockService.getCommissionRules();
        call.enqueue(new Callback<List<CommissionRule>>() {
            @Override
            public void onResponse(Call<List<CommissionRule>> call, Response<List<CommissionRule>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Erro ao buscar regras de comissão: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<CommissionRule>> call, Throwable t) {
                callback.onError("Erro de rede: " + t.getMessage());
            }
        });
    }

    public void getGoals(ApiCallback<List<Goal>> callback) {
        Call<List<Goal>> call = mockService.getGoals();
        call.enqueue(new Callback<List<Goal>>() {
            @Override
            public void onResponse(Call<List<Goal>> call, Response<List<Goal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Erro ao buscar metas: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Goal>> call, Throwable t) {
                callback.onError("Falha na rede ao buscar metas: " + t.getMessage());
            }
        });
    }

    // --- NOVOS MÉTODOS PARA API PHP LOCAL ---
    public void getAllCommissionRules(ApiCallback<List<CommissionRule>> callback) {
        Call<List<CommissionRule>> call = mockService.getCommissionRules();
        call.enqueue(new Callback<List<CommissionRule>>() {
            @Override
            public void onResponse(Call<List<CommissionRule>> call, Response<List<CommissionRule>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Erro ao buscar regras de comissão: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<CommissionRule>> call, Throwable t) {
                callback.onError("Erro de rede: " + t.getMessage());
            }
        });
    }

    public void createCommissionRule(CommissionRule rule, ApiCallback<CommissionRule> callback) {
        Call<CommissionRule> call = mockService.createCommissionRule(rule);
        call.enqueue(new Callback<CommissionRule>() {
            @Override
            public void onResponse(Call<CommissionRule> call, Response<CommissionRule> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Erro ao cadastrar regra: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<CommissionRule> call, Throwable t) {
                callback.onError("Erro de rede: " + t.getMessage());
            }
        });
    }

    public void getAllUsers(ApiCallback<List<User>> callback) {
        Call<List<User>> call = mockService.getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Erro ao buscar usuários: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onError("Erro de rede: " + t.getMessage());
            }
        });
    }

    public void createUser(User user, ApiCallback<User> callback) {
        Call<User> call = mockService.createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Erro ao cadastrar usuário: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError("Erro de rede: " + t.getMessage());
            }
        });
    }

    public void getAllGoals(ApiCallback<List<Goal>> callback) {
        Call<List<Goal>> call = mockService.getGoals();
        call.enqueue(new Callback<List<Goal>>() {
            @Override
            public void onResponse(Call<List<Goal>> call, Response<List<Goal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Erro ao buscar metas: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Goal>> call, Throwable t) {
                callback.onError("Erro de rede: " + t.getMessage());
            }
        });
    }

    public void createGoal(Goal goal, ApiCallback<Goal> callback) {
        Call<Goal> call = mockService.createGoal(goal);
        call.enqueue(new Callback<Goal>() {
            @Override
            public void onResponse(Call<Goal> call, Response<Goal> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Erro ao cadastrar meta: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Goal> call, Throwable t) {
                callback.onError("Erro de rede: " + t.getMessage());
            }
        });
    }

}