package com.bookstore.app.controller;

import com.bookstore.app.model.Customer;
import com.bookstore.app.service.CustomerService;

import java.util.List;

public class CustomerController {
    private final CustomerService customerService;
    
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }
    
    public Customer getCustomerById(int id) {
        return customerService.getCustomerById(id);
    }
    
    public boolean addCustomer(Customer customer) {
        return customerService.addCustomer(customer);
    }
    
    public boolean updateCustomer(Customer customer) {
        return customerService.updateCustomer(customer);
    }
    
    public boolean deleteCustomer(int id) {
        return customerService.deleteCustomer(id);
    }
    
    public boolean isValidCustomer(Customer customer) {
        return customerService.isValidCustomer(customer);
    }
}
