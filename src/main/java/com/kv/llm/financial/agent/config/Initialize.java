package com.kv.llm.financial.agent.config;


import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import com.kv.llm.financial.agent.exception.FinancialAgentConfigException;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.TokenCountEstimator;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Configuration
public class Initialize {
    @Value("${fmp.hostName:null}")
    private String hostname;


    @Bean
    public RestClient fmpClient() {
        return RestClient.builder().baseUrl(hostname).defaultHeader("Content-Type", "application/json").build();
    }

    @Bean
    public TokenCountEstimator tokenCountEstimator(@Value("${huggingface.access-token}") String apiToken) {
        // Initialize TokenCountEstimator with DJL HuggingFaceTokenizer
        TokenCountEstimator estimator = null;

        Map<String, String> token = Optional.ofNullable(apiToken).map(v -> Map.of("accessToken", apiToken)).orElseGet(() -> Map.of());
        log.info("API TOKEN : {}", token);
        try {
            HuggingFaceTokenizer hfTokenizer = null;
            try {
                hfTokenizer = HuggingFaceTokenizer.newInstance("meta-llama/Llama-2-7b",token);
            } catch (Exception e) {
                log.info(ExceptionUtils.getStackTrace(e));
                hfTokenizer = HuggingFaceTokenizer.newInstance("bert-base-uncased");
                log.info("Using bert-base-uncased tokenizer as fallback");
            }
            final HuggingFaceTokenizer tokenizer = hfTokenizer;
            estimator = new TokenCountEstimator() {
                @Override
                public int estimateTokenCountInText(String text) {
                    if (text == null || text.isEmpty()) {
                        log.info("Text is null or empty, returning 0 tokens");
                        return 0;
                    }
                    try {
                        int tokenCount = tokenizer.encode(text).getIds().length;
                        log.info("Tokenized text length: {}, tokens: {} ", text.length() + tokenCount);
                        return tokenCount;
                    } catch (Exception e) {
                        Throwable rootCause = ExceptionUtils.getRootCause(e);
                        log.error("Tokenization failed for text: '{}', error: {}",text,rootCause);
                        throw new FinancialAgentConfigException("Text tokenization error", rootCause);
                    }
                }

                @Override
                public int estimateTokenCountInMessage(ChatMessage message) {
                    if (message == null) {
                        System.out.println("Message is null, returning 0 tokens");
                        return 0;
                    }
                    String text = getMessage(message);

                    if (text == null || text.isEmpty()) {
                        System.out.println("Message text is null or empty, type: " + message.type() + ", returning 0 tokens");
                        return 0;
                    }
                    try {
                        int tokenCount = tokenizer.encode(text).getIds().length;
                        System.out.println("Tokenized message type: " + message.type() + ", text length: " + text.length() + ", tokens: " + tokenCount);
                        return tokenCount;
                    } catch (Exception e) {
                        Throwable rootCause = ExceptionUtils.getRootCause(e);
                        log.error("Tokenization failed for message type:  '{}', error: {}",text,rootCause);
                        throw new FinancialAgentConfigException("Message tokenization error", rootCause);
                    }
                }

                @Override
                public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
                    if (messages == null) {
                        System.out.println("Messages iterable is null, returning 0 tokens");
                        return 0;
                    }
                    int totalTokens = 0;
                    for (ChatMessage message : messages) {
                        totalTokens += estimateTokenCountInMessage(message);
                    }
                    log.info("Tokenized messages, total tokens: {}", totalTokens);
                    return totalTokens;
                }
            };
            log.info("Initialized DJL HuggingFaceTokenizer");
        } catch (Exception e) {
            log.error("Initialize DJL HuggingFaceTokenizer failed", ExceptionUtils.getRootCause(e));
            // Fallback to mock tokenizer
            estimator = new TokenCountEstimator() {
                @Override
                public int estimateTokenCountInText(String text) {
                    if (text == null || text.isEmpty()) {
                        log.info("Text is null or empty in mock tokenizer, returning 0 tokens");
                        return 0;
                    }
                    int wordCount = text.split("\\s+").length;
                    int estimatedTokens = (int) (wordCount * 1.33);
                    log.info("Mock tokenized text length: {} , words: {} , tokens: {} ", text.length() , wordCount,estimatedTokens);
                    return estimatedTokens;
                }

                @Override
                public int estimateTokenCountInMessage(ChatMessage message) {
                    if (message == null) {
                        log.info("Message is null in mock tokenizer, returning 0 tokens");
                        return 0;
                    }
                    String text = getMessage(message);

                    if (text == null || text.isEmpty()) {
                        log.info("Message text is null or empty in mock tokenizer, type: {}, Tokenized messages, total tokens: 0 ", message.type());
                        return 0;
                    }
                    return estimateTokenCountInText(text);
                }

                @Override
                public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
                    if (messages == null) {
                        log.info("Messages iterable is null in mock tokenizer, returning 0 tokens");
                        return 0;
                    }
                    int totalTokens = 0;
                    for (ChatMessage message : messages) {
                        totalTokens += estimateTokenCountInMessage(message);
                    }
                    log.info("Mock tokenized messages, total tokens: {}", totalTokens);
                    return totalTokens;
                }
            };
            log.info("Using mock tokenizer due to DJL failure");
        }
        return estimator;
    }


    private String getMessage(ChatMessage message) {
        String text = null;
        switch (message.type()) {
            case SYSTEM -> {
                if (message instanceof SystemMessage systemMessage) {
                    text = systemMessage.text();
                }
            }
            case USER -> {
                if (message instanceof UserMessage userMessage) {
                    List<Content> contents = userMessage.contents();
                    text = extract(contents);
                }
            }
            case AI -> {
                if (message instanceof AiMessage aiMessage) {
                    text = aiMessage.text();
                }
            }
            default -> log.info("Unsupported message type: {}", message.type());
        }
        return text;
    }

    private String extract(List<Content> contents){
        return Optional.ofNullable(contents).filter(l -> l != null && !l.isEmpty())
                .map(list -> list.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining()))
                .orElseGet(() -> null);
    }

}
