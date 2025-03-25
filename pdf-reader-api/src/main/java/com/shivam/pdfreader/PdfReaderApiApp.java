package com.shivam.pdfreader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PdfReaderApiApp {
    public static void main(String[] args) {
        SpringApplication.run(PdfReaderApiApp.class, args);
        System.out.println("🚀 PDF Reader API is running on http://localhost:8080");
    }
}
