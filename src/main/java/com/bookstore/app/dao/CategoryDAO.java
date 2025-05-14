package com.bookstore.app.dao;

import com.bookstore.app.model.Category;
import com.bookstore.app.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private static final String SELECT_ALL_CATEGORIES = "SELECT * FROM categories";
    private static final String SELECT_CATEGORY_BY_ID = "SELECT * FROM categories WHERE id = ?";
    private static final String INSERT_CATEGORY = "INSERT INTO categories (name, description) VALUES (?, ?)";
    private static final String UPDATE_CATEGORY = "UPDATE categories SET name = ?, description = ? WHERE id = ?";
    private static final String DELETE_CATEGORY = "DELETE FROM categories WHERE id = ?";
    private static final String SELECT_ALL_CATEGORY_NAMES = "SELECT name FROM categories";
    
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        
        try (
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_CATEGORIES)
        ) {
            while (resultSet.next()) {
                categories.add(new Category(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return categories;
    }
    
    public List<String> getAllCategoryNames() {
        List<String> categoryNames = new ArrayList<>();
        
        try (
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SELECT_ALL_CATEGORY_NAMES)
        ) {
            while (resultSet.next()) {
                categoryNames.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // If we can't get category names, at least provide some defaults
            categoryNames.add("Tiểu thuyết");
            categoryNames.add("Kỹ năng sống");
            categoryNames.add("Sách giáo khoa");
            categoryNames.add("Truyện tranh");
            categoryNames.add("Khoa học");
        }
        
        return categoryNames;
    }
    
    public Category getCategoryById(int id) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_CATEGORY_BY_ID)
        ) {
            preparedStatement.setInt(1, id);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Category(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean addCategory(Category category) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CATEGORY, Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        category.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean updateCategory(Category category) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CATEGORY)
        ) {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3, category.getId());
            
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean deleteCategory(int id) {
        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CATEGORY)
        ) {
            preparedStatement.setInt(1, id);
            
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
}
