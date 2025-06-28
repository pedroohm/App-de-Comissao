package com.grupo6.appdecomissao.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {
    @SerializedName("id")
    private String id;

    @SerializedName("nome")
    private String name;

    @SerializedName("email")
    private String email;

    // Possíveis atributos (picture e password) que deverão ser armazenados em um BD
    // Independem da API
    private String picture; // É o caminho relativo da imagem
    private String password; // É um hash

    // Atributo usado para identificar se o usuário é um consultor ou um supervisor
    private String profile;

    // Se for um consultor, guarda o id do seu supervisor
    private String supervisorId;

    // Se for um supervisor, guarda uma lista com os ids dos seus consultores
    private List<String> consultantIds;

    public User(String id, String name, String supervisorId, List<String> consultantIds, String email, String picture, String password, String profile) {
        this.id = id;
        this.name = name;
        this.supervisorId = supervisorId;
        this.consultantIds = consultantIds;
        this.email = email;
        this.picture = picture;
        this.password = password;
        this.profile = profile;
    }

    // Métodos de Get
    public String getId() { return id; }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPicture() { return picture; }
    public String getPassword() { return password; }
    public String getProfile() { return profile; }
    public String getSupervisorId() { return supervisorId; }
    public List<String> getConsultantIds() { return consultantIds; }

    // Métodos de Set para os atributos que serão mutáveis
    public void setName(String name) { this.name = name; }
    public void setPicture(String picture) { this.picture = picture; }
    public void setConsultantIds(List<String> consultantIds) { this.consultantIds = consultantIds; }
}