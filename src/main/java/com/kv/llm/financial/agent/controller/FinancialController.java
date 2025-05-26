package com.kv.llm.financial.agent.controller;

import com.kv.llm.financial.agent.config.FinancialAgent;
import com.kv.llm.financial.agent.dto.UserReq;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class FinancialController {

    private final FinancialAgent financialAgent;

    public FinancialController(FinancialAgent financialAgent) {
        this.financialAgent = financialAgent;
    }

    @PostMapping("/analyze")
    public String analyzeStock(@Valid @RequestBody UserReq req) {
        //  try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        // Remove any existing "for {symbol}" from query and append "for {symbol}"
        String normalizedQuery = req.getQuery().replaceAll("(?i)\\s*for\\s+\\w+\\s*$", "").trim();
        String fullQuery = normalizedQuery + " for " + req.getSymbol();
        return financialAgent.analyzeStock(fullQuery);
        //    return scope.fork(() -> financialAgent.analyzeStock(fullQuery)).get();
        //} catch (Exception e) {
        //  return "Error analyzing stock: " + e.getMessage();
        //}
    }
}
