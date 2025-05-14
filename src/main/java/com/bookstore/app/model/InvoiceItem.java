package com.bookstore.app.model;

public class InvoiceItem {
    private int invoiceId;
    private int bookId;
    private int quantity;
    private double unitPrice;

    public InvoiceItem() {
    }

    public InvoiceItem(int invoiceId, int bookId, int quantity, double unitPrice) {
        this.invoiceId = invoiceId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "InvoiceItem{" +
                "invoiceId=" + invoiceId +
                ", bookId=" + bookId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
