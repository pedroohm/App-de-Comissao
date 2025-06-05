package com.grupo6.appdecomissao;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import com.google.gson.JsonObject;
import java.util.Map;

public interface RubeusEndpointsAPI {

    @Headers("Content-type: application/json")
    @POST("api/Usuario/listarUsuarios")
    Call<JsonObject> listUsers(@Body Map<String, String> body);
}
