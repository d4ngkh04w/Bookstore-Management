package com.bookstore.app.model;

import java.util.Date;

public class Invoice {
    private int id;
    private int customerId;
    private Date date;
    private double totalAmount;

    public Invoice() {
    }

    public Invoice(int id, int customerId, Date date, double totalAmount) {
        this.id = id;
        this.customerId = customerId;
        this.date = date;
        this.totalAmount = totalAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Date getDate() {
        return date;
    }    public void setDate(Date date) {
        this.date = date;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", date=" + date +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
