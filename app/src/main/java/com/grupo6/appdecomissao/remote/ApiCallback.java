package com.grupo6.appdecomissao.remote;

/*
    Essa é uma interface genérica responsável para lidar com as respostas da API, devolvendo os resultados
    para o metodo que a chamou
*/
public interface ApiCallback<T> {
    void onSuccess(T result);
    void onError(String errorMessage);
}