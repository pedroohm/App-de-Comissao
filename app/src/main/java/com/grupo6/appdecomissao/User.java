package com.grupo6.appdecomissao;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private Integer id;

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

    public User(Integer id, String name, String email, String picture, String password, String profile) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.password = password;
        this.profile = profile;
    }

    // Métodos de Get
    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPicture() { return picture; }
    public String getPassword() { return password; }
    public String getProfile() { return profile; }

    // Métodos de Set para os atributos que serão mutáveis
    public void setName(String name) { this.name = name; }
    public void setPicture(String picture) { this.picture = picture; }
}
