package com.grupo6.appdecomissao;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Classes com os métodos para requisição de dados
        ApiRepository apiRepository = new ApiRepository();

        String origin = "8";
        String token = "b116d29f1252d2ce144d5cb15fb14c7f";

        // Chamando o metodo para listar todos os usuários da plataforma
        apiRepository.getAllUsers(origin, token, new ApiCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                // Utilizar runOnUiThread sempre que for atualizar ALGO NA TELA!!
                // Nesse caso não precisa porque só estamos usando log
                // Mas se estivessemos colocando esses dados em uma activity, por exemplo, seria necessário
                // Deixei o metodo aqui para saberem que é necessário em alguns momentos
                runOnUiThread(() -> {
                    for (User user : users) {
                        Log.d("MainActivity", "ID: " + user.getId());
                        Log.d("MainActivity", "Nome: " + user.getName());
                        Log.d("MainActivity", "Email: " + user.getEmail());
                        Log.d("MainActivity", "-----------------------------------");
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Log.e("MainActivity", "Erro: " + errorMessage);
                });
            }
        });
    }
}