package com.mx.data;

import java.io.Serializable;
import java.sql.Date;


public class Supplies implements Serializable {
    private static final long serialVersionUID = 2L;
    private int supplyID;
    private Date supplyDate;
    private int purchasePrice;
    private int quantitySupplied;
    private String status="Ожидается";
    private Technique technique;
    private Providers provider;

    public Supplies(int supplyID, Date supplyDate, int purchasePrice, int quantitySupplied,String status,Technique technique, Providers provider ) {
        this.supplyID = supplyID;
        this.supplyDate = supplyDate;
        this.purchasePrice = purchasePrice;
        this.quantitySupplied = quantitySupplied;
        this.status = status;
        this.technique = technique;
        this.provider = provider;
    }

    public Supplies(int supplyID, Date supplyDate, int purchasePrice, int quantitySupplied) {
        this.supplyID = supplyID;
        this.supplyDate = supplyDate;
        this.purchasePrice = purchasePrice;
        this.quantitySupplied = quantitySupplied;
    }



    public Technique getTechnique() {
        return technique;
    }

    public void setTechnique(Technique technique) {
        this.technique = technique;
    }

    public Providers getProvider() {
        return provider;
    }

    public void setProvider(Providers provider) {
        this.provider = provider;
    }

    public int getSupplyID() {
        return supplyID;
    }

    public void setSupplyID(int supplyID) {
        this.supplyID = supplyID;
    }

    public Date getSupplyDate() {
        return supplyDate;
    }

    public void setSupplyDate(Date supplyDate) {
        this.supplyDate = supplyDate;
    }

    public int getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(int purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public int getQuantitySupplied() {
        return quantitySupplied;
    }

    public void setQuantitySupplied(int quantitySupplied) {
        this.quantitySupplied = quantitySupplied;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
