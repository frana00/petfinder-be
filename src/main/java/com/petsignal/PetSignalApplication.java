package com.petsignal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PetSignalApplication {

  public static void main(String[] args) {
    SpringApplication.run(PetSignalApplication.class, args);
  }

} 