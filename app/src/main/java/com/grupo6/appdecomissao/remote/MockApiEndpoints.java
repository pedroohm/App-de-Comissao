package com.grupo6.appdecomissao.remote;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.grupo6.appdecomissao.domain.CommissionRule;
import com.grupo6.appdecomissao.domain.Goal;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MockApiEndpoints {

    @GET("api/commission-rules")
    Call<List<CommissionRule>> getCommissionRules();

    @GET("api/goals")
    Call<List<Goal>> getGoals();

}
