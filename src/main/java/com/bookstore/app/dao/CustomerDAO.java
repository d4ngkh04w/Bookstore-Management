package com.bookstore.app.dao;

import com.bookstore.app.model.Customer;
import com.bookstore.app.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private static final String SELECT_ALL_CUSTOMERS = "SELECT * FROM customers";
    private static final String SELECT_CUSTOMER_BY_ID = "SELECT * FROM customers WHERE id = ?";
    private static final String INSERT_CUSTOMER = "INSERT INTO customers (name, phone) VALUES (?, ?)";
    private static final String UPDATE_CUSTOMER = "UPDATE customers SET name = ?, phone = ? WHERE id = ?";
    private static final String DELETE_CUSTOMER = "DELETE FROM customers WHERE id = ?";
    
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        
        try (
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_CUSTOMERS)
        ) {
            while (resultSet.next()) {
                customers.add(new Customer(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("phone")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
    
    public Customer getCustomerById(int id) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CUSTOMER_BY_ID)
        ) {
            preparedStatement.setInt(1, id);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Customer(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("phone")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addCustomer(Customer customer) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CUSTOMER, Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getPhone());
            
            int affectedRows = preparedStatement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateCustomer(Customer customer) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CUSTOMER)
        ) {
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getPhone());
            preparedStatement.setInt(3, customer.getId());
            
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteCustomer(int id) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CUSTOMER)
        ) {
            preparedStatement.setInt(1, id);
            
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
