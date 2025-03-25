package com.shivam.pdfreader.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    // ✅ Standard PDF Parsing API
    @PostMapping("/parse")
    public String parsePdf(@RequestParam("file") MultipartFile file) throws IOException {
        Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
        file.transferTo(filePath.toFile());

        String text = PdfParser.extractText(filePath.toString());
        return llmService.extractDataUsingLLM(text);
    }

    // ✅ Secure PDF Parsing with Password Generation
    @PostMapping("/parse-secure")
    public String parsePdfWithPassword(
            @RequestParam("file") MultipartFile file,
            @RequestParam("firstname") String firstname,
            @RequestParam("dob") String dob
    ) throws IOException {
        // Generate password dynamically
        String password = firstname.toLowerCase() + dob.replace("-", "");
        System.out.println("Generated Password: " + password);

        Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
        file.transferTo(filePath.toFile());

        String text = PdfParser.extractText(filePath.toString());
        return llmService.extractDataUsingLLM(text);
    }
}
