package com.example.cinemas;

public class Combo {
    private String name;
    private int price;
    private int imageResId;
    public Combo(String name, int price, int imageResId){
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }
    @Override
    public String toString(){
        return name;
    }
}
