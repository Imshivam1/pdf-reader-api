package com.shivam.pdfreader.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LLMService {
    
    @Value("${openai.api.key}")
    private String apiKey;

    public String extractDataUsingLLM(String text) {
        try {
            OpenAiService service = new OpenAiService(apiKey);
            CompletionRequest request = CompletionRequest.builder()
                    .model("o3-mini")
                    .prompt("Extract Name, Email, Opening Balance, and Closing Balance from the CASA statement below. Respond in JSON format:\n\n" + text)
                    .maxTokens(200)
                    .build();
            CompletionResult result = service.createCompletion(request);
            return result.getChoices().get(0).getText();
        } catch (Exception e) {
            return "{\"error\": \"Failed to extract data. Try again.\"}";
        }
    }
}