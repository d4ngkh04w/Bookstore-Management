package com.bookstore.app;

import com.bookstore.app.service.BookService;
import com.bookstore.app.service.CustomerService;
import com.bookstore.app.service.InvoiceService;
import com.bookstore.app.service.UserService;
import com.bookstore.app.util.DatabaseInitializer;
import com.bookstore.app.view.LoginView;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Initialize the database first
        DatabaseInitializer.initializeDatabase();
        
        SwingUtilities.invokeLater(() -> {            try {
                // Set look and feel to system default
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Set system properties for UTF-8 encoding
                System.setProperty("file.encoding", "UTF-8");
                System.setProperty("sun.jnu.encoding", "UTF-8");
                
                // Set font for Vietnamese
                Font defaultFont = new Font("Arial", Font.PLAIN, 14);
                UIManager.put("Button.font", defaultFont);
                UIManager.put("Label.font", defaultFont);
                UIManager.put("TextField.font", defaultFont);
                UIManager.put("TextArea.font", defaultFont);
                UIManager.put("Table.font", defaultFont);
                UIManager.put("TableHeader.font", new Font("Arial", Font.BOLD, 14));
                UIManager.put("ComboBox.font", defaultFont);
                UIManager.put("OptionPane.messageFont", defaultFont);
                UIManager.put("OptionPane.buttonFont", defaultFont);
                UIManager.put("Menu.font", defaultFont);
                UIManager.put("MenuItem.font", defaultFont);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
              // Initialize services
            BookService.getInstance();
            CustomerService.getInstance();
            InvoiceService.getInstance();
            UserService.getInstance();
            
            // Create the login view
            LoginView loginView = new LoginView();
            
            // Set application icon
            try {
                Image appIcon = new ImageIcon(
                    Main.class.getClassLoader().getResource("icons/logo.png")
                ).getImage();
                loginView.setIconImage(appIcon);
            } catch (Exception e) {
                System.out.println("App icon not found");
            }
              // Show the login form
            loginView.setVisible(true);
        });
    }
}
