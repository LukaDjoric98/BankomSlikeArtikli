package com.example.bankomslikeartikala.models;

public class Artikli {
    private Integer id;
    private String naziv;

    public Artikli(String naziv) {
        this.naziv = naziv;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return naziv;
    }
}
