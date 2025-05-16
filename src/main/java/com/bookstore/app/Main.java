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
        // Khởi tạo db
        DatabaseInitializer.initializeDatabase();
        
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                System.setProperty("file.encoding", "UTF-8");
                System.setProperty("sun.jnu.encoding", "UTF-8");

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
                System.out.println(e.getMessage());
            }

            BookService.getInstance();
            CustomerService.getInstance();
            InvoiceService.getInstance();
            UserService.getInstance();

            LoginView loginView = new LoginView();

            try {
                Image appIcon = new ImageIcon(
                    Main.class.getClassLoader().getResource("icons/logo.png")
                ).getImage();
                loginView.setIconImage(appIcon);
            } catch (Exception e) {
                System.out.println("App icon not found");
            }
            
            loginView.setVisible(true);
            loginView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}
