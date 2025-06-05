package com.grupo6.appdecomissao;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
    Essa classe é responsável por esconder toda a lógica pesada de requisição para uma API.
    Aqui, implementaremos todos os métodos necessários para a nossa aplicação que envolvam a requisição de dados
    na plataforma Rubeus.
 */

public class ApiRepository {

    // Instância única do retrofit
    private final RubeusEndpointsAPI service;
    // Corpo de cada requisição
    private Map<String, String> body;

    public ApiRepository() {
        service = ApiClient
                .getInstance()
                .create(RubeusEndpointsAPI.class);

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
                    callback.onError("API retornou sucess=false");
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
}
