package com.shivam.pdfreader.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shivam.pdfreader.service.LLMService;
import com.shivam.pdfreader.util.PdfParser;

@RestController
@RequestMapping("/api/pdf")
public class PdfReaderController {

    private final LLMService llmService;

    public PdfReaderController(LLMService llmService) {
        this.llmService = llmService;
    }

    // PDF Parsing API (Multipart Upload)
    @PostMapping("/parse")
public ResponseEntity<String> parsePdf(@RequestParam("file") MultipartFile file) {
    try {
        Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
        file.transferTo(filePath.toFile());

        System.out.println("Processing file: " + filePath.toString());

        String text;
try {
    text = PdfParser.extractText(filePath.toString());
} catch (Exception ex) {
    ex.printStackTrace();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                         .body("PDF Parsing Error: " + ex.getMessage());
}

        System.out.println("Extracted text: " + text);

        String result = llmService.extractDataUsingLLM(text);
        System.out.println("LLM Response: " + result);
        return ResponseEntity.ok(result);
    } catch (IOException | RuntimeException e) {
        e.printStackTrace(); // Keep this to log errors in the console
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Error processing PDF: " + e.getClass().getName() + " - " + e.getMessage());
    }
    
}



    // Secure PDF Parsing with Password
    @PostMapping("/parse-secure")
    public String parsePdfWithPassword(
            @RequestParam("file") MultipartFile file,
            @RequestParam("firstname") String firstname,
            @RequestParam("dob") String dob
    ) throws IOException {
        String password = firstname.toLowerCase() + dob.replace("-", "");
        System.out.println("Generated Password: " + password);

        Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
        file.transferTo(filePath.toFile());

        String text = PdfParser.extractText(filePath.toString());
        return llmService.extractDataUsingLLM(text);
    }

    // 
    @GetMapping("/parse-from-disk")
    public String parseFromDisk(@RequestParam("filename") String filename) throws IOException {
        Path filePath = Paths.get("src/main/resources/pdf/", filename);

        //Check if file exists
        if (!Files.exists(filePath)) {
            return "Error: File not found in resources/pdf directory.";
        }

        String text = PdfParser.extractText(filePath.toString());
        return llmService.extractDataUsingLLM(text);
    }
}
