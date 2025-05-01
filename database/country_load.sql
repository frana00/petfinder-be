use petsignal;

-- Load country data
LOAD DATA LOCAL INFILE '/home/hannah/Desktop/UNIR/GII/TFG/countries.csv'
    INTO TABLE countries
    FIELDS TERMINATED BY ','
    ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (name, country_code);

-- load Spain postal codes
LOAD DATA LOCAL INFILE '/home/hannah/Desktop/UNIR/GII/TFG/postal_codes_ES_formatted.csv'
    INTO TABLE postal_codes
    FIELDS TERMINATED BY ','
    ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (country_code, postal_code);