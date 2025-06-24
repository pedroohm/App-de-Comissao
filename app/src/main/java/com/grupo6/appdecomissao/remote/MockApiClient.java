package com.grupo6.appdecomissao.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MockApiClient {
    // IMPORTANTE: Do emulador Android, 'localhost' é acessado pelo IP 10.0.2.2
    private static final String BASE_URL = "http://[ADICIONE O SEU IP]/api-comissao/";

    private static Retrofit retrofit = null;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}