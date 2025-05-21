package com.petsignal;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class PetSignalApplication {

  public static void main(String[] args) {
    // Cargar variables de entorno desde el archivo .env
    loadEnvVariables();
    
    // Imprimir variables de entorno para diagnóstico
    System.out.println("DB_URL from System.getenv(): " + System.getenv("DB_URL"));
    System.out.println("DB_USERNAME from System.getenv(): " + System.getenv("DB_USERNAME"));
    System.out.println("DB_URL from System.getProperty(): " + System.getProperty("DB_URL"));
    System.out.println("DB_USERNAME from System.getProperty(): " + System.getProperty("DB_USERNAME"));
    
    SpringApplication.run(PetSignalApplication.class, args);
  }
  
  private static void loadEnvVariables() {
    try {
      // Intentar cargar el archivo .env desde la raíz del proyecto
      Path envPath = Paths.get(".env");
      if (Files.exists(envPath)) {
        Map<String, String> envVars = parseEnvFile(envPath);
        envVars.forEach((key, value) -> {
          System.setProperty(key, value);
        });
        
        // Imprimir variables para diagnóstico
        System.out.println("Variables cargadas desde .env:");
        envVars.forEach((key, value) -> 
          System.out.println(key + "=" + value)
        );
      } else {
        System.err.println("Archivo .env no encontrado en la raíz del proyecto");
      }
    } catch (IOException e) {
      System.err.println("Error al cargar el archivo .env: " + e.getMessage());
    }
  }
  
  private static Map<String, String> parseEnvFile(Path path) throws IOException {
    Map<String, String> envVars = new HashMap<>();
    
    try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (!line.isEmpty() && !line.startsWith("#")) {
          int delimiterPos = line.indexOf('=');
          if (delimiterPos > 0) {
            String key = line.substring(0, delimiterPos).trim();
            String value = line.substring(delimiterPos + 1).trim();
            // Eliminar comillas si existen
            if (value.startsWith("\"") && value.endsWith("\"")) {
              value = value.substring(1, value.length() - 1);
            }
            envVars.put(key, value);
          }
        }
      }
    }
    
    return envVars;
  }
} 