package com.example.bankomslikeartikala.models;

import com.google.gson.annotations.SerializedName;


public class User {
    @SerializedName("id")
    private int Id;
    @SerializedName("username")
    private String Username;
    @SerializedName("password")
    private String Password;
    @SerializedName("salt")
    private String Salt;
    @SerializedName("rola")
    private String Rola;

    public User(String username, String password) {
        Username = username;
        Password = password;
    }

    public User(String username){
        Username = username;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getRola() {
        return Rola;
    }

    public void setRola(String rola) {
        Rola = rola;
    }

    @Override
    public String toString() {
        return Username;
    }
}
