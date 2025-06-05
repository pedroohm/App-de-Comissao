package com.grupo6.appdecomissao;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {

    // private User firstUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // O CÓDIGO COMENTADO ABAIXO FAZ REQUISIÇÃO PARA A API DA RUBEUS E PEGA O PRIMEIRO USUÁRIO DA RESPOSTA DA API
        // ESTÁ AQUI APENAS PARA TESTE
        /*
        RubeusEndpointsAPI service = ApiClient
                .getInstance()
                .create(RubeusEndpointsAPI.class);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("origem", "PEGUE A ORIGEM NO SITE");
        requestBody.put("token", "PEGUE O TOKEN NO SITE");

        Call<JsonObject> call = service.listUsers(requestBody);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // 5) Verificar se veio 200 OK e se o body não é nulo
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject json = response.body();

                    // 6) Verificar o campo "success"
                    boolean success = false;
                    if (json.has("success") && !json.get("success").isJsonNull()) {
                        success = json.get("success").getAsBoolean();
                    }

                    if (!success) {
                        Log.e("API", "success=false na resposta");
                        return;
                    }

                    // 7) Pegar o array "dados"
                    if (json.has("dados") && json.get("dados").isJsonArray()) {
                        JsonArray dadosArray = json.getAsJsonArray("dados");

                        // 8) Checar se não está vazio
                        if (dadosArray.size() > 0) {
                            // 9) Pegar o primeiro JsonObject dentro de "dados"
                            JsonObject firstJsonUser = dadosArray.get(0).getAsJsonObject();

                            // 10) Converter esse JsonObject para User
                            //     (User tem @SerializedName para "nome")
                            firstUser = new Gson().fromJson(firstJsonUser, User.class);

                            // 11) Testar: exibir no log
                            Log.d("API", "Primeiro usuário recebido:");
                            Log.d("API", "ID: " + firstUser.getId());
                            Log.d("API", "Name: " + firstUser.getName());
                            Log.d("API", "Email: " + firstUser.getEmail());
                        } else {
                            Log.e("API", "Array 'dados' estava vazio");
                        }
                    } else {
                        Log.e("API", "Não achei 'dados' como JsonArray");
                    }
                } else {
                    // Resposta não foi 200 ou corpo nulo
                    Log.e("API", "Resposta não bem-sucedida. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Erro de rede ou falha no parsing
                Log.e("API", "Falha na chamada Retrofit: " + t.getMessage());
            }

         */
    }
}