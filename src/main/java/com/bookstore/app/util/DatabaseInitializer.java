package com.bookstore.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp tiện ích để khởi tạo cơ sở dữ liệu
 */
public class DatabaseInitializer {
    
    /**
     * Khởi tạo cơ sở dữ liệu bằng cách thực thi script SQL
     */
    public static void initializeDatabase() {
        // Chạy script cài đặt chứa tất cả các bảng
        runScript("database/init.sql");
        
        System.out.println("Database initialized with complete schema");
    }
    
    /**
     * Thực thi một script SQL từ file
     * 
     * @param scriptPath đường dẫn đến file script trong resources
     */    private static void runScript(String scriptPath) {
        try {
            InputStream inputStream = DatabaseInitializer.class.getClassLoader().getResourceAsStream(scriptPath);
            if (inputStream == null) {
                System.err.println("Could not find script file: " + scriptPath);
                return;
            }
            
            // Đọc nội dung file SQL
            String sqlScript = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining("\n"));
                    
            // Tách script thành các lệnh SQL riêng lẻ
            List<String> statements = parseSqlStatements(sqlScript);
            
            // Thực thi từng câu lệnh SQL
            executeStatements(statements);
            
        } catch (Exception e) {
            System.err.println("Error processing SQL script: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Phân tích cú pháp của script SQL và trả về danh sách các câu lệnh SQL
     * 
     * @param sqlScript Script SQL cần phân tích
     * @return Danh sách các câu lệnh SQL đã tách
     */
    private static List<String> parseSqlStatements(String sqlScript) {
        List<String> statements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();
        
        boolean inMultiLineComment = false;
        
        for (String line : sqlScript.split("\n")) {
            // Bỏ qua dòng trống
            if (line.trim().isEmpty()) {
                continue;
            }

            // Xử lý các comment kiểu /* */
            if (inMultiLineComment) {
                if (line.contains("*/")) {
                    inMultiLineComment = false;
                    line = line.substring(line.indexOf("*/") + 2);
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            
            if (line.contains("/*")) {
                if (line.contains("*/")) {
                    line = line.substring(0, line.indexOf("/*")) + 
                          line.substring(line.indexOf("*/") + 2);
                } else {
                    line = line.substring(0, line.indexOf("/*"));
                    inMultiLineComment = true;
                }
                if (line.trim().isEmpty()) {
                    continue;
                }
            }
            
            // Xử lý các dòng comment SQL
            String processedLine = line;
            if (line.contains("--")) {
                // Loại bỏ phần comment nếu -- không nằm trong chuỗi nháy đơn hoặc kép
                int commentPos = -1;
                boolean inQuote = false;
                for (int i = 0; i < line.length() - 1; i++) {
                    if (line.charAt(i) == '\'' || line.charAt(i) == '"') {
                        inQuote = !inQuote;
                    } else if (line.charAt(i) == '-' && line.charAt(i + 1) == '-' && !inQuote) {
                        commentPos = i;
                        break;
                    }
                }
                
                if (commentPos >= 0) {
                    processedLine = line.substring(0, commentPos).trim();
                    if (processedLine.isEmpty()) {
                        continue;
                    }
                }
            }
            
            // Bỏ qua các dòng comment kiểu #
            if (processedLine.trim().startsWith("#")) {
                continue;
            }
            
            currentStatement.append(processedLine).append(" ");
            
            // Khi gặp dấu ; kết thúc câu lệnh, lưu câu lệnh vào danh sách
            if (processedLine.trim().endsWith(";")) {
                statements.add(currentStatement.toString());
                currentStatement = new StringBuilder();
            }
        }
        
        // Xử lý trường hợp câu lệnh SQL cuối cùng không có dấu chấm phẩy
        if (currentStatement.length() > 0) {
            String lastStatement = currentStatement.toString().trim();
            if (!lastStatement.isEmpty()) {
                statements.add(lastStatement + ";");
            }
        }
        
        return statements;
    }
    
    /**
     * Thực thi danh sách các câu lệnh SQL
     * 
     * @param statements Danh sách các câu lệnh SQL cần thực thi
     */
    private static void executeStatements(List<String> statements) {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                System.err.println("Failed to establish database connection");
                return;
            }
            
            try (Statement statement = connection.createStatement()) {
                for (String sql : statements) {
                    if (!sql.trim().isEmpty()) {
                        try {
                            statement.execute(sql);
                        } catch (SQLException e) {
                            // Ghi log lỗi nhưng vẫn tiếp tục thực thi các câu lệnh khác
                            // Bỏ qua lỗi duplicate entry vì có thể là do chạy lại script nhiều lần
                            if (e.getMessage().contains("Duplicate entry") && sql.contains("INSERT")) {
                                System.out.println("Ignoring duplicate entry: " + sql);
                            } else {
                                System.err.println("Error executing SQL: " + sql);
                                System.err.println("Error message: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
