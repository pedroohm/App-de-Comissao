package com.grupo6.appdecomissao.remote;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.grupo6.appdecomissao.domain.CommissionRule;
import com.grupo6.appdecomissao.domain.Goal;
import com.grupo6.appdecomissao.domain.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MockApiEndpoints {

    @GET("api/users") // Endpoint para buscar todos os usuários do mock
    Call<List<User>> getUsers();

    @GET("api/commission-rules")
    Call<List<CommissionRule>> getCommissionRules();

    @GET("api/goals")
    Call<List<Goal>> getGoals();

    @DELETE("api/users/{id}")
    Call<Void> deleteUser(@Path("id") String userId);

    @POST("api/users")
    Call<User> createUser(@Body User newUser);

    @PUT("api/users/{id}")
    Call<User> updateUser(@Path("id") String userId, @Body User updatedUser);

    @POST("api/commission-rules")
    Call<CommissionRule> createCommissionRule(@Body CommissionRule rule);

    @POST("api/goals")
    Call<Goal> createGoal(@Body Goal goal);

}