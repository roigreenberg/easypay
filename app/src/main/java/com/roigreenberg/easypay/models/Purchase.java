package com.roigreenberg.easypay.models;

/**
 * Created by Roi on 16/09/2017.
 */

public class Purchase {
    String title;
    String cost;

    public Purchase() {
    }

    public Purchase(String title, String cost) {
        this.title = title;
        this.cost = cost;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
