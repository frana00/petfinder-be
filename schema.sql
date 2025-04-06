-- Database schema for PetSignal application

CREATE TABLE countries (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    subscription_email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    role ENUM('ADMIN', 'USER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE alerts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type ENUM('LOST', 'FOUND') NOT NULL,
    status ENUM('ACTIVE', 'RESOLVED') NOT NULL,
    chip_number VARCHAR(50) COMMENT 'Chip number of the pet',
    sex ENUM('MALE', 'FEMALE', 'UNKNOWN'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date TIMESTAMP NOT NULL,
    description TEXT,
    breed VARCHAR(100),
    cp VARCHAR(10) NOT NULL COMMENT 'zip code where last seen',
    country_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (country_id) REFERENCES countries(id)
);

CREATE TABLE photos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    alert_id INT NOT NULL,
    url VARCHAR(255) NOT NULL UNIQUE,
    FOREIGN KEY (alert_id) REFERENCES alerts(id)
);

CREATE TABLE subscriptions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    cp VARCHAR(10) NOT NULL,
    country_id INT NOT NULL,
    alert_type ENUM('LOST', 'FOUND') NOT NULL,
    type ENUM('EMAIL', 'SMS') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (country_id) REFERENCES countries(id)
);

CREATE TABLE notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    alert_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type ENUM('EMAIL', 'SMS') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (alert_id) REFERENCES alerts(id)
);

CREATE TABLE email_notifications (
    notification_id INT PRIMARY KEY,
    status ENUM('PENDING', 'SENT', 'FAILED') NOT NULL,
    `to` VARCHAR(254) NOT NULL,
    subject VARCHAR(100) NOT NULL,
    body TEXT NOT NULL,
    FOREIGN KEY (notification_id) REFERENCES notifications(id)
);

CREATE TABLE sms_notifications (
    notification_id INT PRIMARY KEY,
    status ENUM('PENDING', 'SENT', 'FAILED') NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    text TEXT NOT NULL,
    FOREIGN KEY (notification_id) REFERENCES notifications(id)
);

-- Indexes for better query performance
CREATE INDEX idx_alerts_user ON alerts(user_id);
CREATE INDEX idx_alerts_date ON alerts(date);
CREATE INDEX idx_alerts_country ON alerts(country_id);
CREATE INDEX idx_photos_alert ON photos(alert_id);
CREATE INDEX idx_subscriptions_user ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_country ON subscriptions(country_id);
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_alert ON notifications(alert_id); 