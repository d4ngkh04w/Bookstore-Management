--CREATE DATABASE IF NOT EXISTS bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE bookstore;


CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL
);

CREATE TABLE IF NOT EXISTS customers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS invoices (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Thêm bảng users cho quản lý người dùng theo vai trò
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,    -- ADMIN hoặc STAFF
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS invoice_items (
    invoice_id INT,
    book_id INT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (invoice_id, book_id),
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- Thêm dữ liệu mẫu cho categories
INSERT IGNORE INTO categories (name, description) VALUES
('Văn học', 'Các tác phẩm văn học Việt Nam và quốc tế'),
('Kinh tế - Quản lý', 'Sách về kinh doanh, quản lý và kinh tế học'),
('Kỹ năng sống', 'Sách dạy về các kỹ năng sống và phát triển bản thân'),
('Tâm lý học', 'Sách về tâm lý con người và các vấn đề tâm lý'),
('Khoa học - Công nghệ', 'Sách về các chủ đề khoa học và công nghệ'),
('Tiểu thuyết', 'Các thể loại tiểu thuyết đa dạng'),
('Thiếu nhi', 'Sách dành cho trẻ em và thiếu niên'),
('Giáo dục', 'Sách giáo khoa và tài liệu học tập'),
('Lịch sử', 'Sách về lịch sử Việt Nam và thế giới'),
('Triết học', 'Sách về triết học đông tây'),
('Y học - Sức khỏe', 'Sách về y học và chăm sóc sức khỏe'),
('Ngoại ngữ', 'Sách học ngoại ngữ và từ điển');

INSERT IGNORE INTO users (username, password, full_name, role, active) VALUES
('admin', 'admin123', 'Administrator', 'ADMIN', true);

-- DROP DATABASE IF EXISTS bookstore;