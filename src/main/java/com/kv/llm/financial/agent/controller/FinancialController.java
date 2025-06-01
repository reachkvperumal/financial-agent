package com.kv.llm.financial.agent.controller;

import com.kv.llm.financial.agent.config.FinancialAgent;
import com.kv.llm.financial.agent.dto.UserReq;
import com.kv.llm.financial.agent.exception.FinancialAgentException;
import dev.langchain4j.model.TokenCountEstimator;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.StructuredTaskScope;

@RestController
@Log4j2
public class FinancialController {

    private final FinancialAgent financialAgent;

    private final TokenCountEstimator tokenCountEstimator;

    private final String systemMessage;

    public FinancialController(FinancialAgent financialAgent, TokenCountEstimator tokenCountEstimator,
                               @Value("${ai.system-message}") String systemMessage) {
        this.financialAgent = financialAgent;
        // Initialize HuggingFaceTokenizer as TokenCountEstimator
        this.tokenCountEstimator = tokenCountEstimator;
        this.systemMessage = systemMessage;
    }

    @PostMapping(value = "/analyze", produces = MediaType.APPLICATION_JSON_VALUE)
    public String analyzeStock(@Valid @RequestBody UserReq req) {
//         Remove any existing "for {symbol}" from query and append "for {symbol}"
      /*      String normalizedQuery = req.getQuery().replaceAll("(?i)\\s*for\\s+\\w+\\s*$", "").trim();
            String fullQuery = normalizedQuery + " for " + req.getSymbol();
        String resp = financialAgent.analyzeStock(fullQuery);
        log.info(resp);
        return resp;*/
        log.info(systemMessage);
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            String normalizedQuery = req.getQuery().replaceAll("(?i)\\s*for\\s+\\w+\\s*$", "").trim();
            String fullQuery = normalizedQuery + " for " + req.getSymbol();

            int systemTokens = tokenCountEstimator.estimateTokenCountInText(systemMessage);
            int queryTokens = tokenCountEstimator.estimateTokenCountInText(fullQuery);
            int memoryTokens = 1000; // Fixed memory estimate
            int inputTokens = systemTokens + queryTokens + memoryTokens + 50; // +50 for tool metadata
            log.info("System Tokens: {}", systemTokens);
            log.info("Query Tokens: {}", queryTokens);
            log.info("Estimated Input Tokens: {}",inputTokens);

            StructuredTaskScope.Subtask<String> result = scope.fork(() -> financialAgent.analyzeStock(fullQuery));
            scope.join().throwIfFailed(FinancialAgentException::new);
            return result.get();
        } catch (Exception e) {
            log.error("Error analyzing stock for symbol: {}, query: {}", req.getSymbol(), req.getQuery(), e);
            return "Error analyzing stock: " + e.getMessage();
        }
    }
}
