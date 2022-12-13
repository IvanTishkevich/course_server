package com.mx.data;

import java.io.Serializable;
import java.sql.Date;

public class Selling implements Serializable {
    private static final long serialVersionUID = 2L;
    private int sellingID;
    private int salePrice;
    private int quantitySold;
    private Date saleDate;
    private Technique technique;

    public Selling(int sellingID, int salePrice, int quantitySold, Date saleDate, Technique technique) {
        this.sellingID = sellingID;
        this.salePrice = salePrice;
        this.quantitySold = quantitySold;
        this.saleDate = saleDate;
        this.technique = technique;
    }

    public Selling(int salePrice, int quantitySold, Date saleDate, Technique technique) {
        this.salePrice = salePrice;
        this.quantitySold = quantitySold;
        this.saleDate = saleDate;
        this.technique = technique;
    }

    public Selling(int quantitySold, Technique technique) {
        this.quantitySold = quantitySold;
        this.technique = technique;
    }

    public Technique getTechnique() {
        return technique;
    }

    public void setTechnique(Technique technique) {
        this.technique = technique;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public int getSellingID() {
        return sellingID;
    }

    public void setSellingID(int sellingID) {
        this.sellingID = sellingID;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }
}
