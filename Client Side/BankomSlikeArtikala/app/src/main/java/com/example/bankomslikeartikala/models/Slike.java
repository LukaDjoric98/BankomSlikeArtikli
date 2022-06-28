package com.example.bankomslikeartikala.models;

public class Slike {

    private Integer id;
    private String put;
    private String naziv;
    private Integer artikalId;

    public Slike(Integer id, String put, String naziv, Integer artikalId) {
        this.id = id;
        this.put = put;
        this.naziv = naziv;
        this.artikalId = artikalId;
    }

    public Integer getId() {
        return id;
    }

    public String getNaziv() {
        return naziv;
    }

    public Integer getArtikalId() {
        return artikalId;
    }
}
