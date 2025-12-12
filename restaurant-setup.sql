DROP DATABASE restaurant;
CREATE DATABASE restaurant;
USE restaurant;

CREATE TABLE IF NOT EXISTS Chefs (
	id INT AUTO_INCREMENT PRIMARY KEY,
    fullname VARCHAR(256) NOT NULL,
    username VARCHAR(256) NOT NULL,
    password VARCHAR(256) NOT NULL,
    fired BOOLEAN NOT NULL DEFAULT FALSE,
    salary FLOAT NOT NULL DEFAULT 3000,
    warnings INT NOT NULL DEFAULT 0,
    bonuses INT NOT NULL DEFAULT 0,
    demotions INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS PendingChefs (
    fullname VARCHAR(256) NOT NULL,
    username VARCHAR(256) NOT NULL,
    password VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS Deliverers (
	id INT AUTO_INCREMENT PRIMARY KEY,
    fullname VARCHAR(256) NOT NULL,
    username VARCHAR(256) NOT NULL,
    password VARCHAR(256) NOT NULL,
    fired BOOLEAN NOT NULL DEFAULT FALSE,
    salary FLOAT NOT NULL DEFAULT 1500,
    warnings INT NOT NULL DEFAULT 0,
    bonuses INT NOT NULL DEFAULT 0,
    demotions INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS PendingDeliverers (
    fullname VARCHAR(256) NOT NULL,
    username VARCHAR(256) NOT NULL,
    password VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS Customers (
	id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(256) NOT NULL,
    password VARCHAR(256) NOT NULL,
    vip BOOLEAN NOT NULL DEFAULT FALSE,
    total_spent FLOAT NOT NULL DEFAULT 0,
    orders INT NOT NULL DEFAULT 0,
    orders_since_last_free_delivery INT NOT NULL DEFAULT 0,
    free_delivery_credits INT NOT NULL DEFAULT 0,
    warnings INT NOT NULL DEFAULT 0,
    banned BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS PendingCustomers (
	username VARCHAR(256) NOT NULL,
    password VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS Managers (
	id INT AUTO_INCREMENT PRIMARY KEY,
    fullname VARCHAR(256) NOT NULL,
    username VARCHAR(256) NOT NULL,
    password VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS MenuItems (
	id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    price FLOAT NOT NULL DEFAULT 0,
    vip BOOLEAN NOT NULL DEFAULT FALSE,
    orders INT NOT NULL DEFAULT 0,
    chef_id INT NOT NULL,
    FOREIGN KEY (chef_id) REFERENCES Chefs(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Orders (
	id INT AUTO_INCREMENT PRIMARY KEY,
    price FLOAT NOT NULL,
    customer_id INT NOT NULL,
    chef_id INT NOT NULL,
    deliverer_id INT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES Customers(id) ON DELETE CASCADE,
    FOREIGN KEY (chef_id) REFERENCES Chefs(id) ON DELETE CASCADE,
    FOREIGN KEY (deliverer_id) REFERENCES Deliverers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS OrderContents (
	order_id INT NOT NULL,
    item_id INT NOT NULL,
	FOREIGN KEY (order_id) REFERENCES Orders(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES MenuItems(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS MenuRatings (
	id INT AUTO_INCREMENT PRIMARY KEY,
    rating INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    menu_id INT NOT NULL,
    FOREIGN KEY (menu_id) REFERENCES MenuItems(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ChefRatings (
	id INT AUTO_INCREMENT PRIMARY KEY,
    rating INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    chef_id INT NOT NULL,
    customer_id INT NOT NULL,
	FOREIGN KEY (chef_id) REFERENCES Chefs(id) ON DELETE CASCADE,
	FOREIGN KEY (customer_id) REFERENCES Customers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ChefComplaints (
	id INT AUTO_INCREMENT PRIMARY KEY,
    message TEXT,
    weight INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    chef_id INT NOT NULL,
    customer_id INT NOT NULL,
	FOREIGN KEY (chef_id) REFERENCES Chefs(id) ON DELETE CASCADE,
	FOREIGN KEY (customer_id) REFERENCES Customers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ChefCompliments (
	id INT AUTO_INCREMENT PRIMARY KEY,
    message TEXT,
    weight INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    chef_id INT NOT NULL,
    customer_id INT NOT NULL,
	FOREIGN KEY (chef_id) REFERENCES Chefs(id) ON DELETE CASCADE,
	FOREIGN KEY (customer_id) REFERENCES Customers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS DelivererRatings (
	id INT AUTO_INCREMENT PRIMARY KEY,
    rating INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deliverer_id INT NOT NULL,
    customer_id INT NOT NULL,
	FOREIGN KEY (deliverer_id) REFERENCES Deliverers(id) ON DELETE CASCADE,
	FOREIGN KEY (customer_id) REFERENCES Customers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS DelivererComplaints (
	id INT AUTO_INCREMENT PRIMARY KEY,
    message TEXT,
    weight INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deliverer_id INT NOT NULL,
    customer_id INT NOT NULL,
	FOREIGN KEY (deliverer_id) REFERENCES Deliverers(id) ON DELETE CASCADE,
	FOREIGN KEY (customer_id) REFERENCES Customers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS DelivererCompliments (
	id INT AUTO_INCREMENT PRIMARY KEY,
    message TEXT,
    weight INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deliverer_id INT NOT NULL,
    customer_id INT NOT NULL,
	FOREIGN KEY (deliverer_id) REFERENCES Deliverers(id) ON DELETE CASCADE,
	FOREIGN KEY (customer_id) REFERENCES Customers(id) ON DELETE CASCADE
);

INSERT IGNORE INTO Chefs (fullname, username, password)
VALUES
	("John Cook", "_j_the_cook", "password1"),
    ("Jimmy Soup", "souplover123", "password2"),
    ("Bill Brioche", "b_brioche_", "password3");

INSERT IGNORE INTO Deliverers (fullname, username, password)
VALUES
	("James Drive", "idrive2009", "password1"),
    ("Brass Tax", "TaxiMan", "password2"),
    ("Michael Bichael", "BikeMike91", "password3");

INSERT IGNORE INTO Customers (username, password)
VALUES
	("_john_doe_123", "pass");

INSERT IGNORE INTO Managers (fullname, username, password)
VALUES
	("Mark Ingo Wager", "minwage", "12345");