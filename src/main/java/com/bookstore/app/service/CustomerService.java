package com.bookstore.app.service;

import com.bookstore.app.dao.CustomerDAO;
import com.bookstore.app.model.Customer;

import java.util.List;

public class CustomerService {
    private static CustomerService instance;
    private final CustomerDAO customerDAO;
    
    public CustomerService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }
    
    public static CustomerService getInstance() {
        if (instance == null) {
            instance = new CustomerService(new CustomerDAO());
        }
        return instance;
    }
    
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }
    
    public Customer getCustomerById(int id) {
        return customerDAO.getCustomerById(id);
    }
    
    public boolean addCustomer(Customer customer) {
        return customerDAO.addCustomer(customer);
    }
    
    public boolean updateCustomer(Customer customer) {
        return customerDAO.updateCustomer(customer);
    }
    
    public boolean deleteCustomer(int id) {
        return customerDAO.deleteCustomer(id);
    }
    
    public boolean isValidCustomer(Customer customer) {
        return customer != null &&
               customer.getName() != null && !customer.getName().trim().isEmpty() &&
               customer.getPhone() != null && !customer.getPhone().trim().isEmpty();
    }
}
