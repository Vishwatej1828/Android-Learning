package com.example.listviewapp;

public class Country {
    private String name;
    private String capital;
    private int flagResource;

    public Country(String name, String capital, int flagResource) {
        this.name = name;
        this.capital = capital;
        this.flagResource = flagResource;
    }

    public String getName() {
        return name;
    }

    public String getCapital() {
        return capital;
    }

    public int getImageResource() {
        return flagResource;
    }
}
