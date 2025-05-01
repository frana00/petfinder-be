USE petsignaldb;

-- Load country data
LOAD DATA INFILE '/var/lib/mysql-files/countries.csv'
    INTO TABLE countries
    FIELDS TERMINATED BY ','
    ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (name, country_code);

-- Load Spain postal codes
LOAD DATA INFILE '/var/lib/mysql-files/postal_codes_ES_formatted.csv'
    INTO TABLE postal_codes
    FIELDS TERMINATED BY ','
    ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (country_code, postal_code);