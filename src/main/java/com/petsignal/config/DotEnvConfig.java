package com.petsignal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DotEnvConfig {

    @Bean
    public String loadEnvVariables() {
        try {
            // Intentar cargar el archivo .env desde la raíz del proyecto
            Path envPath = Paths.get(".env");
            if (Files.exists(envPath)) {
                Map<String, String> envVars = parseEnvFile(envPath);
                envVars.forEach((key, value) -> {
                    if (System.getProperty(key) == null) {
                        System.setProperty(key, value);
                    }
                });
                
                // Imprimir variables para diagnóstico
                System.out.println("Variables cargadas desde .env:");
                envVars.forEach((key, value) -> 
                    System.out.println(key + "=" + value)
                );
                return "Variables de entorno cargadas correctamente";
            } else {
                System.err.println("Archivo .env no encontrado en la raíz del proyecto");
                return "Archivo .env no encontrado";
            }
        } catch (IOException e) {
            System.err.println("Error al cargar el archivo .env: " + e.getMessage());
            return "Error al cargar variables de entorno: " + e.getMessage();
        }
    }
    
    private Map<String, String> parseEnvFile(Path path) throws IOException {
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
