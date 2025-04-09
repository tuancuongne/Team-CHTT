package com.example.cinemas;

public class SelectedCombo implements java.io.Serializable {
    private String name;
    private int price;
    private int quantity;
    private int imageResId;

    public SelectedCombo(String name, int price, int quantity, int imageResId) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getImageResId() {
        return imageResId;
    }
}