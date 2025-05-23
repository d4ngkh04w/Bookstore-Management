package com.bookstore.app.service;

import com.bookstore.app.dao.BookDAO;
import com.bookstore.app.dao.InvoiceDAO;
import com.bookstore.app.model.Book;
import com.bookstore.app.model.Invoice;
import com.bookstore.app.model.InvoiceItem;

import java.util.List;

public class InvoiceService {
    private static InvoiceService instance;
    private final InvoiceDAO invoiceDAO;
    private final BookDAO bookDAO;
    
    public InvoiceService(InvoiceDAO invoiceDAO, BookDAO bookDAO) {
        this.invoiceDAO = invoiceDAO;
        this.bookDAO = bookDAO;
    }
    
    public static InvoiceService getInstance() {
        if (instance == null) {
            instance = new InvoiceService(new InvoiceDAO(), new BookDAO());
        }
        return instance;
    }
    
    public List<Invoice> getAllInvoices() {
        return invoiceDAO.getAllInvoices();
    }
    
    public Invoice getInvoiceById(int id) {
        return invoiceDAO.getInvoiceById(id);
    }

    public List<InvoiceItem> getInvoiceItems(int invoiceId) {
        return invoiceDAO.getInvoiceItems(invoiceId);
    }
    
    public List<InvoiceItem> getInvoiceItemsByInvoiceId(int invoiceId) {
        return invoiceDAO.getInvoiceItems(invoiceId);
    }

    public int createInvoice(Invoice invoice, List<InvoiceItem> items) {
        if (items == null || items.isEmpty()) {
            return -1;
        }

        for (InvoiceItem item : items) {
            Book book = null;
            
            try {
                book = bookDAO.getBookById(item.getBookId());
                  if (book == null || book.getQuantity() < item.getQuantity()) {
                    return -1;
                }

                book.setQuantity(book.getQuantity() - item.getQuantity());
                bookDAO.updateBook(book);
                
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return -1;
            }
        }

        int newInvoiceId = invoiceDAO.addInvoice(invoice, items);
        if (newInvoiceId > 0) {
            return newInvoiceId;
        }
        return -1;
    }
    
    public boolean deleteInvoice(int id) {
        List<InvoiceItem> items = invoiceDAO.getInvoiceItems(id);

        for (InvoiceItem item : items) {
            try {
                Book book = bookDAO.getBookById(item.getBookId());
                
                if (book != null) {
                    book.setQuantity(book.getQuantity() + item.getQuantity());
                    bookDAO.updateBook(book);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }

        return invoiceDAO.deleteInvoice(id);
    }
}
