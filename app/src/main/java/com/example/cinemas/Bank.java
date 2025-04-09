package com.example.cinemas;

public class Bank {
    private String name;
    private int logo;

    public Bank(String name, int logo) {
        this.name = name;
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public int getLogo() {
        return logo;
    }
}