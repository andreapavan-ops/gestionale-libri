package it.biblioteca.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("it.biblioteca")
public class BibliotecaWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(BibliotecaWebApplication.class, args);
    }
}
