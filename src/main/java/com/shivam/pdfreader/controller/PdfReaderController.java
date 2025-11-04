package com.shivam.pdfreader.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(PdfReaderController.class);
    
    private final LLMService llmService;

    public PdfReaderController(LLMService llmService) {
        this.llmService = llmService;
    }

    /**
     * ✅ Standard PDF Parsing API (Multipart Upload)
     */
    @PostMapping("/parse")
    public ResponseEntity<String> parsePdf(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Uploaded file is empty.");
            }

            Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
            file.transferTo(filePath.toFile());
            logger.info("Processing file: {}", filePath);

            // Extract text from PDF
            String text;
            try {
                text = PdfParser.extractText(filePath.toString());
            } catch (Exception ex) {
                logger.error("PDF Parsing Error", ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("PDF Parsing Error: " + ex.getMessage());
            }

            logger.info("Extracted text: {}", text);

            // Pass extracted text to LLM for processing
            String result = llmService.extractDataUsingLLM(text);
            logger.info("LLM Response: {}", result);
            return ResponseEntity.ok(result);
        } catch (IOException | RuntimeException e) {
            logger.error("Error processing PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing PDF: " + e.getClass().getName() + " - " + e.getMessage());
        }
    }

    /**
     * ✅ Secure PDF Parsing API with Password
     */
    @PostMapping("/parse-secure")
    public ResponseEntity<String> parsePdfWithPassword(
            @RequestParam("file") MultipartFile file,
            @RequestParam("firstname") String firstname,
            @RequestParam("dob") String dob) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Uploaded file is empty.");
            }

            String password = firstname.toLowerCase() + dob.replace("-", "");
            logger.info("Generated Password: {}", password);

            Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
            file.transferTo(filePath.toFile());

            String text = PdfParser.extractText(filePath.toString());
            String result = llmService.extractDataUsingLLM(text);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            logger.error("Error processing secure PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing PDF: " + e.getMessage());
        }
    }

    /**
     * ✅ Parse PDF from Disk (Pre-uploaded files in resources/pdf/)
     */
    @GetMapping("/parse-from-disk")
    public ResponseEntity<String> parseFromDisk(@RequestParam("filename") String filename) {
        try {
            Path filePath = Paths.get("src/main/resources/pdf/", filename);

            // Check if the file exists
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error: File not found in resources/pdf directory.");
            }

            String text = PdfParser.extractText(filePath.toString());
            String result = llmService.extractDataUsingLLM(text);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            logger.error("Error processing file from disk", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file from disk: " + e.getMessage());
        }
    }
}
