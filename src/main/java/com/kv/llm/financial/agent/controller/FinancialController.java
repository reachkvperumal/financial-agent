package com.kv.llm.financial.agent.controller;

import com.kv.llm.financial.agent.config.FinancialAgent;
import com.kv.llm.financial.agent.dto.UserReq;
import com.kv.llm.financial.agent.exception.FinancialAgentException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.StructuredTaskScope;

@RestController
@Log4j2
public class FinancialController {

    private final FinancialAgent financialAgent;

    public FinancialController(FinancialAgent financialAgent) {
        this.financialAgent = financialAgent;
    }

    @PostMapping("/analyze")
    public String analyzeStock(@Valid @RequestBody UserReq req) {
//         Remove any existing "for {symbol}" from query and append "for {symbol}"
      /*      String normalizedQuery = req.getQuery().replaceAll("(?i)\\s*for\\s+\\w+\\s*$", "").trim();
            String fullQuery = normalizedQuery + " for " + req.getSymbol();
        String resp = financialAgent.analyzeStock(fullQuery);
        log.info(resp);
        return resp;*/

       try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            String normalizedQuery = req.getQuery().replaceAll("(?i)\\s*for\\s+\\w+\\s*$", "").trim();
            String fullQuery = normalizedQuery + " for " + req.getSymbol();
            log.info("Submitting query: {}", fullQuery);
            StructuredTaskScope.Subtask<String> result = scope.fork(() -> financialAgent.analyzeStock(fullQuery));
            scope.join().throwIfFailed(FinancialAgentException::new);
            return result.get();
        } catch (Exception e) {
            log.error("Error analyzing stock for symbol: {}, query: {}", req.getSymbol(), req.getQuery(), e);
            return "Error analyzing stock: " + e.getMessage();
        }


    }
}
