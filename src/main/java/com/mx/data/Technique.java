package com.mx.data;

import java.io.Serializable;

public class Technique implements Serializable {
    private static final long serialVersionUID = 2L;
    private int techID;
    private String typeName;
    private String brand;
    private String model;
    private int releaseYear;
    private int quantityInStock;

    public Technique(int techID, String typeName, String brand, String model, int releaseYear, int quantityInStock) {
        this.techID = techID;
        this.typeName = typeName;
        this.brand = brand;
        this.model = model;
        this.releaseYear = releaseYear;
        this.quantityInStock = quantityInStock;
    }

    public Technique(int techID, String brand, String model) {
        this.techID = techID;
        this.brand = brand;
        this.model = model;
    }

    public Technique(String brand, String model) {
        this.brand = brand;
        this.model = model;
    }

    public Technique(int techID, int quantityInStock) {
        this.techID = techID;
        this.quantityInStock = quantityInStock;
    }

    public Technique(String typeName, String brand, String model) {
        this.typeName = typeName;
        this.brand = brand;
        this.model = model;
    }

    public Technique(String typeName, String brand, String model, int quantityInStock) {
        this.typeName = typeName;
        this.brand = brand;
        this.model = model;
        this.quantityInStock = quantityInStock;
    }

    public int getTechID() {
        return techID;
    }

    public void setTechID(int techID) {
        this.techID = techID;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }
}
