package com.bookstore.app.controller;

import com.bookstore.app.model.Invoice;
import com.bookstore.app.model.InvoiceItem;
import com.bookstore.app.service.InvoiceService;

import java.util.List;

public class InvoiceController {
    private final InvoiceService invoiceService;
    
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }
    
    public List<Invoice> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    public List<InvoiceItem> getInvoiceItemsByInvoiceId(int invoiceId) {
        return invoiceService.getInvoiceItemsByInvoiceId(invoiceId);
    }
    
    public int createInvoice(Invoice invoice, List<InvoiceItem> items) {
        return invoiceService.createInvoice(invoice, items);
    }
}
