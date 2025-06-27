package com.grupo6.appdecomissao.remote;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.grupo6.appdecomissao.domain.CommissionRule;
import com.grupo6.appdecomissao.domain.Goal;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MockApiEndpoints {

    @GET("api/commission-rules")
    Call<List<CommissionRule>> getCommissionRules();

    @GET("api/goals")
    Call<List<Goal>> getGoals();

    @GET("api/users")
    Call<List<com.grupo6.appdecomissao.domain.User>> getUsers();

    @POST("api/commission-rules")
    Call<CommissionRule> createCommissionRule(@Body CommissionRule rule);

    @POST("api/users")
    Call<com.grupo6.appdecomissao.domain.User> createUser(@Body com.grupo6.appdecomissao.domain.User user);

    @POST("api/goals")
    Call<Goal> createGoal(@Body Goal goal);

}
