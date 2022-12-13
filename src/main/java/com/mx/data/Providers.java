package com.mx.data;

import java.io.Serializable;
//класс поставщика
public class Providers implements Serializable {
    private static final long serialVersionUID = 2L;
    private int providerID;
    private String name;
    private String address;
    private String telephone;
    private int sumSupply;

    public Providers(int providerID, String name, String address, String telephone) {
        this.providerID = providerID;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
    }

    public Providers(int providerID, String name) {
        this.providerID = providerID;
        this.name = name;
    }

    public Providers(String name, int sumSupply) {
        this.name = name;
        this.sumSupply = sumSupply;
    }

    public int getProviderID() {
        return providerID;
    }

    public void setProviderID(int providerID) {
        this.providerID = providerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
