-- Database schema for PetSignal application
CREATE TABLE countries (
    country_code VARCHAR(2) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE postal_codes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    postal_code VARCHAR(10) NOT NULL,
    country_code VARCHAR(2) NOT NULL,
    FOREIGN KEY (country_code) REFERENCES countries(country_code)
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    subscription_email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    role ENUM('ADMIN', 'USER') NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE alerts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    postal_code_id BIGINT NOT NULL,
    type ENUM('LOST', 'SEEN') NOT NULL,
    status ENUM('ACTIVE', 'RESOLVED') NOT NULL,
    chip_number VARCHAR(50) COMMENT 'Chip number of the pet',
    sex ENUM('MALE', 'FEMALE', 'UNKNOWN'),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    date TIMESTAMP NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    breed VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (postal_code_id) REFERENCES postal_codes(id)
);

CREATE TABLE photos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    alert_id BIGINT NOT NULL,
    url VARCHAR(255) NOT NULL UNIQUE,
    FOREIGN KEY (alert_id) REFERENCES alerts(id)
);

CREATE TABLE subscriptions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    country_code VARCHAR(2) NOT NULL,
    alert_type ENUM('LOST', 'FOUND') NOT NULL,
    type ENUM('EMAIL', 'SMS') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (country_code) REFERENCES countries(country_code)
);

CREATE TABLE subscriptions_postal_codes (
	subscription_id BIGINT NOT NULL,
	postal_code_id BIGINT NOT NULL,
	PRIMARY KEY (postal_code_id, subscription_id),
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id),
    FOREIGN KEY (postal_code_id) REFERENCES postal_codes(id)
);

CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    alert_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    type ENUM('EMAIL', 'SMS') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (alert_id) REFERENCES alerts(id)
);

CREATE TABLE email_notifications (
    notification_id BIGINT PRIMARY KEY,
    status ENUM('PENDING', 'SENT', 'FAILED') NOT NULL,
    `to` VARCHAR(254) NOT NULL,
    subject VARCHAR(100) NOT NULL,
    body TEXT NOT NULL,
    FOREIGN KEY (notification_id) REFERENCES notifications(id)
);

CREATE TABLE sms_notifications (
    notification_id BIGINT PRIMARY KEY,
    status ENUM('PENDING', 'SENT', 'FAILED') NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    text TEXT NOT NULL,
    FOREIGN KEY (notification_id) REFERENCES notifications(id)
);

CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    alert_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (alert_id) REFERENCES alerts(id)
);

-- Indexes for better query performance
CREATE INDEX idx_alerts_user ON alerts(user_id);
CREATE INDEX idx_alerts_date ON alerts(date);
CREATE INDEX idx_alerts_postal_code ON alerts(postal_code_id);
CREATE INDEX idx_photos_alert ON photos(alert_id);
CREATE INDEX idx_subscriptions_user ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_country ON subscriptions(country_code);
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_alert ON notifications(alert_id);
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_posts_alert_id ON posts(alert_id);
CREATE INDEX idx_posts_created_at ON posts(created_at);-- DROP TABLE subscriptions;
