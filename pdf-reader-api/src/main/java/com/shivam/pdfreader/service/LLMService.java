package com.shivam.pdfreader.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LLMService {
    
    @Value("${openai.api.key}")
    private String apiKey;

    public String extractDataUsingLLM(String text) {
        OpenAiService service = new OpenAiService(apiKey);
        CompletionRequest request = CompletionRequest.builder()
                .model("gpt-4-turbo")
                .prompt("Extract Name, Email, Opening Balance, and Closing Balance from the following CASA statement:\n\n" + text)
                .maxTokens(200)
                .build();

        CompletionResult result = service.createCompletion(request);
        return result.getChoices().get(0).getText();
    }
}
